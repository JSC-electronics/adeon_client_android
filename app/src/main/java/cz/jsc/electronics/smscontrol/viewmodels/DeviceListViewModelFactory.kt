package cz.jsc.electronics.smscontrol.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cz.jsc.electronics.smscontrol.data.DeviceRepository

/**
 * Factory for creating a [DeviceListViewModel] with a constructor that takes a [DeviceRepository].
 */
class DeviceListViewModelFactory(
    private val context: Context,
    private val repository: DeviceRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) = DeviceListViewModel(context, repository) as T
}
