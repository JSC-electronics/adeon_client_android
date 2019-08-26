package cz.jscelectronics.adeon.ui.device.viewmodels

import android.content.Context
import android.graphics.Color
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
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import cz.jscelectronics.adeon.BR
import cz.jscelectronics.adeon.R
import cz.jscelectronics.adeon.adapters.AttributesAdapter
import cz.jscelectronics.adeon.adapters.RecyclerAttributeTouchHelper
import cz.jscelectronics.adeon.data.Attribute
import cz.jscelectronics.adeon.data.Device
import cz.jscelectronics.adeon.data.DeviceRepository
import cz.jscelectronics.adeon.ui.device.AddDeviceFragment
import cz.jscelectronics.adeon.ui.device.SendSmsFragment
import cz.jscelectronics.adeon.utilities.computeMd5
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
    else MutableLiveData(Device(name = "", location = null, phoneNumber = "", attributes = mutableListOf()))
    val uriHandler = ImageUriHandler()

    private var attributesCopy: List<Attribute>? = null

    // For Singleton instantiation
    @Volatile
    private var attributesAdapter: AttributesAdapter? = null

    fun getAttributesAdapter(isEditMode: Boolean = false) =
        attributesAdapter ?: synchronized(this) {
            attributesAdapter ?: AttributesAdapter(isEditMode).also { attributesAdapter = it }
        }

    fun initAttributes(device: Device) {
        viewModelScope.launch {
            device.attributes = device.attributes.toMutableList()

            if (device.attributes.isEmpty()) {
                (device.attributes as MutableList).add(Attribute())
            }

            attributesAdapter?.submitList(device.attributes.toList())

            attributesCopy = device.attributes.map { it.copy() }.toList()
        }
    }

    fun isEditingDevice(): Boolean {
        return deviceId != null
    }

    fun addNewAttribute() {
        viewModelScope.launch {
            device.value?.let {
                (it.attributes as MutableList).add(Attribute())
                attributesAdapter?.submitList(it.attributes.toList())
            }
        }
    }

    fun isAttributeListValid(): Boolean {
        device.value?.attributes?.forEach {
            if (!it.isValid())
                return false
        }

        return true
    }

    fun isAttributeListEmpty(): Boolean {
        return device.value?.attributes.isNullOrEmpty()
    }

    fun areAttributesChecked(): Boolean {
        return device.value?.attributes?.any { it.isChecked } ?: false
    }

    fun areAttributesCheckedAndValid(): Boolean {
        return device.value?.attributes?.none { it.isChecked && !it.isValid() } ?: true
    }

    fun setMessageType(messageType: Int, refreshAttributes: Boolean = true) {
        viewModelScope.launch {
            attributesAdapter?.setAttributeFormat(messageType == Device.PLAIN_TEXT_FORMAT)

            device.value?.let {
                when {
                    it.messageType != messageType -> {
                        it.messageType = messageType

                        val attributes = it.attributes as MutableList
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

    fun addOrUpdateDevice(updateCheckFlagsOnly: Boolean = false) {
        viewModelScope.launch {
            device.value?.let { device ->
                if (deviceId == null) {
                    deviceRepository.addDevice(device)
                } else {
                    if (updateCheckFlagsOnly) {
                        attributesCopy?.let {
                            var index = 0
                            while (index < it.size) {
                                it[index].isChecked = device.attributes[index].isChecked
                                index++

                                device.attributes = it
                            }
                        }
                    } else {
                        deviceRepository.updateDevice(device)
                    }
                }
            }
        }
    }

    fun sendSmsMessage(message: String? = null) {
        viewModelScope.launch {
            device.value?.let { device ->
                val smsManager = SmsManager.getDefault()
                if (message != null) {
                    smsManager.sendMultipartTextMessage(
                        device.phoneNumber, null,
                        smsManager.divideMessage(message), null, null
                    )
                    return@launch
                }

                val smsAttributes = device.attributes.filter { it.isChecked && it.isValid() }

                if (smsAttributes.isNotEmpty()) {
                    // Store which attributes are checked
                    addOrUpdateDevice(true)

                    if (device.messageType == Device.PLAIN_TEXT_FORMAT) {
                        smsAttributes.forEach {
                            if (it.containsPlainText()) {
                                smsManager.sendMultipartTextMessage(
                                    device.phoneNumber, null,
                                    smsManager.divideMessage(it.text), null, null
                                )
                            }
                        }
                    } else if (device.messageType == Device.INT_VALUE_FORMAT) {
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

    override fun onSwiped(viewholder: RecyclerView.ViewHolder, position: Int) {
        device.value?.apply {
            val attributes = this.attributes as MutableList
            val removedAttribute = attributes.removeAt(position)
            attributesAdapter?.submitList(attributes.toList())

            val snackbar = Snackbar.make(
                viewholder.itemView,
                context.getString(R.string.command_removed, removedAttribute.name), Snackbar.LENGTH_LONG
            )
            snackbar.setAction(R.string.undo) {
                attributes.add(position, removedAttribute)
                attributesAdapter?.submitList(attributes.toList())
            }
            snackbar.setActionTextColor(Color.YELLOW)
            snackbar.show()
        }
    }

    override fun onMove(viewholder: RecyclerView.ViewHolder, from: Int, to: Int) {
        device.value?.apply {
            val attributes = this.attributes as MutableList
            val attribute = attributes.removeAt(from)
            attributes.add(to, attribute)
            attributesAdapter?.submitList(attributes.toList())
        }
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
