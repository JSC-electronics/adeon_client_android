package cz.jsc.electronics.arduinosms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import cz.jsc.electronics.arduinosms.adapters.AttributesAdapter
import cz.jsc.electronics.arduinosms.data.Attribute
import cz.jsc.electronics.arduinosms.databinding.FragmentAddDeviceBinding
import cz.jsc.electronics.arduinosms.utilities.InjectorUtils
import cz.jsc.electronics.arduinosms.utilities.hideSoftKeyboard
import cz.jsc.electronics.arduinosms.viewmodels.AddDeviceViewModel
import java.util.*

class AddDeviceFragment : Fragment() {

    private lateinit var layout: ConstraintLayout
    private lateinit var addDeviceViewModel: AddDeviceViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var deviceId: Long? = null
        if (arguments!!.size() > 0) {
            deviceId = AddDeviceFragmentArgs.fromBundle(arguments!!).deviceId
        }

        val factory = InjectorUtils.provideAddDeviceViewModelFactory(requireActivity(), deviceId)
        addDeviceViewModel = ViewModelProviders.of(this, factory).get(AddDeviceViewModel::class.java)

        val binding = FragmentAddDeviceBinding.inflate(inflater, container, false).apply {
            viewModel = addDeviceViewModel
            lifecycleOwner = this@AddDeviceFragment

            phoneNumber.setHint(R.string.phone_hint)
            phoneNumber.setDefaultCountry(Locale.getDefault().country)

            fab.setOnClickListener {
                addDeviceViewModel.addNewAttribute()
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

                if (addDeviceViewModel.isAttributeListEmpty()) {
                    isError = true
                    Snackbar.make(view, R.string.no_attributes_error, Snackbar.LENGTH_LONG).show()
                }

                if (!addDeviceViewModel.isAttributeListValid()) {
                    isError = true
                    Snackbar.make(view, R.string.invalid_attributes, Snackbar.LENGTH_LONG).show()
                }

                if (!isError) {
                        viewModel?.device?.value?.let {
                            it.name = deviceNameEditText.text.toString()
                            it.location = locationEditText.text.toString()
                            it.phoneNumber = phoneNumber.phoneNumber

                            addDeviceViewModel.addOrUpdateDevice()
                        }

                    view.hideSoftKeyboard()
                    val direction = AddDeviceFragmentDirections.actionAddDeviceFragmentToDeviceListFragment()
                    view.findNavController().navigate(direction)
                }
            }
        }
        layout = binding.addDeviceLayout

        binding.attributeList.adapter = addDeviceViewModel.attributeAdapter
        subscribeUi(binding, addDeviceViewModel.attributeAdapter)

        return binding.root
    }

    private fun subscribeUi(binding: FragmentAddDeviceBinding, adapter: AttributesAdapter) {
        addDeviceViewModel.device.observe(this, Observer { device ->
            binding.deviceNameEditText.setText(device.name)
            binding.phoneNumber.phoneNumber = device.phoneNumber

            device.location?.let { location ->
                binding.locationEditText.setText(location)
            }

            if (device.attributes.isEmpty()) {
                device.attributes.add(Attribute())
            }

            adapter.submitList(device.attributes.toList())

        })
    }

    override fun onPause() {
        layout.hideSoftKeyboard()
        super.onPause()
    }

}
