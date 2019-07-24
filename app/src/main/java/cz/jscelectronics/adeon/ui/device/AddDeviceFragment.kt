package cz.jscelectronics.adeon.ui.device

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.telephony.TelephonyManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import com.google.android.material.snackbar.Snackbar
import cz.jscelectronics.adeon.R
import cz.jscelectronics.adeon.adapters.RecyclerAttributeTouchHelper
import cz.jscelectronics.adeon.data.Device
import cz.jscelectronics.adeon.databinding.FragmentAddDeviceBinding
import cz.jscelectronics.adeon.ui.device.dialogs.ImageCaptureDialogFragment
import cz.jscelectronics.adeon.ui.device.viewmodels.ManageDeviceViewModel
import cz.jscelectronics.adeon.utilities.InjectorUtils
import cz.jscelectronics.adeon.utilities.hideSoftKeyboard
import java.util.*


class AddDeviceFragment : Fragment(), ImageCaptureDialogFragment.ImageCaptureDialogListener {
    companion object {
        const val REQUEST_TAKE_PHOTO = 1
        const val REQUEST_GALLERY_IMAGE = 2
    }

    private lateinit var layout: CoordinatorLayout
    private lateinit var manageDeviceViewModel: ManageDeviceViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var deviceId: Long? = null
        if (arguments!!.size() > 0) {
            deviceId = AddDeviceFragmentArgs.fromBundle(arguments!!).deviceId
        }

        val factory = InjectorUtils.provideManageDeviceViewModelFactory(requireActivity(), deviceId)
        manageDeviceViewModel = ViewModelProviders.of(this, factory).get(ManageDeviceViewModel::class.java)

        val binding = FragmentAddDeviceBinding.inflate(inflater, container, false).apply {
            viewModel = manageDeviceViewModel
            lifecycleOwner = this@AddDeviceFragment

            phoneNumber.setHint(R.string.phone_hint)

            context?.apply {
                val telephonyMgr = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                var country = telephonyMgr.networkCountryIso

                // Fallback to locale based country detection if no SIM is inserted
                if (country.isNullOrEmpty()) {
                    country = Locale.getDefault().country
                }

                phoneNumber.defaultCountry = country
            }

            fab.setOnClickListener {
                manageDeviceViewModel.addNewAttribute()
            }

            addDeviceButton.setOnClickListener { view ->
                var isError = false

                if (deviceName.editText!!.text.isNullOrEmpty()) {
                    isError = true
                    deviceName.error = getString(R.string.invalid_device_name)
                } else {
                    deviceName.error = null
                }

                if (phoneNumber.isValid) {
                    phoneNumber.setError(null)
                } else {
                    isError = true
                    phoneNumber.setError(getString(R.string.invalid_phone_number))
                }

                if (manageDeviceViewModel.isAttributeListEmpty()) {
                    isError = true
                    Snackbar.make(view, R.string.no_defined_command_error, Snackbar.LENGTH_LONG).show()
                }

                if (!manageDeviceViewModel.isAttributeListValid()) {
                    isError = true
                    Snackbar.make(view, R.string.invalid_defined_commands, Snackbar.LENGTH_LONG).show()
                }

                if (!isError) {
                    manageDeviceViewModel.addOrUpdateDevice()
                    view.hideSoftKeyboard()
                    val direction =
                        AddDeviceFragmentDirections.actionAddDeviceFragmentToDeviceListFragment()
                    view.findNavController().navigate(direction)
                }
            }
        }

        binding.deviceImage.setOnClickListener {
            this.fragmentManager?.let {
                ImageCaptureDialogFragment(this).show(it, "Image Select Dialog")
            }
        }

        layout = binding.addDeviceLayout

        val adapter = manageDeviceViewModel.getAttributesAdapter(isEditMode = true)
        binding.attributeList.adapter = adapter
        ItemTouchHelper(
            RecyclerAttributeTouchHelper(
                ItemTouchHelper.UP or ItemTouchHelper.DOWN,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,
                manageDeviceViewModel
            )
        ).attachToRecyclerView(binding.attributeList)

