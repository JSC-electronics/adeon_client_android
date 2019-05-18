package cz.jsc.electronics.smscontrol

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import cz.jsc.electronics.smscontrol.adapters.DeviceAdapter
import cz.jsc.electronics.smscontrol.databinding.FragmentDeviceListBinding
import cz.jsc.electronics.smscontrol.utilities.InjectorUtils
import cz.jsc.electronics.smscontrol.viewmodels.DeviceListViewModel

class DeviceListFragment : Fragment(), ImportDialogFragment.ImportDialogListener {

    companion object {
        private const val JSON_MIME_TYPE = "text/json"
        private const val DEFAULT_CONFIG_NAME = "sms_control_config.json"
        private const val READ_CONFIG_REQUEST_CODE: Int = 42
        private const val WRITE_CONFIG_REQUEST_CODE: Int = 43
    }

    private lateinit var viewModel: DeviceListViewModel
    private lateinit var layout: CoordinatorLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentDeviceListBinding.inflate(inflater, container, false).apply {
            fab.setOnClickListener { view ->
                val direction = DeviceListFragmentDirections.actionDeviceListFragmentToAddDeviceFragment()
                view.findNavController().navigate(direction)
            }
        }

        val context = context ?: return binding.root
        val factory = InjectorUtils.provideDeviceListViewModelFactory(context)
        viewModel = ViewModelProviders.of(this, factory).get(DeviceListViewModel::class.java)

        layout = binding.devices
        val adapter = DeviceAdapter(viewModel)
        binding.deviceList.adapter = adapter

        subscribeUi(adapter, binding)

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_import_export, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_import -> {
                if (!viewModel.getDevices().value.isNullOrEmpty()) {
                    this.fragmentManager?.let { manager ->
                        ImportDialogFragment(this).show(manager, "Import Dialog")
                    }
                } else {
                    readConfiguration()
                }

                return true
            }
            R.id.action_export -> {
                if (!viewModel.getDevices().value.isNullOrEmpty()) {
                    writeConfiguration()
                } else {
                    Snackbar.make(layout, R.string.nothing_to_export, Snackbar.LENGTH_LONG).show()
                }

                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun subscribeUi(adapter: DeviceAdapter, binding: FragmentDeviceListBinding) {
        viewModel.getDevices().observe(viewLifecycleOwner, Observer { devices ->
            if (devices != null && devices.isNotEmpty()) {
                binding.hasDevices = true
                adapter.submitList(devices)
            } else {
                binding.hasDevices = false
            }
        })
    }

    override fun onDialogPositiveClick(dialog: DialogFragment) {
        readConfiguration()
    }

    private fun readConfiguration() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = JSON_MIME_TYPE
        }

        startActivityForResult(intent, READ_CONFIG_REQUEST_CODE)
    }

    private fun writeConfiguration() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = JSON_MIME_TYPE
            putExtra(Intent.EXTRA_TITLE, DEFAULT_CONFIG_NAME)
        }

        startActivityForResult(intent, WRITE_CONFIG_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                READ_CONFIG_REQUEST_CODE -> {
                    data?.data?.also { uri ->
                        context?.let {
                            viewModel.importConfiguration(uri, it)
                        }
                    }
                }
                WRITE_CONFIG_REQUEST_CODE -> {
                    data?.data?.also { uri ->
                        context?.let {
                            viewModel.exportConfiguration(uri, it)
                        }
                    }
                }
            }
        }
    }
}
