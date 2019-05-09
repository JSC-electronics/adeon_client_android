package cz.jsc.electronics.smscontrol.data

data class Attribute(
    val id: Long = 0,
    var key: String? = null,
    var value: Int? = null,
    var checked: Boolean = true
) {

    override fun toString() = "$key = $value"
}
