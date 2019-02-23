package cz.jsc.electronics.arduinosms.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cz.jsc.electronics.arduinosms.data.DeviceRepository

/**
 * Factory for creating a [AddDeviceViewModel] with a constructor that takes a [DeviceRepository].
 */
class AddDeviceViewModelFactory(
    private val repository: DeviceRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) = AddDeviceViewModel(repository) as T
}
