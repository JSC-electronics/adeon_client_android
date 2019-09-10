package cz.jscelectronics.adeon.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cz.jscelectronics.adeon.databinding.ListItemGalleryIconBinding
import cz.jscelectronics.adeon.ui.device.AddDeviceFragment


/**
 * Adapter for the [RecyclerView] in [AddDeviceFragment].
 */
class GalleryAdapter :
    ListAdapter<Int, GalleryAdapter.ViewHolder>(ImagesDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ListItemGalleryIconBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val imageId = getItem(position)
        holder.apply {
            bind(imageId)
            itemView.tag = imageId
        }
    }

    class ViewHolder(
        private val binding: ListItemGalleryIconBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(imageId: Int) {
            binding.apply {
//                deviceImage.setImageResource(imageId)
//                deviceImage.setOnClickListener {
//                    it.findNavController().navigateUp()
//                }
            }
        }
    }
}

private class ImagesDiffCallback : DiffUtil.ItemCallback<Int>() {

    override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean {
        return oldItem == newItem
    }
}
