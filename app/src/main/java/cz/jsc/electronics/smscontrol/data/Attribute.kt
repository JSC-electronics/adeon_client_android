package cz.jsc.electronics.smscontrol.data

data class Attribute(
    val id: Long = 0,
    var key: String? = null,
    var value: Int? = null,
    var text: String? = null,
    var isChecked: Boolean = true
) {

    override fun toString(): String {
        if (key != null) {
            return "$key = $value"
        } else {
            return "$text"
        }
    }
}
