package cz.jsc.electronics.arduinosms.viewmodels

import android.telephony.SmsManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.jsc.electronics.arduinosms.SendSmsFragment
import cz.jsc.electronics.arduinosms.data.Attribute
import cz.jsc.electronics.arduinosms.data.Device
import cz.jsc.electronics.arduinosms.data.DeviceRepository
import kotlinx.coroutines.launch
import java.security.NoSuchAlgorithmException


/**
 * The ViewModel for [SendSmsFragment].
 */
class SendSmsViewModel internal constructor(
    deviceRepository: DeviceRepository,
    deviceId: Long
) : ViewModel() {

    val device: LiveData<Device> = deviceRepository.getDevice(deviceId)
    private var deviceData: Device? = null
    private val attributes = ArrayList<Attribute>()
    private val attributesLiveData = MutableLiveData<List<Attribute>>()

    init {
        attributesLiveData.value = attributes
    }

    fun getAttributes(): MutableLiveData<List<Attribute>> {
        return attributesLiveData
    }

    fun restoreData(device: Device) {
        deviceData = device

        device.attributes?.let { attributes ->
            this.attributes.clear()

            attributes.split(";").dropLast(1).forEach {
                val attrs = it.split("=")
                this.attributes.add(
                    Attribute(
                        this.attributes.size.toLong(),
                        attrs[0].trim(), attrs[1].trim().toIntOrNull()
                    )
                )
            }
            attributesLiveData.value = this.attributes
        }
    }

    fun sendSmsMessage() {
        viewModelScope.launch {
            deviceData?.let { device ->
                val smsManager = SmsManager.getDefault()

                val smsAttributes = attributes.filter { it.checked && it.key != null && it.value != null }
                    .joinToString(separator = ";", postfix = ";")
                val md5 = computeMd5(smsAttributes)
                val smsMessage = smsManager.divideMessage("${md5.substring(md5.length - 5)}: $smsAttributes")
                smsManager.sendMultipartTextMessage(
                    device.phoneNumber,
                    null, smsMessage, null, null
                )
            }
        }
    }

    private fun computeMd5(s: String): String {
        try {
            // Create MD5 Hash
            val digest = java.security.MessageDigest.getInstance("MD5")
            digest.update(s.toByteArray())
            val messageDigest = digest.digest()

            // Create Hex String
            val hexString = StringBuffer()
            for (i in messageDigest.indices)
                hexString.append(Integer.toHexString(0xFF and messageDigest[i].toInt()))
            return hexString.toString()

        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }

        return ""
    }
}
