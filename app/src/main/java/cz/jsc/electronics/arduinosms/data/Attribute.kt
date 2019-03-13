package cz.jsc.electronics.arduinosms.data

data class Attribute(
    val key: String? = null,
    val value: Int? = null
) {

    override fun toString() = "${key}=${value}"
}
