package cz.jscelectronics.adeon.data

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "devices")
data class Device(
    @SerializedName("deviceId")
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val deviceId: Long = 0,

    @SerializedName("name")
    var name: String,

    @SerializedName("location")
    var location: String?,

    @SerializedName("phoneNumber")
    var phoneNumber: String,

    @SerializedName("messageType")
    var messageType: Int = PLAIN_TEXT_FORMAT,

    @SerializedName("image")
    var image: Uri? = null,

    @SerializedName("attributes")
    var attributes: List<Attribute> = arrayListOf()
) {

    companion object {
        const val PLAIN_TEXT_FORMAT = 1
        const val INT_VALUE_FORMAT = 2
    }

    override fun toString() = name
}
