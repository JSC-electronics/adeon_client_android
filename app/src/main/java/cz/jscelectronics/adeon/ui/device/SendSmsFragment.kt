package cz.jscelectronics.adeon.ui.device

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
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
    private lateinit var layout: CoordinatorLayout
    private lateinit var manageDeviceViewModel: ManageDeviceViewModel
    private lateinit var billingViewModel: BillingViewModel
    private var interstitialAd: InterstitialAd? = null
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

        billingViewModel.noAdvertisementsLiveData.observe(this.viewLifecycleOwner, Observer {
            if (it == null || !it.entitled) {
                enableAdvertisements()
            }
        })
    }

    private fun enableAdvertisements() {
        MobileAds.initialize(this.requireContext(), "ca-app-pub-7647102948654129~2785935234")
        interstitialAd = InterstitialAd(this.requireContext()).apply {
            this.adUnitId = "ca-app-pub-7647102948654129/6655848049"
            this.loadAd(AdRequest.Builder().build())
            this.adListener = object : AdListener() {
                override fun onAdClosed() {
                    interstitialAd?.loadAd(AdRequest.Builder().build())
                }
            }
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

            if (interstitialAd == null) {
                sendSmsToDevice()
            } else {
                interstitialAd?.apply {
                    if (this.isLoaded) {
                        this.show()
                        this.adListener = object: AdListener() {
                            override fun onAdClosed() {
                                sendSmsToDevice()
                            }
                        }
                    } else {
                        sendSmsToDevice()
                    }
                }
            }
        } else {
            Snackbar.make(layout, R.string.no_command_selected, Snackbar.LENGTH_LONG).show()
        }
    }

    private fun sendSmsToDevice() {
        manageDeviceViewModel.sendSmsMessage(requireActivity(), getPhoneNumber(), messageText)
        messageText = null

        val direction =
            SendSmsFragmentDirections.actionGlobalDeviceList()
        layout.findNavController().navigate(direction)
    }

    private fun getPhoneNumber(): String {
        return layout.phone_number.getPhoneNumber(layout.phone_number.isShortNumber)
    }

    override fun onPause() {
        layout.hideSoftKeyboard()
        super.onPause()
    }
}
