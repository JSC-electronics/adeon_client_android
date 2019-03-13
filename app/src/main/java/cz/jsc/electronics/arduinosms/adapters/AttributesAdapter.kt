package cz.jsc.electronics.arduinosms.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cz.jsc.electronics.arduinosms.data.Attribute
import cz.jsc.electronics.arduinosms.databinding.ListItemAttributeBinding


/**
 * Adapter for the [RecyclerView] in [AddDeviceFragment].
 */
class AttributesAdapter : ListAdapter<Attribute, AttributesAdapter.ViewHolder>(AttributesDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val attribute = getItem(position)
        holder.apply {
            bind(attribute)
            itemView.tag = attribute
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ListItemAttributeBinding.inflate(
                LayoutInflater.from(parent.context), parent, false))
    }

    class ViewHolder(
        private val binding: ListItemAttributeBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Attribute) {
            binding.apply {
                attribute = item
                executePendingBindings()
            }
        }
    }
}

private class AttributesDiffCallback : DiffUtil.ItemCallback<Attribute>() {

    override fun areItemsTheSame(oldItem: Attribute, newItem: Attribute): Boolean {
        return oldItem.key == newItem.key
    }

    override fun areContentsTheSame(oldItem: Attribute, newItem: Attribute): Boolean {
        return oldItem.key == newItem.key && oldItem.value == newItem.value
    }
}