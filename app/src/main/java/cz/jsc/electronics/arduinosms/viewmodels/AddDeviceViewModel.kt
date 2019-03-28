package cz.jsc.electronics.arduinosms.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.jsc.electronics.arduinosms.AddDeviceFragment
import cz.jsc.electronics.arduinosms.adapters.AttributesAdapter
import cz.jsc.electronics.arduinosms.data.Attribute
import cz.jsc.electronics.arduinosms.data.Device
import cz.jsc.electronics.arduinosms.data.DeviceRepository
import kotlinx.coroutines.launch

/**
 * The ViewModel for [AddDeviceFragment].
 */
class AddDeviceViewModel internal constructor(
    private val deviceRepository: DeviceRepository,
    private val deviceId: Long?
) : ViewModel() {

    val device: LiveData<Device> = if (deviceId != null) deviceRepository.getDevice(deviceId)
    else MutableLiveData(Device(name = "", location = null, phoneNumber = ""))

    val attributeAdapter = AttributesAdapter()

    fun isEditingDevice(): Boolean {
        return deviceId != null
    }

    fun addNewAttribute() {
        device.value?.let {
            it.attributes.add(Attribute(it.attributes.size.toLong()))
            attributeAdapter.submitList(it.attributes.toList())
        }
    }

    fun isAttributeListValid(): Boolean {
        device.value?.apply {
            attributes.forEach {
                if ((it.key.isNullOrEmpty() && it.value != null) ||
                    (!it.key.isNullOrEmpty() && (it.value == null || it.value!! < 0 || it.value!! > 65535))
                ) {
                    return false
                }
            }
        }

        return true
    }

    fun isAttributeListEmpty(): Boolean {
        device.value?.apply {
            attributes.forEach {
                if (!it.key.isNullOrEmpty())
                    return false
            }
        }

        return true
    }

    fun addOrUpdateDevice() {
        viewModelScope.launch {

            device.value?.let {
                if (deviceId == null) {
                    deviceRepository.addDevice(it)
                } else {
                    deviceRepository.updateDevice(it)
                }
            }
        }
    }
}
