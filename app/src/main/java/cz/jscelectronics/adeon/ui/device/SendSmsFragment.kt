package cz.jscelectronics.adeon.ui.device

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.google.android.material.snackbar.Snackbar
import cz.jscelectronics.adeon.R
import cz.jscelectronics.adeon.adapters.AttributesAdapter
import cz.jscelectronics.adeon.data.Attribute
import cz.jscelectronics.adeon.databinding.FragmentSendSmsBinding
import cz.jscelectronics.adeon.ui.device.viewmodels.ManageDeviceViewModel
import cz.jscelectronics.adeon.utilities.InjectorUtils
import cz.jscelectronics.adeon.utilities.hideSoftKeyboard

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
    private lateinit var interstitialAd: InterstitialAd


    companion object {
        const val REQUEST_SEND_SMS = 187
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val deviceId = AddDeviceFragmentArgs.fromBundle(arguments!!).deviceId

        val factory = InjectorUtils.provideManageDeviceViewModelFactory(requireActivity(), deviceId)
        manageDeviceViewModel = ViewModelProviders.of(this, factory).get(ManageDeviceViewModel::class.java)

        val binding = FragmentSendSmsBinding.inflate(inflater, container, false).apply {
            viewModel = manageDeviceViewModel
            lifecycleOwner = this@SendSmsFragment

            sendSmsButton.setOnClickListener {
                prepareAndSendSms()
            }
        }
        layout = binding.sendSmsLayout

        val adapter = manageDeviceViewModel.getAttributesAdapter()
        adapter.listener = this
        binding.attributeList.adapter = adapter

        subscribeUi()
        MobileAds.initialize(this.requireContext(), "ca-app-pub-3940256099942544~3347511713")

        interstitialAd = InterstitialAd(this.requireContext())
        interstitialAd.adUnitId = "ca-app-pub-3940256099942544/1033173712"
        interstitialAd.loadAd(AdRequest.Builder().build())
        interstitialAd.adListener = object : AdListener() {
            override fun onAdClosed() {
                interstitialAd.loadAd(AdRequest.Builder().build())
            }
        }

        return binding.root
    }

    private fun subscribeUi() {
        manageDeviceViewModel.device.observe(this, Observer { device ->
            manageDeviceViewModel.setMessageType(device.messageType, refreshAttributes = false)
            manageDeviceViewModel.initAttributes(device)
        })
    }

    private fun prepareAndSendSms() {
        if (manageDeviceViewModel.areAttributesChecked() || messageText != null) {
            manageDeviceViewModel.sendSmsMessage(requireActivity(), messageText)

            val direction =
                SendSmsFragmentDirections.actionSendSmsFragmentToDeviceListFragment()
            layout.findNavController().navigate(direction)
            if (interstitialAd.isLoaded) {
                interstitialAd.show()
            }
        } else {
            Snackbar.make(layout, R.string.no_command_selected, Snackbar.LENGTH_LONG).show()
        }

    }

    override fun onPause() {
        layout.hideSoftKeyboard()
        super.onPause()
    }
}