        subscribeUi(binding)

        return binding.root
    }

    private fun subscribeUi(binding: FragmentAddDeviceBinding) {
        manageDeviceViewModel.device.observe(this, Observer { device ->
            binding.deviceNameEditText.setText(device.name)
            binding.deviceNameEditText.addTextChangedListener {
                device.name = binding.deviceNameEditText.text.toString()
            }

            device.location?.let { location ->
                binding.locationEditText.setText(location)
            }
            binding.locationEditText.addTextChangedListener {
                device.location = binding.locationEditText.text.toString()
            }

            binding.phoneNumber.phoneNumber = device.phoneNumber
            binding.phoneNumber.textInputLayout.editText?.addTextChangedListener {
                device.phoneNumber = binding.phoneNumber.phoneNumber
            }

            if (device.messageType == Device.PLAIN_TEXT_FORMAT) {
                binding.plainText.isChecked = true
            } else {
                binding.nameValue.isChecked = true
            }

            binding.messageTypeSelect.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    binding.plainText.id -> manageDeviceViewModel.setMessageType(Device.PLAIN_TEXT_FORMAT)
                    binding.nameValue.id -> manageDeviceViewModel.setMessageType(Device.INT_VALUE_FORMAT)
                }
                // RecyclerView cannot be focused during its update (e.g. if we click on EditText field),
                // otherwise its items may not be shown.
                binding.locationEditText.requestFocus()
            }

            manageDeviceViewModel.uriHandler.setUri(device.image)
            manageDeviceViewModel.setMessageType(device.messageType, refreshAttributes = false)
            manageDeviceViewModel.initAttributes(device)
        })
    }

    override fun onPause() {
        layout.hideSoftKeyboard()

        // If we don't save the device, it will stay in inconsistent state. Old device image was
        // already replaced and removed. We need to store reference to the new one.
        if (manageDeviceViewModel.uriHandler.isDeviceImageChanged() && manageDeviceViewModel.isEditingDevice()) {
            manageDeviceViewModel.addOrUpdateDevice()
        }

        super.onPause()
    }

    override fun onDialogSelectImageActionClick(dialog: DialogFragment) {
        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).also { getImageIntent ->
            this.context?.let {
                getImageIntent.resolveActivity(it.packageManager)?.also {
                    startActivityForResult(getImageIntent,
                        REQUEST_GALLERY_IMAGE
                    )
                }
            }
        }
    }

    override fun onDialogTakePhotoActionClick(dialog: DialogFragment) {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            this.context?.let { context ->
                // Ensure that there's a camera activity to handle the intent
                takePictureIntent.resolveActivity(context.packageManager)?.also {
                    manageDeviceViewModel.uriHandler.createTempUri()?.also { photoUri ->
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)

                        // Add compatibility code for Android API < 21
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            takePictureIntent.addFlags(FLAG_GRANT_WRITE_URI_PERMISSION)
                        } else {
                            val clip = ClipData.newUri(
                                context.contentResolver, "whatevs",
                                photoUri
                            )

                            takePictureIntent.clipData = clip
                            takePictureIntent.addFlags(FLAG_GRANT_WRITE_URI_PERMISSION)
                        }

                        startActivityForResult(takePictureIntent,
                            REQUEST_TAKE_PHOTO
                        )
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_TAKE_PHOTO -> {
                if (resultCode == RESULT_OK) {
                    manageDeviceViewModel.uriHandler.makeTempUriPermanent()
                } else if (resultCode == RESULT_CANCELED) {
                    manageDeviceViewModel.uriHandler.clearTempUri()
                }
            }
            REQUEST_GALLERY_IMAGE -> {
                if (resultCode == RESULT_OK) {
                    data?.data?.apply {
                        manageDeviceViewModel.uriHandler.storeGalleryImage(this)
                    }
                }
            }
        }
    }
}
