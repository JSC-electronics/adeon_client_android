package cz.jscelectronics.adeon.viewmodels

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.telephony.SmsManager
import androidx.core.content.FileProvider
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.jscelectronics.adeon.AddDeviceFragment
import cz.jscelectronics.adeon.BR
import cz.jscelectronics.adeon.SendSmsFragment
import cz.jscelectronics.adeon.adapters.AttributesAdapter
import cz.jscelectronics.adeon.adapters.RecyclerAttributeTouchHelper
import cz.jscelectronics.adeon.data.Attribute
import cz.jscelectronics.adeon.data.Device
import cz.jscelectronics.adeon.data.DeviceRepository
import cz.jscelectronics.adeon.utilities.computeMd5
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * The ViewModel for managing device properties in [AddDeviceFragment] and [SendSmsFragment].
 */
class ManageDeviceViewModel internal constructor(
    private val context: Context,
    private val deviceRepository: DeviceRepository,
    private val deviceId: Long?
) : ViewModel(), RecyclerAttributeTouchHelper.RecyclerAttributeTouchHelperListener {

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

    var uriHandler = ImageUriHandler()

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
                name = it.name, value = it.value,
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
        attributes.add(Attribute())
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
                when {
                    it.messageType != messageType -> {
                        attributes.clear()
                        attributes.add(Attribute())
                        attributesAdapter?.submitList(attributes.toList())
                    }
                    refreshAttributes -> initAttributes(it)
                    else -> {
                        // Do nothing
                    }
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
                    val checkedAttributes = attributes.filter { it.isChecked }.toSet()

                    device.attributes.onEach { attribute ->
                        attribute.name?.let {
                            if (it.isEmpty()) {
                                attribute.name = null
                            }
                        }

                        attribute.isChecked = checkedAttributes.any {
                            attribute.hasTheSameContent(it)
                        }
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
    fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "Device_image_${timeStamp}_",
            ".jpg",
            storageDir
        )
    }

    override fun onSwiped(position: Int) {
        attributes.removeAt(position)
        attributesAdapter?.submitList(attributes.toList())
    }

    override fun onMove(from: Int, to: Int) {
        val attribute = attributes.removeAt(from)
        attributes.add(to, attribute)
        attributesAdapter?.submitList(attributes.toList())
    }

    inner class ImageUriHandler : BaseObservable() {

        private var currentUri: Uri? = null
        private var tempUri: Uri? = null
        private var deviceImageChanged = false
        private var init = false

        @Bindable
        fun getUri(): Uri? {
            return currentUri
        }

        @Bindable
        fun setUri(uri: Uri?) {
            currentUri?.let {
                deleteImage(it)
            }

            currentUri = uri

            device.value?.let {
                it.image = uri
            }

            if (!init) {
                init = true
            } else {
                deviceImageChanged = true
            }
            notifyPropertyChanged(BR.uri)
        }

        fun isDeviceImageChanged(): Boolean {
            return deviceImageChanged
        }

        // Create new empty file and URI for it
        fun createTempUri(): Uri? {
            // Create the File where the photo should go
            val photoFile: File? = try {
                createImageFile()
            } catch (ex: IOException) {
                null
            }

            // Continue only if the File was successfully created
            photoFile?.also {
                tempUri = FileProvider.getUriForFile(
                    context,
                    "cz.jscelectronics.adeon.fileprovider",
                    it
                )

                return tempUri
            }

            return null
        }

        fun makeTempUriPermanent() {
            setUri(tempUri)
        }

        fun clearTempUri() {
            tempUri?.let {
                deleteImage(it)
                tempUri = null
            }
        }

        fun storeGalleryImage(sourceUri: Uri): Job {
            return viewModelScope.launch {
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    null
                }

                // Continue only if the File was successfully created
                photoFile?.also {
                    val destUri: Uri = FileProvider.getUriForFile(
                        context,
                        "cz.jscelectronics.adeon.fileprovider",
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
                            bis.read(buf)
                            do {
                                bos.write(buf)
                            } while (bis.read(buf) != -1)
                        }

                    } catch (ex: IOException) {
                        ex.printStackTrace()
                        success = false
                    } finally {
                        try {
                            bis?.close()
                            bos?.close()
                            srcFileDescriptor?.close()
                            dstFileDescriptor?.close()
                        } catch (ex: IOException) {
                            ex.printStackTrace()
                        }
                    }

                    if (success) {
                        setUri(destUri)
                    }
                }
            }
        }

        private fun deleteImage(uri: Uri) {
            viewModelScope.launch {
                context.contentResolver.delete(uri, null, null)
            }
        }
    }
}
