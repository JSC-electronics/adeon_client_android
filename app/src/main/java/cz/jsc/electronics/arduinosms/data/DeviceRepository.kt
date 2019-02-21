package cz.jsc.electronics.arduinosms.data

/**
 * Repository module for handling data operations.
 */
class DeviceRepository private constructor(private val deviceDao: DeviceDao) {

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
