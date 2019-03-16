package cz.jsc.electronics.arduinosms.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cz.jsc.electronics.arduinosms.AddDeviceFragment
import cz.jsc.electronics.arduinosms.R
import cz.jsc.electronics.arduinosms.data.Attribute
import cz.jsc.electronics.arduinosms.databinding.ListItemAttributeBinding


/**
 * Adapter for the [RecyclerView] in [AddDeviceFragment].
 */
class AttributesAdapter(private val showCheckbox: Boolean = false) : ListAdapter<Attribute, AttributesAdapter.ViewHolder>(AttributesDiffCallback()) {

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
                LayoutInflater.from(parent.context), parent, false), showCheckbox)
    }

    class ViewHolder(
        private val binding: ListItemAttributeBinding,
        private val showCheckbox: Boolean
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Attribute) {
            binding.apply {
                attribute = item
                executePendingBindings()
                keyEditText.addTextChangedListener {
                    attribute?.apply {
                        key = keyEditText.text.toString()
                    }
                }
                valueEditText.addTextChangedListener {
                    attribute?.apply {
                        value = valueEditText.text.toString().toIntOrNull()
                        value?.let {
                            if (it > 65535 || it < 0) {
                                attributeValue.error = binding.root.context.getString(R.string.attribute_value_out_of_range)
                            } else {
                                attributeValue.error = null
                            }
                        }
                    }
                }
                if (showCheckbox) {
                    binding.attributeCheckbox.isVisible = showCheckbox
                    attributeCheckbox.setOnCheckedChangeListener { _, isChecked ->
                        attribute?.apply {
                            checked = isChecked
                        }
                    }
                }
            }
        }
    }
}

private class AttributesDiffCallback : DiffUtil.ItemCallback<Attribute>() {

    override fun areItemsTheSame(oldItem: Attribute, newItem: Attribute): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Attribute, newItem: Attribute): Boolean {
        return oldItem.key == newItem.key && oldItem.value == newItem.value
    }
}