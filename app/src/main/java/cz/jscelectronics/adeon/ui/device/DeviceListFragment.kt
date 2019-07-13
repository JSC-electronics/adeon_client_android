package cz.jscelectronics.adeon.ui.device

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
import androidx.recyclerview.widget.ItemTouchHelper
import com.google.android.material.snackbar.Snackbar
import cz.jscelectronics.adeon.R
import cz.jscelectronics.adeon.adapters.DeviceAdapter
import cz.jscelectronics.adeon.adapters.RecyclerAttributeTouchHelper
import cz.jscelectronics.adeon.databinding.FragmentDeviceListBinding
import cz.jscelectronics.adeon.ui.device.dialogs.ImportDialogFragment
import cz.jscelectronics.adeon.ui.device.viewmodels.DeviceListViewModel
import cz.jscelectronics.adeon.utilities.InjectorUtils
import java.text.SimpleDateFormat
import java.util.*


class DeviceListFragment : Fragment(), ImportDialogFragment.ImportDialogListener {

    companion object {
        // As a workaround for SAF, where JSON MIME type is not supported on some devices,
        // we'll use TXT instead. In order to allow import between all of our devices, we'll
        // use TEXT type on all devices.
        private const val JSON_MIME_TYPE = "text/plain"
        private const val DEFAULT_CONFIG_NAME_TEMPLATE = "adeon_config_%s.txt"
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
                val direction =
                    DeviceListFragmentDirections.actionDeviceListFragmentToAddDeviceFragment()
                view.findNavController().navigate(direction)
            }
        }

        val context = context ?: return binding.root
        val factory = InjectorUtils.provideDeviceListViewModelFactory(context)
        viewModel = ViewModelProviders.of(this, factory).get(DeviceListViewModel::class.java)

        layout = binding.devices
        val adapter = DeviceAdapter(viewModel)
        binding.deviceList.adapter = adapter
        ItemTouchHelper(
            RecyclerAttributeTouchHelper(
                0 /* TODO: Device move not implemented */,
                0 /* Disable swipe gesture to resolve issue #22 and partially mitigate #21; ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT*/,
                viewModel
            )
        ).attachToRecyclerView(binding.deviceList)

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

        startActivityForResult(intent,
            READ_CONFIG_REQUEST_CODE
        )
    }

    private fun writeConfiguration() {
        val currentTime = System.currentTimeMillis()
        val configDate = SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US).format(Date(currentTime))
        val configFileName = String.format(DEFAULT_CONFIG_NAME_TEMPLATE, configDate)

        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = JSON_MIME_TYPE
            putExtra(Intent.EXTRA_TITLE,
                configFileName
            )
        }

        startActivityForResult(intent,
            WRITE_CONFIG_REQUEST_CODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                READ_CONFIG_REQUEST_CODE -> {
                    data?.data?.also { uri ->
                        viewModel.importConfiguration(uri, layout)
                    }
                }
                WRITE_CONFIG_REQUEST_CODE -> {
                    data?.data?.also { uri ->
                        viewModel.exportConfiguration(uri, layout)
                    }
                }
            }
        }
    }
}
