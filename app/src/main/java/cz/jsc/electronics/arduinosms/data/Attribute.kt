package cz.jsc.electronics.arduinosms.data

data class Attribute(
    val id: Long,
    var key: String? = null,
    var value: Int? = null,
    var checked: Boolean = true
) {

    override fun toString() = "$key = $value"
}
