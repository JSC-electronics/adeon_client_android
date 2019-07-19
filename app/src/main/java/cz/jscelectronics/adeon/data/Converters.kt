package cz.jscelectronics.adeon.data

import android.net.Uri
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class Converters {

    @TypeConverter
    fun attributeListToString(attributes: List<Attribute>): String =
        Gson().toJson(attributes.filter { (it.name != null && it.value != null) || it.text != null })

    @TypeConverter
    fun stringToAttributeList(attributeString: String): List<Attribute> {
        val attributeType = object : TypeToken<List<Attribute>>() {}.type
        return Gson().fromJson(attributeString, attributeType)
    }

    @TypeConverter
    fun uriToString(uri: Uri?): String? = uri?.toString()

    @TypeConverter
    fun stringToUri(string: String?): Uri? {
        string?.let {
            return Uri.parse(it)
        }

        return null
    }
}