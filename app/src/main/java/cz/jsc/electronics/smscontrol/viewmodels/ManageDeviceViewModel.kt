package cz.jsc.electronics.smscontrol.viewmodels

import android.telephony.SmsManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.jsc.electronics.smscontrol.AddDeviceFragment
import cz.jsc.electronics.smscontrol.SendSmsFragment
import cz.jsc.electronics.smscontrol.adapters.AttributesAdapter
import cz.jsc.electronics.smscontrol.data.Attribute
import cz.jsc.electronics.smscontrol.data.Device
import cz.jsc.electronics.smscontrol.data.DeviceRepository
import cz.jsc.electronics.smscontrol.utilities.computeMd5
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

    private var attributes = mutableListOf<Attribute>()

    // For Singleton instantiation
    @Volatile
    private var attributesAdapter: AttributesAdapter? = null

    fun getAttributesAdapter(showCheckbox: Boolean = false) =
        attributesAdapter ?: synchronized(this) {
            attributesAdapter ?: AttributesAdapter(showCheckbox).also { attributesAdapter = it }
        }

    fun initAttributes(device: Device) {
        attributes = device.attributes.map { it.copy(id = it.id, key = it.key, value = it.value,
            text = it.text, isChecked = it.isChecked) }.toMutableList()

        if (attributes.isEmpty()) {
            attributes.add(Attribute())
        }

        attributesAdapter?.submitList( attributes.toList())
    }

    fun isEditingDevice(): Boolean {
        return deviceId != null
    }

    fun addNewAttribute() {
        attributes.add(Attribute(attributes.size.toLong()))
        attributesAdapter?.submitList(attributes.toList())
    }

    fun isAttributeListValid(): Boolean {
        attributes.forEach {
            if ((it.key.isNullOrEmpty() && it.value != null) ||
                (!it.key.isNullOrEmpty() && (it.value == null || it.value!! < ATTRIBUTE_VAL_MIN || it.value!! > ATTRIBUTE_VAL_MAX))
            ) {
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

    fun addOrUpdateDevice() {
        viewModelScope.launch {

            device.value?.let { device ->
                if (device.attributes.isEmpty() || device.attributes.size != attributes.size) {
                    device.attributes = attributes.toList()
                } else {
                    val checkedAttributes = attributes.filter { it.isChecked }.map { it.id }.toSet()

                    device.attributes.onEach { attribute ->
                        attribute.isChecked = attribute.id in checkedAttributes
                    }
                }

                if (deviceId == null) {
                    deviceRepository.addDevice(device)
                } else {
                    deviceRepository.updateDevice(device)
                }
            }
        }
    }

    fun sendSmsMessage() {
        viewModelScope.launch {
            device.value?.let { device ->
                val smsManager = SmsManager.getDefault()

                val smsAttributes = attributes.filter { it.isChecked }

                composeMessage(smsAttributes).forEach { message ->
                    val md5 = computeMd5(message)
                    val smsMessage = "${md5.substring(md5.length - CHECKSUM_SIZE)}: $message"
                    smsManager.sendTextMessage(
                        device.phoneNumber, null, smsMessage,
                        null, null
                    )
                }

                // Store which attributes are checked
                addOrUpdateDevice()
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
