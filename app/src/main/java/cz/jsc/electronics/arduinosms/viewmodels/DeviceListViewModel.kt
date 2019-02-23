package cz.jsc.electronics.arduinosms.viewmodels

import androidx.lifecycle.*
import cz.jsc.electronics.arduinosms.data.Device
import cz.jsc.electronics.arduinosms.data.DeviceRepository

/**
 * The ViewModel for [DeviceListFragment].
 */
class DeviceListViewModel internal constructor(
    deviceRepository: DeviceRepository
) : ViewModel() {

    private val deviceList = deviceRepository.getDevices()

    fun getDevices() = deviceList
}
