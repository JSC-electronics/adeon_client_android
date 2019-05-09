package cz.jsc.electronics.smscontrol

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.google.android.material.snackbar.Snackbar
import cz.jsc.electronics.smscontrol.adapters.AttributesAdapter
import cz.jsc.electronics.smscontrol.databinding.FragmentSendSmsBinding
import cz.jsc.electronics.smscontrol.utilities.InjectorUtils
import cz.jsc.electronics.smscontrol.utilities.hideSoftKeyboard
import cz.jsc.electronics.smscontrol.viewmodels.ManageDeviceViewModel

class SendSmsFragment : Fragment() {

    private lateinit var layout: ConstraintLayout
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
                requestSmsPermissions()
            }
        }
        layout = binding.sendSmsLayout

        val adapter = manageDeviceViewModel.getAttributesAdapter(true)
        binding.attributeList.adapter = adapter

        subscribeUi(adapter)
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

    private fun subscribeUi(adapter: AttributesAdapter) {
        manageDeviceViewModel.device.observe(this, Observer { device ->
            adapter.submitList(device.attributes.toList())

        })
    }

    private fun requestSmsPermissions() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.SEND_SMS
            )
            != PackageManager.PERMISSION_GRANTED
        ) {

            requestPermissions(arrayOf(Manifest.permission.SEND_SMS), REQUEST_SEND_SMS)
        } else {
            manageDeviceViewModel.sendSmsMessage()
            val direction = SendSmsFragmentDirections.actionSendSmsFragmentToDeviceListFragment()
            layout.findNavController().navigate(direction)
            if (interstitialAd.isLoaded) {
                interstitialAd.show()
            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_SEND_SMS -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    manageDeviceViewModel.sendSmsMessage()
                    val direction = SendSmsFragmentDirections.actionSendSmsFragmentToDeviceListFragment()
                    layout.findNavController().navigate(direction)
                    if (interstitialAd.isLoaded) {
                        interstitialAd.show()
                    }
                } else {
                    Snackbar.make(layout, R.string.sms_permissions_not_granted, Snackbar.LENGTH_LONG).show()
                }
                return
            }
            else -> {
                // Ignore all other requests.
            }
        }
    }

    override fun onPause() {
        layout.hideSoftKeyboard()
        super.onPause()
    }
}
