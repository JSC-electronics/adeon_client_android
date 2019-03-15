package cz.jsc.electronics.arduinosms.data

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * The Data Access Object for the Device class.
 */
@Dao
interface DeviceDao {
    @Query("SELECT * FROM devices ORDER BY id")
    fun getDevices(): LiveData<List<Device>>

    @Query("SELECT * FROM devices WHERE id = :deviceId")
    fun getDevice(deviceId: Long): LiveData<Device>

    @Insert
    fun insertDevice(device: Device): Long

    @Update
    fun updateDevice(device: Device)

    @Delete
    fun deleteDevice(device: Device)
}
