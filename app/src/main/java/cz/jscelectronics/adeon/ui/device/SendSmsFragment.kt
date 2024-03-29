package cz.jscelectronics.adeon.ui.device

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
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
import cz.jscelectronics.phonefield.PhoneTextView

class SendSmsFragment : Fragment(), AttributesAdapter.AttributeListener {
    override fun onClicked(attribute: Attribute) {
        if (attribute.containsPlainText()) {
            messageText = attribute.text
            prepareAndSendSms()
        }
    }

    private var messageText: String? = null
    private lateinit var layout: ConstraintLayout
    private lateinit var manageDeviceViewModel: ManageDeviceViewModel
    private val args: SendSmsFragmentArgs by navArgs()
    private lateinit var phoneNumber: PhoneTextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val factory =
            InjectorUtils.provideManageDeviceViewModelFactory(requireActivity(), args.deviceId)
        manageDeviceViewModel =
            ViewModelProvider(this, factory)[ManageDeviceViewModel::class.java]

        val binding = FragmentSendSmsBinding.inflate(inflater, container, false).apply {
            viewModel = manageDeviceViewModel
            lifecycleOwner = this@SendSmsFragment

            sendSmsButton.setOnClickListener {
                prepareAndSendSms()
            }

            dialButton.setOnClickListener {
                manageDeviceViewModel.callDevice(requireActivity(), getPhoneNumber())
            }
        }
        layout = binding.sendSmsLayout

        val adapter = manageDeviceViewModel.getAttributesAdapter()
        adapter.listener = this
        binding.attributeList.adapter = adapter
        binding.phoneNumber.textView.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
        subscribeUi()
        phoneNumber = binding.phoneNumber
        return binding.root
    }

    private fun subscribeUi() {
        manageDeviceViewModel.device.observe(this.viewLifecycleOwner) { device ->
            manageDeviceViewModel.setMessageType(device.messageType, refreshAttributes = false)
            manageDeviceViewModel.initAttributes(device)
        }
    }

    private fun prepareAndSendSms() {
        if (manageDeviceViewModel.areAttributesChecked() || messageText != null) {
            if (!manageDeviceViewModel.areAttributesCheckedAndValid()) {
                Snackbar.make(
                    layout,
                    getString(R.string.invalid_command_value),
                    Snackbar.LENGTH_LONG
                ).show()
                return
            }
            manageDeviceViewModel.sendSmsMessage(requireActivity(), getPhoneNumber(), messageText)
            messageText = null
            layout.findNavController().popBackStack()
        } else {
            Snackbar.make(layout, R.string.no_command_selected, Snackbar.LENGTH_LONG).show()
        }
    }

    private fun getPhoneNumber(): String {
        return phoneNumber.getPhoneNumber(phoneNumber.isShortNumber)
    }

    override fun onPause() {
        layout.hideSoftKeyboard()
        super.onPause()
    }
}
