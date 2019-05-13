package cz.jsc.electronics.smscontrol.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "devices")
data class Device(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val deviceId: Long = 0,
    var name: String,
    var location: String?,
    var phoneNumber: String,
    var messageType: Int = KEY_VALUE_FORMAT,
    var attributes: List<Attribute> = arrayListOf()
) {

    companion object {
        const val KEY_VALUE_FORMAT = 1
        const val PLAIN_TEXT_FORMAT = 2
    }

    override fun toString() = name
}
