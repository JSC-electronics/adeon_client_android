package cz.jscelectronics.adeon.ui.help

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import cz.jscelectronics.adeon.databinding.FragmentHelpAddNewDeviceBinding
import cz.jscelectronics.adeon.databinding.FragmentHelpBinding

class AddNewDeviceFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentHelpAddNewDeviceBinding.inflate(inflater, container, false)
        return binding.root
    }
}
