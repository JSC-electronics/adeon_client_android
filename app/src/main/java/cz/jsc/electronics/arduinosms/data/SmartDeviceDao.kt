package cz.jsc.electronics.arduinosms.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * The Data Access Object for the SmartDevice class.
 */
@Dao
interface SmartDeviceDao {
    @Query("SELECT * FROM devices ORDER BY id")
    fun getDevices(): LiveData<List<SmartDevice>>

    @Query("SELECT * FROM devices WHERE id = :deviceId")
    fun getDevice(deviceId: Long): LiveData<SmartDevice>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(devices: List<SmartDevice>)
}
