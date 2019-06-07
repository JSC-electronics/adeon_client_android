package cz.jscelectronics.adeon.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cz.jscelectronics.adeon.AddDeviceFragment
import cz.jscelectronics.adeon.R
import cz.jscelectronics.adeon.data.Attribute
import cz.jscelectronics.adeon.databinding.ListItemAttributeBinding


/**
 * Adapter for the [RecyclerView] in [AddDeviceFragment].
 */
class AttributesAdapter(private val isEditMode: Boolean = false, private var preferPlainText: Boolean = false) :
    ListAdapter<Attribute, AttributesAdapter.ViewHolder>(AttributesDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val attribute = getItem(position)
        holder.apply {
            bind(attribute)
            itemView.tag = attribute
        }
    }

    fun setAttributeFormat(preferPlainText: Boolean = false) {
        this.preferPlainText = preferPlainText
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ListItemAttributeBinding.inflate(
                LayoutInflater.from(parent.context), parent, false),
                    this)
    }

    class ViewHolder(
        private val binding: ListItemAttributeBinding,
        private val adapter: AttributesAdapter
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Attribute) {
            binding.apply {
                attribute = item
                showPlainText = adapter.preferPlainText
                isEditMode = adapter.isEditMode
                executePendingBindings()
                nameEditText.addTextChangedListener {
                    attribute?.apply {
                        name = nameEditText.text.toString()
                    }
                }
                valueEditText.addTextChangedListener {
                    attribute?.apply {
                        value = valueEditText.text.toString().toIntOrNull()
                        value?.let {
                            if (it > Attribute.ATTRIBUTE_VAL_MAX || it < Attribute.ATTRIBUTE_VAL_MIN) {
                                attributeValue.error = binding.root.context.getString(R.string.attribute_value_out_of_range)
                            } else {
                                attributeValue.error = null
                            }
                        }
                    }
                }
                commandDescEditText.addTextChangedListener {
                    attribute?.apply {
                        name = commandDescEditText.text.toString()
                    }
                }
                plainEditText.addTextChangedListener {
                    attribute?.apply {
                        text = plainEditText.text.toString()
                    }
                }
                attributeCheckbox.setOnCheckedChangeListener { _, isChecked ->
                    attribute?.apply {
                        this.isChecked = isChecked
                    }
                }
            }
        }
    }
}

private class AttributesDiffCallback : DiffUtil.ItemCallback<Attribute>() {

    override fun areItemsTheSame(oldItem: Attribute, newItem: Attribute): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: Attribute, newItem: Attribute): Boolean {
        return oldItem.name == newItem.name && oldItem.value == newItem.value && oldItem.text == newItem.text
    }
}