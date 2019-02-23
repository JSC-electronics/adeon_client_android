package cz.jsc.electronics.arduinosms.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cz.jsc.electronics.arduinosms.data.DeviceRepository

/**
 * Factory for creating a [DeviceListViewModel] with a constructor that takes a [DeviceRepository].
 */
class DeviceListViewModelFactory(
    private val repository: DeviceRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) = DeviceListViewModel(repository) as T
}
