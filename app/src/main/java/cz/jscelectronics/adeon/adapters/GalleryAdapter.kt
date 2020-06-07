package cz.jscelectronics.adeon.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cz.jscelectronics.adeon.databinding.ListItemGalleryIconBinding
import cz.jscelectronics.adeon.ui.device.AddDeviceFragment


/**
 * Adapter for the [RecyclerView] in [AddDeviceFragment].
 */
open class GalleryAdapter :
    ListAdapter<Int, GalleryAdapter.ViewHolder>(ImagesDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ListItemGalleryIconBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ), this
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val imageId = getItem(position)
        holder.apply {
            bind(imageId)
            itemView.tag = imageId
        }
    }

    open fun onClick(iconResId: Int) {}

    class ViewHolder(
        private val binding: ListItemGalleryIconBinding,
        private val adapter: GalleryAdapter
    ) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        fun bind(imageId: Int) {
            binding.apply {
                deviceImage.setImageResource(imageId)
            }

            binding.deviceImage.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            adapter.onClick(adapter.getItem(absoluteAdapterPosition))
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
