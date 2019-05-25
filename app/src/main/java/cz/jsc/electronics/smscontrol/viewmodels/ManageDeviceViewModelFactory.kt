package cz.jsc.electronics.smscontrol.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cz.jsc.electronics.smscontrol.data.DeviceRepository

/**
 * Factory for creating a [ManageDeviceViewModel] with a constructor that takes a [DeviceRepository].
 */
class ManageDeviceViewModelFactory(
    private val context: Context,
    private val repository: DeviceRepository,
    private val deviceId: Long?
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) = ManageDeviceViewModel(context, repository, deviceId) as T
}
