package cz.jscelectronics.adeon.ui.billing.viewmodels

import android.app.Activity
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import cz.jscelectronics.adeon.billingrepo.BillingRepository
import cz.jscelectronics.adeon.billingrepo.localdb.AugmentedSkuDetails
import cz.jscelectronics.adeon.billingrepo.localdb.NoAdvertisements
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel

/**
 * Notice just how small and simple this BillingViewModel is!!
 *
 * This beautiful simplicity is the result of keeping all the hard work buried inside the
 * [BillingRepository] and only inside the [BillingRepository]. The rest of your app
 * is now free from [BillingClient] tentacles!! And this [BillingViewModel] is the one and only
 * object the rest of your Android team need to know about billing.
 *
 */
class BillingViewModel(application: Application) : AndroidViewModel(application) {

    val noAdvertisementsLiveData: LiveData<NoAdvertisements>
    val inappSkuDetailsListLiveData: LiveData<List<AugmentedSkuDetails>>

    private val LOG_TAG = "BillingViewModel"
    private val viewModelScope = CoroutineScope(Job() + Dispatchers.Main)
    private val repository: BillingRepository = BillingRepository.getInstance(application)

    init {
        repository.startDataSourceConnections()
        noAdvertisementsLiveData = repository.noAdvertisementsLiveData
        inappSkuDetailsListLiveData = repository.inappSkuDetailsListLiveData
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(LOG_TAG, "onCleared")
        repository.endDataSourceConnections()
        viewModelScope.coroutineContext.cancel()
    }

    fun makePurchase(activity: Activity, augmentedSkuDetails: AugmentedSkuDetails) {
        repository.launchBillingFlow(activity, augmentedSkuDetails)
    }
}