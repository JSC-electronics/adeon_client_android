package cz.jscelectronics.adeon.data

import com.google.gson.annotations.SerializedName

data class Attribute(
    @SerializedName("name")
    var name: String? = null,

    @SerializedName("value")
    var value: Int? = null,

    @SerializedName("text")
    var text: String? = null,

    @SerializedName("isChecked")
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
        return "$name = ${value ?: text}"
    }

    fun isValid(): Boolean {
        return (containsNameValuePair() &&
                value!! >= ATTRIBUTE_VAL_MIN && value!! <= ATTRIBUTE_VAL_MAX) ||
                containsPlainText() && text!!.length <= PLAIN_TEXT_MAX_LEN
    }

    fun containsPlainText(): Boolean {
        return !text.isNullOrEmpty()
    }

    fun containsNameValuePair(): Boolean {
        return !name.isNullOrEmpty() && value != null
    }

    fun hasTheSameContent(other: Attribute): Boolean {
        return name == other.name && value == other.value && text == other.text
    }
}
