package cz.jsc.electronics.arduinosms.viewmodels

import android.telephony.SmsManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.jsc.electronics.arduinosms.AddDeviceFragment
import cz.jsc.electronics.arduinosms.SendSmsFragment
import cz.jsc.electronics.arduinosms.adapters.AttributesAdapter
import cz.jsc.electronics.arduinosms.data.Attribute
import cz.jsc.electronics.arduinosms.data.Device
import cz.jsc.electronics.arduinosms.data.DeviceRepository
import cz.jsc.electronics.arduinosms.utilities.computeMd5
import kotlinx.coroutines.launch

/**
 * The ViewModel for managing device properties in [AddDeviceFragment] and [SendSmsFragment].
 */
class ManageDeviceViewModel internal constructor(
    private val deviceRepository: DeviceRepository,
    private val deviceId: Long?
) : ViewModel() {

    companion object {
        const val CHECKSUM_SIZE = 5
        const val ATTRIBUTE_VAL_MIN = 0
        const val ATTRIBUTE_VAL_MAX = 65535
    }

    val device: LiveData<Device> = if (deviceId != null) deviceRepository.getDevice(deviceId)
    else MutableLiveData(Device(name = "", location = null, phoneNumber = ""))

    // For Singleton instantiation
    @Volatile
    private var attributesAdapter: AttributesAdapter? = null

    fun getAttributesAdapter(showCheckbox: Boolean = false) =
        attributesAdapter ?: synchronized(this) {
            attributesAdapter ?: AttributesAdapter(showCheckbox).also { attributesAdapter = it }
        }

    fun isEditingDevice(): Boolean {
        return deviceId != null
    }

    fun addNewAttribute() {
        device.value?.let {
            it.attributes.add(Attribute(it.attributes.size.toLong()))
            attributesAdapter?.submitList(it.attributes.toList())
        }
    }

    fun isAttributeListValid(): Boolean {
        device.value?.apply {
            attributes.forEach {
                if ((it.key.isNullOrEmpty() && it.value != null) ||
                    (!it.key.isNullOrEmpty() && (it.value == null || it.value!! < ATTRIBUTE_VAL_MIN || it.value!! > ATTRIBUTE_VAL_MAX))
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

    fun sendSmsMessage() {
        viewModelScope.launch {
            device.value?.let { device ->
                val smsManager = SmsManager.getDefault()

                val smsAttributes = device.attributes.filter { it.checked && it.key != null && it.value != null }
                    .joinToString(separator = ";", postfix = ";")
                val md5 = computeMd5(smsAttributes)
                val smsMessage =
                    smsManager.divideMessage("${md5.substring(md5.length - CHECKSUM_SIZE)}: $smsAttributes")
                smsManager.sendMultipartTextMessage(
                    device.phoneNumber,
                    null, smsMessage, null, null
                )
            }
        }
    }
}
