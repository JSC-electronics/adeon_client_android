package cz.jsc.electronics.arduinosms.data

data class Attribute(
    val key: String,
    val value: Int
) {

    override fun toString() = "${key}=${value}"
}
