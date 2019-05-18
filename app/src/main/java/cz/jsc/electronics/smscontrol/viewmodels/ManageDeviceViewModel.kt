package cz.jsc.electronics.smscontrol.viewmodels

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.telephony.SmsManager
import androidx.core.content.FileProvider
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * The ViewModel for managing device properties in [AddDeviceFragment] and [SendSmsFragment].
 */
class ManageDeviceViewModel internal constructor(
    private val deviceRepository: DeviceRepository,
    private val deviceId: Long?
) : ViewModel() {

    companion object {
        private const val CHECKSUM_SIZE = 5

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
    private var messageType: Int = Device.INT_VALUE_FORMAT

    // For Singleton instantiation
    @Volatile
    private var attributesAdapter: AttributesAdapter? = null

    fun getAttributesAdapter(isEditMode: Boolean = false) =
        attributesAdapter ?: synchronized(this) {
            attributesAdapter ?: AttributesAdapter(isEditMode).also { attributesAdapter = it }
        }

    fun initAttributes(device: Device) {
        attributes = device.attributes.map {
            it.copy(
                id = it.id, name = it.name, value = it.value,
                text = it.text, isChecked = it.isChecked
            )
        }.toMutableList()

        if (attributes.isEmpty()) {
            attributes.add(Attribute())
        }

        attributesAdapter?.submitList(attributes.toList())
    }

    fun isEditingDevice(): Boolean {
        return deviceId != null
    }

    fun addNewAttribute() {
        attributes.add(Attribute(attributes.get(attributes.size-1).id + 1))
        attributesAdapter?.submitList(attributes.toList())
    }

    fun isAttributeListValid(): Boolean {
        attributes.forEach {
            if (!it.isValid())
                return false
        }
        return true
    }

    fun isAttributeListEmpty(): Boolean {
        return attributes.isEmpty()
    }

    fun areAttributesChecked(): Boolean {
        return attributes.any { it.isChecked }
    }

    fun setMessageType(messageType: Int, refreshAttributes: Boolean = true) {
        if (this.messageType != messageType) {
            this.messageType = messageType
            attributesAdapter?.setAttributeFormat(messageType == Device.PLAIN_TEXT_FORMAT)

            device.value?.let {
                if (it.messageType != messageType) {
                    attributes.clear()
                    attributes.add(Attribute())
                    attributesAdapter?.submitList(attributes.toList())
                } else if (refreshAttributes) {
                    initAttributes(it)
                } else {
                    // Do nothing
                }
            }
        }
    }

    fun addOrUpdateDevice(overwriteAttributes: Boolean = false) {
        viewModelScope.launch {

            device.value?.let { device ->
                if (device.messageType != messageType) {
                    device.messageType = messageType
                    device.attributes = attributes
                }

                if (device.attributes.isEmpty() || device.attributes.size != attributes.size || overwriteAttributes) {
                    device.attributes = attributes.toList()
                } else {
                    val checkedAttributes = attributes.filter { it.isChecked }.map { it.id }.toSet()

                    device.attributes.onEach { attribute ->
                        attribute.name?.let {
                            if (it.isEmpty()) {
                                attribute.name = null
                            }
                        }

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

                val smsAttributes = attributes.filter { it.isChecked && it.isValid() }

                if (smsAttributes.isNotEmpty()) {
                    // Store which attributes are checked
                    addOrUpdateDevice()

                    if (messageType == Device.INT_VALUE_FORMAT) {
                        composeMessage(smsAttributes).forEach { message ->
                            val md5 = computeMd5(message)
                            val smsMessage = "${md5.substring(md5.length - CHECKSUM_SIZE)}: $message"
                            smsManager.sendTextMessage(
                                device.phoneNumber, null, smsMessage,
                                null, null
                            )
                        }
                    } else if (messageType == Device.PLAIN_TEXT_FORMAT) {
                        smsAttributes.forEach {
                            if (it.containsPlainText()) {
                                smsManager.sendMultipartTextMessage(
                                    device.phoneNumber, null,
                                    smsManager.divideMessage(it.text), null, null
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun composeMessage(attributes: List<Attribute>): List<String> {
        val messages = ArrayList<String>()
        var message = ""

        attributes.forEach { attribute ->
            if (attribute.containsNameValuePair()) {
                // If we append the attribute, max message length will be exceeded
                if (message.length + attribute.toString().length + 1 > MAX_PAYLOAD_SIZE) {
                    messages.add(message)
                    message = "$attribute;"
                } else {
                    message += "$attribute;"
                }
            }
        }

        // Put last message into the list
        if (message.isNotEmpty()) {
            messages.add(message)
        }

        return messages
    }

    @Throws(IOException::class)
    fun createImageFile(context: Context): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "Device_icon_${timeStamp}_",
            ".jpg",
            storageDir
        )
    }

    fun storeGalleryImage(sourceUri: Uri, context: Context): Job {
        return viewModelScope.launch {
            val photoFile: File? = try {
                createImageFile(context)
            } catch (ex: IOException) {
                null
            }

            // Continue only if the File was successfully created
            photoFile?.also {
                val destUri: Uri = FileProvider.getUriForFile(
                    context,
                    "cz.jsc.electronics.smscontrol.fileprovider",
                    it
                )

                var srcFileDescriptor: ParcelFileDescriptor? = null
                var dstFileDescriptor: ParcelFileDescriptor? = null
                var bis: BufferedInputStream? = null
                var bos: BufferedOutputStream? = null
                var success = true

                try {
                    srcFileDescriptor = context.contentResolver?.openFileDescriptor(sourceUri, "r")
                    dstFileDescriptor = context.contentResolver?.openFileDescriptor(destUri, "w")

                    if (srcFileDescriptor != null && dstFileDescriptor != null) {
                        bis = BufferedInputStream(FileInputStream(srcFileDescriptor.fileDescriptor))
                        bos = BufferedOutputStream(FileOutputStream(dstFileDescriptor.fileDescriptor))

                        val buf = ByteArray(size = 1024)
                        bis.read(buf);
                        do {
                            bos.write(buf);
                        } while (bis.read(buf) != -1)
                    }

                } catch (ex: IOException) {
                    ex.printStackTrace()
                    success = false
                } finally {
                    try {
                        bis?.close();
                        bos?.close();
                        srcFileDescriptor?.close()
                        dstFileDescriptor?.close()
                    } catch (ex: IOException) {
                        ex.printStackTrace();
                    }
                }

                if (success) {
                    device.value?.apply {
                        icon = destUri
                    }
                }
            }
        }
    }
}
