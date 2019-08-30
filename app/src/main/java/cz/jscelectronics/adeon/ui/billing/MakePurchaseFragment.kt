package cz.jscelectronics.adeon.ui.billing


import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cz.jscelectronics.adeon.adapters.SkuDetailsAdapter
import cz.jscelectronics.adeon.billingrepo.localdb.AugmentedSkuDetails
import cz.jscelectronics.adeon.databinding.FragmentMakePurchaseBinding
import cz.jscelectronics.adeon.ui.billing.viewmodels.BillingViewModel
import kotlinx.android.synthetic.main.fragment_make_purchase.view.*

/**
 * This Fragment is simply a wrapper for the inventory (i.e. items for sale). It contains two
 * [lists][RecyclerView], one for subscriptions and one for in-app products. Here again there is
 * no complicated billing logic. All the billing logic reside inside the [BillingRepository].
 * The [BillingRepository] provides a so-called [AugmentedSkuDetails] object that shows what
 * is for sale and whether the user is allowed to buy the item at this moment. E.g. if the user
 * already has a full tank of gas, then they cannot buy gas at this moment.
 */
class MakePurchaseFragment : Fragment() {

    val LOG_TAG = "MakePurchaseFragment"
    private lateinit var billingViewModel: BillingViewModel
    private lateinit var binding: FragmentMakePurchaseBinding

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = FragmentMakePurchaseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(LOG_TAG, "onViewCreated")

        val inappAdapter = object : SkuDetailsAdapter() {
            override fun onSkuDetailsClicked(item: AugmentedSkuDetails) {
                onPurchase(view, item)
            }
        }
        attachAdapterToRecyclerView(view.inapp_inventory, inappAdapter)

        billingViewModel = ViewModelProvider(this).get(BillingViewModel::class.java)
        billingViewModel.inappSkuDetailsListLiveData.observe(this, Observer {
            it?.let { inappAdapter.setSkuDetailsList(it) }
            binding.hasPurchases = it.isNotEmpty()
        })
    }

    private fun attachAdapterToRecyclerView(recyclerView: RecyclerView, skuAdapter: SkuDetailsAdapter) {
        with(recyclerView) {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = skuAdapter
        }
    }

    private fun onPurchase(view: View, item: AugmentedSkuDetails) {
        billingViewModel.makePurchase(activity as Activity, item)
        Log.d(LOG_TAG, "starting purchase flow for SkuDetail:\n ${item}")
    }
}
