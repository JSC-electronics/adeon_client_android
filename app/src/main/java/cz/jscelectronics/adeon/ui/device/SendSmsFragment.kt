package cz.jscelectronics.adeon.ui.device

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.material.snackbar.Snackbar
import cz.jscelectronics.adeon.R
import cz.jscelectronics.adeon.adapters.AttributesAdapter
import cz.jscelectronics.adeon.data.Attribute
import cz.jscelectronics.adeon.databinding.FragmentSendSmsBinding
import cz.jscelectronics.adeon.ui.billing.viewmodels.BillingViewModel
import cz.jscelectronics.adeon.ui.device.viewmodels.ManageDeviceViewModel
import cz.jscelectronics.adeon.utilities.InjectorUtils
import cz.jscelectronics.adeon.utilities.hideSoftKeyboard
import kotlinx.android.synthetic.main.fragment_send_sms.view.*

class SendSmsFragment : Fragment(), AttributesAdapter.AttributeListener {
    override fun onClicked(attribute: Attribute) {
        if (attribute.containsPlainText()) {
            messageText = attribute.text
            prepareAndSendSms()
        }
    }

    private var messageText: String? = null
    private lateinit var layout: ConstraintLayout
    private lateinit var adView: AdView
    private lateinit var manageDeviceViewModel: ManageDeviceViewModel
    private lateinit var billingViewModel: BillingViewModel
    private val args: SendSmsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val factory =
            InjectorUtils.provideManageDeviceViewModelFactory(requireActivity(), args.deviceId)
        manageDeviceViewModel =
            ViewModelProvider(this, factory).get(ManageDeviceViewModel::class.java)
        billingViewModel = ViewModelProvider(this).get(BillingViewModel::class.java)

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
        adView = binding.adView
        layout = binding.sendSmsLayout

        val adapter = manageDeviceViewModel.getAttributesAdapter()
        adapter.listener = this
        binding.attributeList.adapter = adapter
        layout.phone_number.textView.setTextColor(resources.getColor(android.R.color.white))
        subscribeUi()
        return binding.root
    }

    private fun subscribeUi() {
        manageDeviceViewModel.device.observe(this.viewLifecycleOwner, Observer { device ->
            manageDeviceViewModel.setMessageType(device.messageType, refreshAttributes = false)
            manageDeviceViewModel.initAttributes(device)
        })

        billingViewModel.noAdvertisementsLiveData.observe(this.viewLifecycleOwner, Observer {
            if (it == null || !it.entitled) {
                enableAdvertisements()
            } else {
                adView.visibility = View.GONE
            }
        })
    }

    private fun enableAdvertisements() {
        MobileAds.initialize(this.requireContext()) {}
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
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
        return layout.phone_number.getPhoneNumber(layout.phone_number.isShortNumber)
    }

    override fun onPause() {
        layout.hideSoftKeyboard()
        super.onPause()
    }
}
