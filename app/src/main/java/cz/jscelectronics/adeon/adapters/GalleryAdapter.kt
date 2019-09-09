package cz.jscelectronics.adeon.adapters

import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cz.jscelectronics.adeon.databinding.ListItemGalleryIconBinding
import cz.jscelectronics.adeon.ui.device.AddDeviceFragment


/**
 * Adapter for the [RecyclerView] in [AddDeviceFragment].
 */
class GalleryAdapter(private val dataSet: TypedArray) :
    RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ListItemGalleryIconBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {
            dataSet.getDrawable(position)?.let {
                bind(it)
                itemView.tag = it
            }
        }
    }

    override fun getItemCount(): Int {
        return dataSet.length()
    }

    class ViewHolder(private val binding: ListItemGalleryIconBinding) :
        RecyclerView.ViewHolder(binding.galleryCard) {
        fun bind(item: Drawable) {
            binding.apply {
                deviceImage.setImageDrawable(item)
            }
        }
    }
}
