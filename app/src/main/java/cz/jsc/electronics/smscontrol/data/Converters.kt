package cz.jsc.electronics.smscontrol.data

import androidx.room.TypeConverter

class Converters {

    @TypeConverter
    fun attributeListToString(attributes: MutableList<Attribute>): String =
        attributes.filter { it.key != null && it.value != null }.joinToString(separator = ";", postfix = ";")

    @TypeConverter
    fun stringToAttributeList(attributeString: String): MutableList<Attribute> {
        val attributes = arrayListOf<Attribute>()

        attributeString.split(";").dropLast(1).forEach {
            val attrs = it.split("=")

            // Attribute has proper key and value
            if (attrs.size == 2) {
                attributes.add(Attribute(attributes.size.toLong(), attrs[0].trim(), attrs[1].trim().toIntOrNull()))
            }
        }

        return attributes
    }
}