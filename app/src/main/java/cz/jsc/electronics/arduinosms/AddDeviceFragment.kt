package cz.jsc.electronics.arduinosms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import cz.jsc.electronics.arduinosms.databinding.FragmentAddDeviceBinding

class AddDeviceFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentAddDeviceBinding.inflate(inflater, container, false)
        return binding.root
    }
}
