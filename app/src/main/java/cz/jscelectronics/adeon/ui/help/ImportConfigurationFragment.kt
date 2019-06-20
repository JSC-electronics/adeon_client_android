package cz.jscelectronics.adeon.ui.help

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import cz.jscelectronics.adeon.databinding.FragmentHelpImportConfigurationBinding

class ImportConfigurationFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentHelpImportConfigurationBinding.inflate(inflater, container, false).apply {
            context?.let { context ->

            }
        }
        return binding.root
    }
}
