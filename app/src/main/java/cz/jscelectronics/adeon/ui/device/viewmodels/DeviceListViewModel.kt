package cz.jscelectronics.adeon.ui.device.viewmodels

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import cz.jscelectronics.adeon.R
import cz.jscelectronics.adeon.adapters.RecyclerAttributeTouchHelper
import cz.jscelectronics.adeon.data.Device
import cz.jscelectronics.adeon.data.DeviceRepository
import cz.jscelectronics.adeon.ui.device.DeviceListFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.FileInputStream
import java.io.FileOutputStream


/**
 * The ViewModel for [DeviceListFragment].
 */
class DeviceListViewModel internal constructor(
    private val context: Context,
    private val deviceRepository: DeviceRepository
) : ViewModel(), RecyclerAttributeTouchHelper.RecyclerAttributeTouchHelperListener {

    private val deviceList = deviceRepository.getDevices()

    fun getDevices() = deviceList

    fun deleteDevice(device: Device) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                deviceRepository.deleteDevice(device, context.contentResolver)
            }
        }
    }

    fun duplicateDevice(device: Device) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val duplicate = device.copy(deviceId = 0, image = null)
                deviceRepository.addDevice(duplicate)
            }
        }
    }

    fun importConfiguration(uri: Uri, view: View) {
        viewModelScope.launch {
            deviceList.value?.let {
                var success = true

                try {
                    context.contentResolver.openFileDescriptor(uri, "r")?.use {
                        // use{} lets the document provider know you're done by automatically closing the stream
                        FileInputStream(it.fileDescriptor).use { inputStream ->
                            val reader = inputStream.reader().buffered()
                            val json = reader.readText()
                            reader.close()

                            val attributeType = object : TypeToken<List<Device>>() {}.type
                            val devices = Gson().fromJson<List<Device>>(json, attributeType)
                            deviceRepository.deleteAllDevices(context.contentResolver)
                            deviceRepository.addDevices(devices)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    success = false
                }

                if (success) {
                    Snackbar.make(view, R.string.configuration_import_successful, Snackbar.LENGTH_LONG).show()
                } else {
                    Snackbar.make(view, R.string.invalid_configuration, Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

    fun exportConfiguration(uri: Uri, view: View) {
        viewModelScope.launch {
            deviceList.value?.let { devices ->
                var success = true

                try {
                    context.contentResolver.openFileDescriptor(uri, "w")?.use {
                        FileOutputStream(it.fileDescriptor).use { outputStream ->
                            outputStream.write(
                                GsonBuilder().addSerializationExclusionStrategy(GsonExludeImageStrategy())
                                    .create().toJson(devices).toByteArray()
                            )
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                if (success) {
                    Snackbar.make(view, R.string.configuration_export_successful, Snackbar.LENGTH_LONG).show()
                } else {
                    Snackbar.make(view, R.string.configuration_export_error, Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onSwiped(viewholder: RecyclerView.ViewHolder, position: Int) {
        deviceList.value?.let {
            val removedDevice = it[position]
            deleteDevice(removedDevice)
            // TODO: We don't support full undo. Image is deleted irreversibly.
            removedDevice.image = null

            val snackbar = Snackbar.make(
                viewholder.itemView,
                context.getString(R.string.device_removed, removedDevice.name), Snackbar.LENGTH_LONG
            )
            snackbar.setAction(R.string.undo) {
                viewModelScope.launch {
                    deviceRepository.addDevice(removedDevice)
                }
            }
            snackbar.setActionTextColor(Color.YELLOW)
            snackbar.show()
        }
    }

    override fun onMove(viewholder: RecyclerView.ViewHolder, from: Int, to: Int) {
        // TODO Not impemented
    }

    // It doesn't make sense to store reference to internal device image. The image is not part of a backup.
    private class GsonExludeImageStrategy : ExclusionStrategy {
        override fun shouldSkipClass(clazz: Class<*>?): Boolean = false

        override fun shouldSkipField(f: FieldAttributes?): Boolean = "image" == f?.name

    }
}
