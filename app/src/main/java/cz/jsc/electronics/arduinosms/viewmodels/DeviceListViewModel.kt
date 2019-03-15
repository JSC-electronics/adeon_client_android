package cz.jsc.electronics.arduinosms.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.jsc.electronics.arduinosms.data.Device
import cz.jsc.electronics.arduinosms.data.DeviceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * The ViewModel for [DeviceListFragment].
 */
class DeviceListViewModel internal constructor(
    val deviceRepository: DeviceRepository
) : ViewModel() {

    private val deviceList = deviceRepository.getDevices()

    fun getDevices() = deviceList

    fun deleteDevice(device: Device) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                deviceRepository.deleteDevice(device)
            }
        }
    }

    fun duplicateDevice(device: Device) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val duplicate = Device(name = device.name, location = device.location,
                    phoneNumber = device.phoneNumber, attributes = device.attributes)

                deviceRepository.addDevice(duplicate)
            }
        }
    }
}
