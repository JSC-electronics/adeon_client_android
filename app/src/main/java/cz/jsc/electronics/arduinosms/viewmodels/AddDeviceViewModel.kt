package cz.jsc.electronics.arduinosms.viewmodels

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
    private val deviceRepository: DeviceRepository
) : ViewModel() {

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

    private val attributes = ArrayList<Attribute>()
    private val attributesLiveData = MutableLiveData<List<Attribute>>()

    init {
        if (attributes.isEmpty()) {
            attributes.add(Attribute())
        }
        attributesLiveData.value = attributes
    }

    fun getAttributes(): MutableLiveData<List<Attribute>> {
        return attributesLiveData
    }

    fun addNewAttribute() {
        attributes.add(Attribute())
        attributesLiveData.value = attributes
    }
}
