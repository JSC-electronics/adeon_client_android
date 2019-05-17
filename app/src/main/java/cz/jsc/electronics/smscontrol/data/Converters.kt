package cz.jsc.electronics.smscontrol.data

import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.ByteArrayOutputStream


class Converters {

    @TypeConverter
    fun attributeListToString(attributes: List<Attribute>): String =
        Gson().toJson(attributes.filter { (it.name != null && it.value != null) || it.text != null })

    @TypeConverter
    fun stringToAttributeList(attributeString: String): List<Attribute> {
        val attributeType = object : TypeToken<List<Attribute>>() {}.type
        return Gson().fromJson<List<Attribute>>(attributeString, attributeType)
    }

    @TypeConverter
    fun bitmapToByteArray(bitmap: Bitmap?): ByteArray? {
        bitmap?.let {
            val stream = ByteArrayOutputStream()
            bitmap.compress(CompressFormat.PNG, 0, stream)
            return stream.toByteArray()
        }

        return null
    }

    @TypeConverter
    fun byteArrayToBitmap(bytes: ByteArray?): Bitmap? {
        bytes?.let {
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        }

        return null
    }
}