package cz.jscelectronics.adeon.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import cz.jscelectronics.adeon.R
import cz.jscelectronics.adeon.billingrepo.localdb.AugmentedSkuDetails
import kotlinx.android.synthetic.main.purchase_inventory_item.view.*

/**
 * This is an [AugmentedSkuDetails] adapter. It can be used anywhere there is a need to display a
 * list of AugmentedSkuDetails. In this app it's used to display both the list of subscriptions and
 * the list of in-app products.
 */
open class SkuDetailsAdapter : RecyclerView.Adapter<SkuDetailsAdapter.SkuDetailsViewHolder>() {

    private var skuDetailsList = emptyList<AugmentedSkuDetails>()

    override fun getItemCount() = skuDetailsList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkuDetailsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
                R.layout.purchase_inventory_item, parent, false
        )
        return SkuDetailsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SkuDetailsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun getItem(position: Int) = if (skuDetailsList.isEmpty()) null else skuDetailsList[position]

    fun setSkuDetailsList(list: List<AugmentedSkuDetails>) {
        if (list != skuDetailsList) {
            skuDetailsList = list
            notifyDataSetChanged()
        }
    }

    /**
     * In the spirit of keeping simple things simple: this is a friendly way of allowing clients
     * to listen to clicks. You should consider doing this for all your other adapters.
     */
    open fun onSkuDetailsClicked(item: AugmentedSkuDetails) {
        //clients to implement for callback if needed
    }

    inner class SkuDetailsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                getItem(absoluteAdapterPosition)?.let { onSkuDetailsClicked(it) }
            }
        }

        fun bind(item: AugmentedSkuDetails?) {
            item?.apply {
                itemView.apply {
                    val name = title?.substring(0, title.indexOf("("))
                    sku_title.text = name
                    sku_description.text = description
                    sku_price.text = price
                    val drawableId = getSkuDrawableId(sku, this)
                    sku_image.setImageResource(drawableId)
                    isEnabled = canPurchase
                    onDisabled(canPurchase)
                }
            }
        }

        private fun onDisabled(enabled: Boolean) {
            if (enabled) {
                itemView.apply {
                    sku_title.setTextColor(ContextCompat.getColor(context, R.color.Text))
                    sku_description.setTextColor(ContextCompat.getColor(context, R.color.Text))
                    sku_price.setTextColor(ContextCompat.getColor(context, R.color.Text))
                }
            } else {
                itemView.apply {
                    setBackgroundColor(ContextCompat.getColor(context, R.color.imgDisableHint))
                    val color = ContextCompat.getColor(context, R.color.textDisabledHint)
                    sku_title.setTextColor(color)
                    sku_description.setTextColor(color)
                    sku_price.setTextColor(color)
                }
            }
        }

        /**
         * Keeping simple things simple, the icons are named after the SKUs. This way, there is no
         * need to create some elaborate system for matching icons to SKUs when displaying the
         * inventory to users. It is sufficient to do
         *
         * ```
         * sku_image.setImageResource(resources.getIdentifier(sku, "drawable", view.context.packageName))
         *
         * ```
         *
         * Alternatively, in the case where more than one SKU should match the same drawable,
         * you can check with a when{} block. In this sample app, for instance, both gold_monthly and
         * gold_yearly should match the same gold_subs_icon; so instead of keeping two copies of
         * the same icon, when{} is used to set imgName
         */
        private fun getSkuDrawableId(sku: String, view: View): Int {
            return view.resources.getIdentifier(sku, "drawable",
                    view.context.packageName)
        }
    }
}