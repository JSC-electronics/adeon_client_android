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
                    addDeviceViewModel.addOrUpdateDevice(
                        deviceNameEditText.text.toString(),
                        locationEditText.text.toString(),
                        phoneNumber.phoneNumber
                    )

                    view.hideSoftKeyboard()
                    val direction = AddDeviceFragmentDirections.actionAddDeviceFragmentToDeviceListFragment()
                    view.findNavController().navigate(direction)
                }
            }
        }
        layout = binding.addDeviceLayout

        val adapter = AttributesAdapter()
        binding.attributeList.adapter = adapter

        addDeviceViewModel.getDevice()?.let {
                it.observe(this, Observer { device ->
                    binding.deviceNameEditText.setText(device.name)
                    binding.phoneNumber.phoneNumber = device.phoneNumber

                    device.location?.let { location ->
                        binding.locationEditText.setText(location)
                    }

                    addDeviceViewModel.restoreData(device)
            })
        }
        subscribeUi(adapter)

        return binding.root
    }

    private fun subscribeUi(adapter: AttributesAdapter) {
        addDeviceViewModel.getAttributes().observe(viewLifecycleOwner, Observer { attributes ->
            if (attributes != null && attributes.isNotEmpty()) {
                adapter.submitList(attributes.toList())
            }
        })
    }

    override fun onPause() {
        layout.hideSoftKeyboard()
        super.onPause()
    }

}
