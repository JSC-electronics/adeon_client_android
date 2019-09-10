package cz.jscelectronics.adeon.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cz.jscelectronics.adeon.R
import cz.jscelectronics.adeon.data.Device
import cz.jscelectronics.adeon.databinding.ListItemDeviceBinding
import cz.jscelectronics.adeon.ui.device.DeviceListFragment
import cz.jscelectronics.adeon.ui.device.DeviceListFragmentDirections
import cz.jscelectronics.adeon.ui.device.viewmodels.DeviceListViewModel


/**
 * Adapter for the [RecyclerView] in [DeviceListFragment].
 */
class DeviceAdapter(private val viewModel: DeviceListViewModel) :
    ListAdapter<Device, DeviceAdapter.ViewHolder>(DeviceDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val device = getItem(position)
        holder.apply {
            bind(createOnClickListener(device.deviceId), device)
            itemView.tag = device
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ListItemDeviceBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ), viewModel
        )
    }

    private fun createOnClickListener(deviceId: Long): View.OnClickListener {
        return View.OnClickListener {
            val direction = DeviceListFragmentDirections.actionDeviceListFragmentToSendSmsFragment(deviceId)
            it.findNavController().navigate(direction)
        }
    }

    class ViewHolder(
        private val binding: ListItemDeviceBinding,
        private val viewModel: DeviceListViewModel
    ) : RecyclerView.ViewHolder(binding.root), SwipableViewHolder {
        private val mRemoveableView: View = binding.root.findViewById(R.id.view_foreground)

        override fun getSwipableView(): View {
            return mRemoveableView
        }

        fun bind(listener: View.OnClickListener, item: Device) {
            binding.apply {
                clickListener = listener
                device = item
                overflow.setOnClickListener {
                    showPopupMenu(it, item)
                }
                executePendingBindings()
            }
        }

        /**
         * Showing popup menu when tapping on 3 dots
         */
        private fun showPopupMenu(view: View, device: Device) {
            // inflate menu
            val popup = PopupMenu(view.context, view)
            val inflater = popup.menuInflater
            inflater.inflate(R.menu.menu_device_card, popup.menu)
            popup.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.action_edit_device_entry -> {
                        val direction =
                            DeviceListFragmentDirections.
                                actionDeviceListFragmentToAddDeviceFragment(
                                    view.resources.getString(R.string.edit_device_title)).
                                setDeviceId(device.deviceId)
                        view.findNavController().navigate(direction)
                        true
                    }
                    R.id.action_remove_device -> {
                        viewModel.deleteDeviceWithUndo(device, view)
                        true
                    }
                    R.id.action_duplicate_entry -> {
                        viewModel.duplicateDevice(device)
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }


    }
}

private class DeviceDiffCallback : DiffUtil.ItemCallback<Device>() {

    override fun areItemsTheSame(oldItem: Device, newItem: Device): Boolean {
        /* FIXME: When last item is deleted from the list, probably due to a bug clearView() is not called.
         * When the view is recycled, we see view in invalid state. For now we'll force this check to
         * assume the items are different. This will create new object in proper state. If we find a proper solution,
         * we should uncomment original code below.
         */
        return oldItem.deviceId == newItem.deviceId

        // return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: Device, newItem: Device): Boolean {
        return oldItem == newItem
    }
}