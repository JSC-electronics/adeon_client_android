package cz.jsc.electronics.smscontrol.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cz.jsc.electronics.smscontrol.AddDeviceFragment
import cz.jsc.electronics.smscontrol.R
import cz.jsc.electronics.smscontrol.data.Attribute
import cz.jsc.electronics.smscontrol.databinding.ListItemAttributeBinding


/**
 * Adapter for the [RecyclerView] in [AddDeviceFragment].
 */
class AttributesAdapter(private val showCheckbox: Boolean = false, private var preferPlainText: Boolean = false) :
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
            binding.content.apply {
                attribute = item
                showPlainText = adapter.preferPlainText
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
                            if (it > Attribute.ATTRIBUTE_VAL_MAX || it < Attribute.ATTRIBUTE_VAL_MIN) {
                                attributeValue.error = binding.root.context.getString(R.string.attribute_value_out_of_range)
                            } else {
                                attributeValue.error = null
                            }
                        }
                    }
                }
                plainEditText.addTextChangedListener {
                    attribute?.apply {
                        text = plainEditText.text.toString()
                    }
                }
                attributeCheckbox.isVisible = adapter.showCheckbox
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
        return oldItem.key == newItem.key && oldItem.value == newItem.value && oldItem.text == newItem.text
    }
}