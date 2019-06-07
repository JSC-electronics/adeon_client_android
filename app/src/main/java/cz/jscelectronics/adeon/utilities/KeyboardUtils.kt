package cz.jscelectronics.adeon.utilities

import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager

fun View.hideSoftKeyboard() {
    val imm = this.context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(this.windowToken, 0)
}