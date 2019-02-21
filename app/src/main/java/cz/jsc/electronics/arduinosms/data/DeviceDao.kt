package cz.jsc.electronics.arduinosms.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * The Data Access Object for the Device class.
 */
@Dao
interface DeviceDao {
    @Query("SELECT * FROM devices ORDER BY id")
    fun getDevices(): LiveData<List<Device>>

    @Query("SELECT * FROM devices WHERE id = :deviceId")
    fun getDevice(deviceId: Long): LiveData<Device>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(devices: List<Device>)
}
