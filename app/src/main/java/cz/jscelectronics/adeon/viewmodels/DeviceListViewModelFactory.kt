package cz.jscelectronics.adeon.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cz.jscelectronics.adeon.data.DeviceRepository

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
