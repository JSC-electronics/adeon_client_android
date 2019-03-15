package cz.jsc.electronics.arduinosms.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.jsc.electronics.arduinosms.data.Attribute
import cz.jsc.electronics.arduinosms.data.Device
import cz.jsc.electronics.arduinosms.data.DeviceRepository
import kotlinx.coroutines.launch

/**
 * The ViewModel for [AddDeviceFragment].
 */
class AddDeviceViewModel internal constructor(
    private val deviceRepository: DeviceRepository,
    deviceId: Long?
) : ViewModel() {

    private var device: LiveData<Device>? = null
    private val attributes = ArrayList<Attribute>()
    private val attributesLiveData = MutableLiveData<List<Attribute>>()

    init {
        deviceId?.let {
            device = deviceRepository.getDevice(it)
        }

        if (attributes.isEmpty()) {
            attributes.add(Attribute())
        }
        attributesLiveData.value = attributes
    }

    fun getDevice(): LiveData<Device>? {
        return device
    }

    fun isEditingDevice(): Boolean {
        return device != null
    }

    fun getAttributes(): MutableLiveData<List<Attribute>> {
        return attributesLiveData
    }

    fun addNewAttribute() {
        attributes.add(Attribute())
        attributesLiveData.value = attributes
    }

    fun addDeviceToList(name: String, location: String?, phoneNumber: String, attributes: String?) {
        viewModelScope.launch {
            deviceRepository.addDevice(
                Device(
                    name = name,
                    location = location,
                    phoneNumber = phoneNumber,
                    attributes = attributes
                )
            )
        }
    }
}
