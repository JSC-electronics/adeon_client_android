package cz.jsc.electronics.arduinosms.data

data class Attribute(
    var key: String? = null,
    var value: Int? = null
) {

    override fun toString() = "$key = $value"
}
