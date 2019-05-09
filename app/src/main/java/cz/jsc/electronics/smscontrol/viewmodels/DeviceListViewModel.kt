package cz.jsc.electronics.smscontrol.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.jsc.electronics.smscontrol.data.Device
import cz.jsc.electronics.smscontrol.data.DeviceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import cz.jsc.electronics.smscontrol.DeviceListFragment

/**
 * The ViewModel for [DeviceListFragment].
 */
class DeviceListViewModel internal constructor(
    private val deviceRepository: DeviceRepository
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
                val duplicate = device.copy(deviceId = 0)
                deviceRepository.addDevice(duplicate)
            }
        }
    }
}
