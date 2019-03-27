package cz.jsc.electronics.arduinosms.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "devices")
data class Device(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val deviceId: Long = 0,
    val name: String,
    val location: String?,
    val phoneNumber: String,
    val attributes: List<Attribute> = ArrayList()
) {

    override fun toString() = name
}
