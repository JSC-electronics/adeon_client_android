package cz.jsc.electronics.arduinosms.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.jsc.electronics.arduinosms.AddDeviceFragment
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

    private var device: LiveData<Device>? = null
    private val attributes = ArrayList<Attribute>()
    private val attributesLiveData = MutableLiveData<List<Attribute>>()

    init {
        deviceId?.let {
            device = deviceRepository.getDevice(it)
        }

        if (attributes.isEmpty()) {
            attributes.add(Attribute(0))
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
        attributes.add(Attribute(attributes.size.toLong()))
        attributesLiveData.value = attributes
    }

    fun restoreAttributes(attrString: String) {
        attributes.clear()

        attrString.split(";").dropLast(1).forEach {
            val attrs = it.split("=")
            if (attrs.size == 2) {
                attributes.add(Attribute(attributes.size.toLong(), attrs[0].trim(), attrs[1].trim().toIntOrNull()))
            }
        }
        attributesLiveData.value = attributes
    }

    fun isAttributeListValid(): Boolean {
        attributes.forEach {
            if ((it.key.isNullOrEmpty() && it.value != null) ||
                (!it.key.isNullOrEmpty() && (it.value == null || it.value!! < 0 || it.value!! > 65535))) {
                return false
            }
        }

        return true
    }

    fun isAttributeListEmpty(): Boolean {
        attributes.forEach {
            if (!it.key.isNullOrEmpty())
                return false
        }
        return true
    }

    fun addOrUpdateDevice(name: String, location: String?, phoneNumber: String) {
        viewModelScope.launch {
            val entry = Device(
                deviceId = deviceId ?: 0,
                name = name,
                location = location,
                phoneNumber = phoneNumber,
                attributes = attributes.filter { it.key != null && it.value != null }.
                    joinToString (separator = ";", postfix = ";")
            )

            // Update entry
            if (entry.deviceId > 0) {
                deviceRepository.updateDevice(entry)
            } else {
                deviceRepository.addDevice(entry)
            }
        }
    }
}
