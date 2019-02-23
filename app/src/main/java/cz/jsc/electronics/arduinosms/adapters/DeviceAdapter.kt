package cz.jsc.electronics.arduinosms.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cz.jsc.electronics.arduinosms.DeviceListFragmentDirections
import cz.jsc.electronics.arduinosms.data.Device
import cz.jsc.electronics.arduinosms.databinding.ListItemDeviceBinding

/**
 * Adapter for the [RecyclerView] in [DeviceListFragment].
 */
class DeviceAdapter : ListAdapter<Device, DeviceAdapter.ViewHolder>(DeviceDiffCallback()) {

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
                LayoutInflater.from(parent.context), parent, false))
    }

    private fun createOnClickListener(deviceId: Long): View.OnClickListener {
        return View.OnClickListener {
            //val direction = DeviceListFragmentDirections.actionPlantListFragmentToPlantDetailFragment(plantId)
            //it.findNavController().navigate(direction)
        }
    }

    class ViewHolder(
        private val binding: ListItemDeviceBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(listener: View.OnClickListener, item: Device) {
            binding.apply {
                clickListener = listener
                device = item
                executePendingBindings()
            }
        }
    }
}

private class DeviceDiffCallback : DiffUtil.ItemCallback<Device>() {

    override fun areItemsTheSame(oldItem: Device, newItem: Device): Boolean {
        return oldItem.deviceId == newItem.deviceId
    }

    override fun areContentsTheSame(oldItem: Device, newItem: Device): Boolean {
        return oldItem == newItem
    }
}