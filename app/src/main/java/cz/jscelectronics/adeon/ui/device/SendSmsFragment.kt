package cz.jscelectronics.adeon.ui.device

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import cz.jscelectronics.adeon.R
import cz.jscelectronics.adeon.adapters.AttributesAdapter
import cz.jscelectronics.adeon.data.Attribute
import cz.jscelectronics.adeon.databinding.FragmentSendSmsBinding
import cz.jscelectronics.adeon.ui.device.viewmodels.ManageDeviceViewModel
import cz.jscelectronics.adeon.utilities.InjectorUtils
import cz.jscelectronics.adeon.utilities.hideSoftKeyboard
import kotlinx.android.synthetic.main.fragment_send_sms.view.*

class SendSmsFragment : Fragment(), AttributesAdapter.AttributeListener {
    override fun onClicked(attribute: Attribute) {
        if (attribute.containsPlainText()) {
            messageText = attribute.text
            requestSmsPermissions()
        }
    }

    private var messageText: String? = null
    private lateinit var layout: CoordinatorLayout
    private lateinit var manageDeviceViewModel: ManageDeviceViewModel
    private val args: SendSmsFragmentArgs by navArgs()

    companion object {
        const val REQUEST_SEND_SMS = 187
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val factory =
            InjectorUtils.provideManageDeviceViewModelFactory(requireActivity(), args.deviceId)
        manageDeviceViewModel =
            ViewModelProvider(this, factory).get(ManageDeviceViewModel::class.java)

        val binding = FragmentSendSmsBinding.inflate(inflater, container, false).apply {
            viewModel = manageDeviceViewModel
            lifecycleOwner = this@SendSmsFragment

            sendSmsButton.setOnClickListener {
                requestSmsPermissions()
            }

            dialButton.setOnClickListener {
                manageDeviceViewModel.callDevice(requireActivity(), getPhoneNumber())
            }
        }
        layout = binding.sendSmsLayout

        val adapter = manageDeviceViewModel.getAttributesAdapter()
        adapter.listener = this
        binding.attributeList.adapter = adapter

        subscribeUi()
        return binding.root
    }

    private fun subscribeUi() {
        manageDeviceViewModel.device.observe(this.viewLifecycleOwner, Observer { device ->
            manageDeviceViewModel.setMessageType(device.messageType, refreshAttributes = false)
            manageDeviceViewModel.initAttributes(device)
        })
    }

    private fun requestSmsPermissions() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.SEND_SMS
            )
            != PackageManager.PERMISSION_GRANTED
        ) {

            requestPermissions(
                arrayOf(Manifest.permission.SEND_SMS),
                REQUEST_SEND_SMS
            )
        } else {
            sendSmsMessage()
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_SEND_SMS -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    sendSmsMessage()
                } else {
                    Snackbar.make(
                        layout,
                        R.string.sms_permissions_not_granted,
                        Snackbar.LENGTH_LONG
                    ).show()
                }
                return
            }
            else -> {
                // Ignore all other requests.
            }
        }
    }

    private fun sendSmsMessage() {
        if (manageDeviceViewModel.areAttributesChecked() || messageText != null) {

            if (!manageDeviceViewModel.areAttributesCheckedAndValid()) {
                Snackbar.make(
                    layout,
                    getString(R.string.invalid_command_value),
                    Snackbar.LENGTH_LONG
                ).show()
                return
            }
            manageDeviceViewModel.sendSmsMessage(getPhoneNumber(), messageText)
            messageText = null
            layout.findNavController().popBackStack()
        } else {
            Snackbar.make(layout, R.string.no_command_selected, Snackbar.LENGTH_LONG).show()
        }
    }

    private fun getPhoneNumber(): String {
        return layout.phone_number.getPhoneNumber(layout.phone_number.isShortNumber)
    }

    override fun onPause() {
        layout.hideSoftKeyboard()
        super.onPause()
    }
}
