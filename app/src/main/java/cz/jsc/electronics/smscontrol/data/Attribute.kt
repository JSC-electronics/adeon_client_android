package cz.jsc.electronics.smscontrol.data

data class Attribute(
    val id: Long = 0,
    var key: String? = null,
    var value: Int? = null,
    var text: String? = null,
    var isChecked: Boolean = true
) {

    companion object {
        private const val ATTRIBUTE_VAL_MIN = 0
        private const val ATTRIBUTE_VAL_MAX = 65535
    }

    override fun toString(): String {
        if (key != null) {
            return "$key = $value"
        } else {
            return "$text"
        }
    }

    fun isValid(): Boolean {
        return (!key.isNullOrEmpty() && value != null &&
                value!! >= ATTRIBUTE_VAL_MIN && value!! <= ATTRIBUTE_VAL_MAX) ||
                !text.isNullOrEmpty()
    }

    fun hasOnlyPlainText(): Boolean {
        return !text.isNullOrEmpty() && key.isNullOrEmpty()
    }
}
