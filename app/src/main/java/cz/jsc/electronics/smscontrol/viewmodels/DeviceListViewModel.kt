package cz.jsc.electronics.smscontrol.viewmodels

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import cz.jsc.electronics.smscontrol.DeviceListFragment
import cz.jsc.electronics.smscontrol.adapters.RecyclerAttributeTouchHelper
import cz.jsc.electronics.smscontrol.data.Device
import cz.jsc.electronics.smscontrol.data.DeviceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


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
                deviceList.value?.let {
                    it.subList(device.position.toInt() + 1, it.size).onEach { dev ->
                        dev.position = dev.position - 1
                        deviceRepository.updateDevice(dev)
                    }
                }

                deviceRepository.deleteDevice(device, context.contentResolver)
            }
        }
    }

    fun duplicateDevice(device: Device) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val duplicate = device.copy(deviceId = 0, image = null, position = deviceRepository.getDeviceCount())
                deviceRepository.addDevice(duplicate)
            }
        }
    }

    fun importConfiguration(uri: Uri) {
        viewModelScope.launch {
            deviceList.value?.let {
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
                } catch (e: FileNotFoundException ) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: IllegalStateException) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun exportConfiguration(uri: Uri) {
        viewModelScope.launch {
            deviceList.value?.let { devices ->
                try {
                    context.contentResolver.openFileDescriptor(uri, "w")?.use {
                        FileOutputStream(it.fileDescriptor).use { outputStream ->
                            outputStream.write(
                                GsonBuilder().addSerializationExclusionStrategy(GsonExludeImageStrategy())
                                    .create().toJson(devices).toByteArray()
                            )
                        }
                    }
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onSwiped(position: Int) {
        deviceList.value?.let {
            deleteDevice(it[position])
        }
    }

    override fun onMove(from: Int, to: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                deviceList.value?.let { list ->
                    val movedDevice = list[from]
                    val targetDevice = list[to]

                    movedDevice.position = to.toLong()
                    targetDevice.position = from.toLong()
                    deviceRepository.updateDevice(movedDevice)
                    deviceRepository.updateDevice(targetDevice)

//                    if (from > to) {
//                        list.subList(to, from).onEach {
//                            it.position = it.position + 1
//                            deviceRepository.updateDevice(it)
//                        }
//                    }
//
//                    if (from < to) {
//                        list.subList(from + 1, to + 1).onEach {
//                            it.position = it.position + 1
//                            deviceRepository.updateDevice(it)
//                        }
//                    }
                }
            }
        }
    }

    // It doesn't make sense to store reference to internal device image. The image is not part of a backup.
    private class GsonExludeImageStrategy : ExclusionStrategy {
        override fun shouldSkipClass(clazz: Class<*>?): Boolean = false

        override fun shouldSkipField(f: FieldAttributes?): Boolean = "image" == f?.name

    }
}
