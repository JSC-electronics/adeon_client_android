package cz.jsc.electronics.arduinosms.data

import androidx.room.TypeConverter

class Converters {

    @TypeConverter
    fun attributeListToString(attributes: List<Attribute>): String =
        attributes.filter { it.key != null && it.value != null }.joinToString(separator = ";", postfix = ";")

    @TypeConverter
    fun stringToAttributeList(attributeString: String): List<Attribute> {
        val attributes = ArrayList<Attribute>()

        attributeString.split(";").dropLast(1).forEach {
            val attrs = it.split("=")

            // Attribute has proper key and value
            if (attrs.size == 2) {
                attributes.add(Attribute(attributes.size.toLong(), attrs[0].trim(), attrs[1].trim().toIntOrNull()))
            }
        }

        if (attributes.isEmpty()) {
            attributes.add(Attribute())
        }

        return attributes
    }
}