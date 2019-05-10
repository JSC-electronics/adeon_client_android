package cz.jsc.electronics.smscontrol.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

    @TypeConverter
    fun attributeListToString(attributes: MutableList<Attribute>): String =
        Gson().toJson(attributes.filter { (it.key != null && it.value != null) || it.text != null })

    @TypeConverter
    fun stringToAttributeList(attributeString: String): MutableList<Attribute> {
        val attributeType = object : TypeToken<List<Attribute>>() {}.type
        return Gson().fromJson<MutableList<Attribute>>(attributeString, attributeType)
    }
}