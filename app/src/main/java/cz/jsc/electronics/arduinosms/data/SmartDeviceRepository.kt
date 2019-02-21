package cz.jsc.electronics.arduinosms.data

/**
 * Repository module for handling data operations.
 */
class SmartDeviceRepository private constructor(private val smartDeviceDao: SmartDeviceDao) {

    fun getDevices() = smartDeviceDao.getDevices()

    fun getDevice(deviceId: Long) = smartDeviceDao.getDevice(deviceId)

    companion object {

        // For Singleton instantiation
        @Volatile
        private var instance: SmartDeviceRepository? = null

        fun getInstance(smartDeviceDao: SmartDeviceDao) =
            instance ?: synchronized(this) {
                instance ?: SmartDeviceRepository(smartDeviceDao).also { instance = it }
            }
    }
}
