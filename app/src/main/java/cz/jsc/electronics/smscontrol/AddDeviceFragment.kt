package cz.jsc.electronics.smscontrol

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import cz.jsc.electronics.smscontrol.data.Device
import cz.jsc.electronics.smscontrol.databinding.FragmentAddDeviceBinding
import cz.jsc.electronics.smscontrol.utilities.InjectorUtils
import cz.jsc.electronics.smscontrol.utilities.hideSoftKeyboard
import cz.jsc.electronics.smscontrol.viewmodels.ManageDeviceViewModel
import kotlinx.android.synthetic.main.fragment_add_device.view.*
import java.io.File
import java.io.IOException
import java.util.*

class AddDeviceFragment : Fragment(), IconCaptureDialogFragment.IconCaptureDialogListener {
    companion object {
        const val REQUEST_TAKE_PHOTO = 1
        const val REQUEST_GALLERY_IMAGE = 2
    }

    private lateinit var layout: CoordinatorLayout
    private lateinit var manageDeviceViewModel: ManageDeviceViewModel

    private var deviceImageUri: Uri? = null

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
            phoneNumber.setDefaultCountry(Locale.getDefault().country)

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
                    Snackbar.make(view, R.string.no_attributes_error, Snackbar.LENGTH_LONG).show()
                }

                if (!manageDeviceViewModel.isAttributeListValid()) {
                    isError = true
                    Snackbar.make(view, R.string.invalid_attributes, Snackbar.LENGTH_LONG).show()
                }

                if (!isError) {
                    viewModel?.device?.value?.let {
                        it.name = deviceNameEditText.text.toString()
                        it.location = locationEditText.text.toString()
                        it.phoneNumber = phoneNumber.phoneNumber

                        manageDeviceViewModel.addOrUpdateDevice(overwriteAttributes = true)
                    }

                    view.hideSoftKeyboard()
                    val direction = AddDeviceFragmentDirections.actionAddDeviceFragmentToDeviceListFragment()
                    view.findNavController().navigate(direction)
                }
            }
        }

        binding.deviceIcon.setOnClickListener {
            this.fragmentManager?.let {
                IconCaptureDialogFragment(this).show(it, "Icon Select Dialog")
            }
        }

        layout = binding.addDeviceLayout

        val adapter = manageDeviceViewModel.getAttributesAdapter(isEditMode = true)
        binding.attributeList.adapter = adapter
        subscribeUi(binding)

        return binding.root
    }

    private fun subscribeUi(binding: FragmentAddDeviceBinding) {
        manageDeviceViewModel.device.observe(this, Observer { device ->
            binding.deviceNameEditText.setText(device.name)
            binding.phoneNumber.phoneNumber = device.phoneNumber

            if (device.messageType == Device.INT_VALUE_FORMAT) {
                binding.nameValue.isChecked = true
            } else {
                binding.plainText.isChecked = true
            }

            binding.messageTypeSelect.setOnCheckedChangeListener { _, checkedId ->
                when(checkedId) {
                    binding.nameValue.id -> manageDeviceViewModel.setMessageType(Device.INT_VALUE_FORMAT)
                    binding.plainText.id -> manageDeviceViewModel.setMessageType(Device.PLAIN_TEXT_FORMAT)
                }
                // RecyclerView cannot be focused during its update (e.g. if we click on EditText field),
                // otherwise its items may not be shown.
                binding.locationEditText.requestFocus()
            }
            
            device.location?.let { location ->
                binding.locationEditText.setText(location)
            }

            device.icon?.let {
                binding.deviceIcon.setImageURI(it)
            }

            manageDeviceViewModel.setMessageType(device.messageType, refreshAttributes = false)
            manageDeviceViewModel.initAttributes(device)
        })
    }

    override fun onPause() {
        layout.hideSoftKeyboard()
        super.onPause()
    }

    override fun onDialogSelectImageActionClick(dialog: DialogFragment) {
        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).also { getImageIntent ->
            this.context?.let {
                getImageIntent.resolveActivity(it.packageManager)?.also {
                    startActivityForResult(getImageIntent, REQUEST_GALLERY_IMAGE)
                }
            }
        }
    }

    override fun onDialogTakePhotoActionClick(dialog: DialogFragment) {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            this.context?.let { context ->
                // Ensure that there's a camera activity to handle the intent
                takePictureIntent.resolveActivity(context.packageManager)?.also {
                    // Create the File where the photo should go
                    val photoFile: File? = try {
                        manageDeviceViewModel.createImageFile(context)
                    } catch (ex: IOException) {
                        null
                    }

                    // Continue only if the File was successfully created
                    photoFile?.also {
                        val photoURI: Uri = FileProvider.getUriForFile(
                            context,
                            "cz.jsc.electronics.smscontrol.fileprovider",
                            it
                        )

                        deviceImageUri = photoURI

                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_TAKE_PHOTO -> {
                    deviceImageUri?.let {
                        manageDeviceViewModel.device.value?.apply {
                            this.icon = deviceImageUri
                        }

                        layout.device_icon.setImageURI(deviceImageUri)
                    }

                    manageDeviceViewModel.device.value?.apply {
                        this.icon = deviceImageUri
                    }
                }
                REQUEST_GALLERY_IMAGE -> {
                    data?.data?.let { uri ->
                        layout.device_icon.setImageURI(uri)

                        context?.let {
                            manageDeviceViewModel.storeGalleryImage(uri, it)
                        }
                    }
                }
            }
        }
    }
}
