package cz.jsc.electronics.arduinosms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import cz.jsc.electronics.arduinosms.databinding.FragmentDevicesBinding

class DeviceListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentDevicesBinding.inflate(inflater, container, false).apply {
            fab.setOnClickListener { view ->
                val direction = DeviceListFragmentDirections.actionDeviceListFragmentToAddDeviceFragment()
                view.findNavController().navigate(direction)
            }
        }

        return binding.root
    }
}
