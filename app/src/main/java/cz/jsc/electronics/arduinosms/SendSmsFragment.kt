package cz.jsc.electronics.arduinosms

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import cz.jsc.electronics.arduinosms.adapters.AttributesAdapter
import cz.jsc.electronics.arduinosms.databinding.FragmentSendSmsBinding
import cz.jsc.electronics.arduinosms.utilities.InjectorUtils
import cz.jsc.electronics.arduinosms.viewmodels.SendSmsViewModel

class SendSmsFragment : Fragment() {

    private lateinit var layout: ConstraintLayout
    private lateinit var sendSmsViewModel: SendSmsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val deviceId = AddDeviceFragmentArgs.fromBundle(arguments!!).deviceId

        val factory = InjectorUtils.provideSendSmsViewModelFactory(requireActivity(), deviceId)
        sendSmsViewModel = ViewModelProviders.of(this, factory).get(SendSmsViewModel::class.java)

        val binding = FragmentSendSmsBinding.inflate(inflater, container, false).apply {
            viewModel = sendSmsViewModel
            lifecycleOwner = this@SendSmsFragment
        }
        layout = binding.sendSmsLayout
        val adapter = AttributesAdapter(true)
        binding.attributeList.adapter = adapter

        sendSmsViewModel.device.observe(this, Observer { device ->
                device.attributes?.let { attributes ->
                    sendSmsViewModel.restoreAttributes(attributes)
                }
            })

        subscribeUi(adapter)

        return binding.root
    }

    private fun subscribeUi(adapter: AttributesAdapter) {
        sendSmsViewModel.getAttributes().observe(viewLifecycleOwner, Observer { attributes ->
            if (attributes != null && attributes.isNotEmpty()) {
                adapter.submitList(attributes.toList())
            }
        })
    }

    override fun onPause() {
        hideKeyboardFrom(context, layout)
        super.onPause()
    }

    private fun hideKeyboardFrom(context: Context?, view: View) {
        val imm = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

}
