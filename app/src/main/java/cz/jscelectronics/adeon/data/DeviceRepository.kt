package cz.jscelectronics.adeon.data

import android.content.ContentResolver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository module for handling data operations.
 */
class DeviceRepository private constructor(private val deviceDao: DeviceDao) {

    suspend fun addDevice(device: Device) {
        withContext(Dispatchers.IO) {
            deviceDao.insertDevice(device)
        }
    }

    suspend fun addDevices(devices: List<Device>) {
        withContext(Dispatchers.IO) {
            deviceDao.insertAll(devices)
        }
    }

    suspend fun updateDevice(device: Device) {
        withContext(Dispatchers.IO) {
            deviceDao.updateDevice(device)
        }
    }

    suspend fun deleteDevice(device: Device) {
        withContext(Dispatchers.IO) {
            deviceDao.deleteDevice(device)
        }
    }

    suspend fun deleteAllDevices(resolver: ContentResolver?) {
        withContext(Dispatchers.IO) {
            resolver?.let {
                deviceDao.getAllDeviceUris().forEach { uri ->
                    if (uri != null && !uri.toString().contains(ContentResolver.SCHEME_ANDROID_RESOURCE)) {
                        it.delete(uri, null, null)
                    }
                }
            }

            deviceDao.deleteAll()
        }
    }

    fun getDevices() = deviceDao.getDevices()

    fun getDevice(deviceId: Long) = deviceDao.getDevice(deviceId)

    companion object {

        // For Singleton instantiation
        @Volatile
        private var instance: DeviceRepository? = null

        fun getInstance(deviceDao: DeviceDao) =
            instance ?: synchronized(this) {
                instance ?: DeviceRepository(deviceDao).also { instance = it }
            }
    }
}
