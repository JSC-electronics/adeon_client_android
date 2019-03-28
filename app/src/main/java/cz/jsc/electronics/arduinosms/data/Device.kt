package cz.jsc.electronics.arduinosms.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "devices")
data class Device(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val deviceId: Long = 0,
    var name: String,
    var location: String?,
    var phoneNumber: String,
    val attributes: MutableList<Attribute> = arrayListOf()
) {

    override fun toString() = name
}
