package cz.jsc.electronics.smscontrol.data

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * The Data Access Object for the Device class.
 */
@Dao
interface DeviceDao {
    @Query("SELECT * FROM devices ORDER BY position ASC, id ASC")
    fun getDevices(): LiveData<List<Device>>

    @Query("SELECT * FROM devices WHERE id = :deviceId")
    fun getDevice(deviceId: Long): LiveData<Device>

    @Query("SELECT image FROM devices")
    fun getAllDeviceUris(): List<Uri>

    @Query("SELECT Count(*) from devices")
    fun getDeviceCount(): Long

    @Insert
    fun insertDevice(device: Device): Long

    @Update
    fun updateDevice(device: Device)

    @Delete
    fun deleteDevice(device: Device)

    @Query("DELETE FROM devices")
    fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(devices: List<Device>)
}
