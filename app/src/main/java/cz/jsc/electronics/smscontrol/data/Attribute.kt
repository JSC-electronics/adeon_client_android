package cz.jsc.electronics.smscontrol.data

data class Attribute(
    val id: Long = 0,
    var key: String? = null,
    var value: Int? = null,
    var text: String? = null,
    var isChecked: Boolean = true
) {

    companion object {
        const val ATTRIBUTE_VAL_MIN = 0
        const val ATTRIBUTE_VAL_MAX = 65535

        /*
         * Limit text length to 160 ASCII characters. Note that we don't limit the user in what character he/she can use.
         * Therefore as a precaution we send multi-part text message, as normal cell phone would od.
         * It's up to the user to put proper text to send.
         */
        private const val PLAIN_TEXT_MAX_LEN = 160
    }

    override fun toString(): String {
        if (key != null) {
            return "$key = $value"
        } else {
            return "$text"
        }
    }

    fun isValid(): Boolean {
        return (containsKeyValuePair() &&
                value!! >= ATTRIBUTE_VAL_MIN && value!! <= ATTRIBUTE_VAL_MAX) ||
                containsPlainText() && text!!.length <= PLAIN_TEXT_MAX_LEN
    }

    fun containsPlainText(): Boolean {
        return !text.isNullOrEmpty()
    }

    fun containsKeyValuePair(): Boolean {
        return !key.isNullOrEmpty() && value != null
    }
}
