package cz.jsc.electronics.arduinosms.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.jsc.electronics.arduinosms.data.Device
import cz.jsc.electronics.arduinosms.data.DeviceRepository
import kotlinx.coroutines.launch

/**
 * The ViewModel for [AddDeviceFragment].
 */
class AddDeviceViewModel internal constructor(
    private val deviceRepository: DeviceRepository
) : ViewModel() {

    fun addDeviceToList(name: String, location: String?, phoneNumber: String, attributes: String?) {
        viewModelScope.launch {
            deviceRepository.addDevice(
                Device(
                    name = name,
                    location = location,
                    phoneNumber = phoneNumber,
                    attributes = attributes)
            )
        }
    }
}
