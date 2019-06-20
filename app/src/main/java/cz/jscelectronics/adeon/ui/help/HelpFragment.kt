package cz.jscelectronics.adeon.ui.help

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import cz.jscelectronics.adeon.databinding.FragmentHelpBinding
import cz.jscelectronics.adeon.ui.device.DeviceListFragmentDirections

class HelpFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentHelpBinding.inflate(inflater, container, false).apply {
            addDevice.setOnClickListener { view ->
                val direction =
                    HelpFragmentDirections.actionHelpFragmentToHelpAddNewDeviceFragment()
                view.findNavController().navigate(direction)
            }
        }
        return binding.root
    }
}
