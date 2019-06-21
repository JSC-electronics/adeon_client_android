package cz.jscelectronics.adeon.ui.help

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Browser
import android.util.Log
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
            deleteDevice.setOnClickListener { view ->
                val direction =
                    HelpFragmentDirections.actionHelpFragmentToHelpDeleteDeviceFragment()
                view.findNavController().navigate(direction)
            }
            exportConfiguration.setOnClickListener { view ->
                val direction =
                    HelpFragmentDirections.actionHelpFragmentToHelpExportConfigurationFragment()
                view.findNavController().navigate(direction)
            }
            importConfiguration.setOnClickListener { view ->
                val direction =
                    HelpFragmentDirections.actionHelpFragmentToHelpImportConfigurationFragment()
                view.findNavController().navigate(direction)
            }
            sendCommands.setOnClickListener { view ->
                val direction =
                    HelpFragmentDirections.actionHelpFragmentToHelpSendCommandsFragment()
                view.findNavController().navigate(direction)
            }
            browseAllArticles.setOnClickListener { view ->
                val uri = Uri.parse(DOCUMENTATION_ROOT_URI)
                val context = view.context
                val intent = Intent(Intent.ACTION_VIEW, uri)
                intent.putExtra(Browser.EXTRA_APPLICATION_ID, context.packageName)
                try {
                    context.startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    Log.w("HelpFragment", "Activity was not found for intent, $intent")
                }
            }
            sendFeedback.setOnClickListener {
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "plain/text"
                intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(ADEON_SUPPORT_EMAIL))
                intent.putExtra(Intent.EXTRA_SUBJECT, ADEON_SUPPORT_EMAIL_SUBJECT)
                startActivity(Intent.createChooser(intent, ""))
            }
        }
        return binding.root
    }
}
