package cz.jsc.electronics.smscontrol.data

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

    suspend fun deleteAllDevices() {
        withContext(Dispatchers.IO) {
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
