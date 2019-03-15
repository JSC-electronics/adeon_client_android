package cz.jsc.electronics.arduinosms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import cz.jsc.electronics.arduinosms.adapters.DeviceAdapter
import cz.jsc.electronics.arduinosms.databinding.FragmentDeviceListBinding
import cz.jsc.electronics.arduinosms.utilities.InjectorUtils
import cz.jsc.electronics.arduinosms.viewmodels.DeviceListViewModel

class DeviceListFragment : Fragment() {

    private lateinit var viewModel: DeviceListViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentDeviceListBinding.inflate(inflater, container, false).apply {
            fab.setOnClickListener { view ->
                val direction = DeviceListFragmentDirections.actionDeviceListFragmentToAddDeviceFragment()
                view.findNavController().navigate(direction)
            }
        }

        val context = context ?: return binding.root

        val factory = InjectorUtils.provideDeviceListViewModelFactory(context)
        viewModel = ViewModelProviders.of(this, factory).get(DeviceListViewModel::class.java)

        val adapter = DeviceAdapter(viewModel)
        binding.deviceList.adapter = adapter
        subscribeUi(adapter, binding)

        return binding.root
    }

    private fun subscribeUi(adapter: DeviceAdapter, binding: FragmentDeviceListBinding) {
        viewModel.getDevices().observe(viewLifecycleOwner, Observer { devices ->
            if (devices != null && devices.isNotEmpty()) {
                binding.hasDevices = true
                adapter.submitList(devices)
            } else {
                binding.hasDevices = false
            }
        })
    }
}
