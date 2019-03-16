package cz.jsc.electronics.arduinosms.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cz.jsc.electronics.arduinosms.data.DeviceRepository

/**
 * Factory for creating a [SendSmsViewModel] with a constructor that takes a [DeviceRepository].
 */
class SendSmsViewModelFactory(
    private val repository: DeviceRepository,
    private val deviceId: Long
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) = SendSmsViewModel(repository, deviceId) as T
}
