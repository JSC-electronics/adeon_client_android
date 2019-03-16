package cz.jsc.electronics.arduinosms.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cz.jsc.electronics.arduinosms.SendSmsFragment
import cz.jsc.electronics.arduinosms.data.Attribute
import cz.jsc.electronics.arduinosms.data.Device
import cz.jsc.electronics.arduinosms.data.DeviceRepository

/**
 * The ViewModel for [SendSmsFragment].
 */
class SendSmsViewModel internal constructor(
    deviceRepository: DeviceRepository,
    deviceId: Long
) : ViewModel() {

    val device: LiveData<Device> = deviceRepository.getDevice(deviceId)
    private val attributes = ArrayList<Attribute>()
    private val attributesLiveData = MutableLiveData<List<Attribute>>()

    init {
        attributesLiveData.value = attributes
    }

    fun getAttributes(): MutableLiveData<List<Attribute>> {
        return attributesLiveData
    }

    fun restoreAttributes(attrString: String) {
        attributes.clear()

        attrString.split(";").dropLast(1).forEach {
            val attrs = it.split("=")
            attributes.add(Attribute(attributes.size.toLong(), attrs[0].trim(), attrs[1].trim().toIntOrNull()))
        }
        attributesLiveData.value = attributes
    }

    fun sendSmsMessage() {

    }
}
