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
        private const val CHECKSUM_SIZE = 5
        private const val ATTRIBUTE_VAL_MIN = 0
        private const val ATTRIBUTE_VAL_MAX = 65535

        /* We've limited the field names to basic ASCII characters only (code below 128),
           which allows for 7-bit encoding. We won't ever send multipart SMS messages
           (due to limitations on receiving end) and hence we don't need to count the overhead.
         */
        private const val MAX_MESSAGE_SIZE = 160

        /* PAYLOAD are in this context comma-separated fields, i.e. attr1 = 1; attr2 = 2; ...
           We also need to take into account part containing MD5 hash and separating characters.
         */
        private const val MAX_PAYLOAD_SIZE = MAX_MESSAGE_SIZE - CHECKSUM_SIZE - 2
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

                composeMessage(smsAttributes).forEach { message ->
                    val md5 = computeMd5(message)
                    val smsMessage = "${md5.substring(md5.length - CHECKSUM_SIZE)}: $message"
                    smsManager.sendTextMessage(
                        device.phoneNumber, null, smsMessage,
                        null, null
                    )
                }
            }
        }
    }

    private fun composeMessage(attributes: List<Attribute>): List<String> {
        val messages = ArrayList<String>()
        var message = ""

        attributes.forEach { attribute ->
            // If we append the attribute, max message length will be exceeded
            if (message.length + attribute.toString().length + 1 > MAX_PAYLOAD_SIZE) {
                messages.add(message)
                message = "$attribute;"
            } else {
                message += "$attribute;"
            }
        }

        // Put last message into the list
        if (message.isNotEmpty()) {
            messages.add(message)
        }

        return messages
    }
}
