package cz.jscelectronics.adeon.ui.device.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import cz.jscelectronics.adeon.R

class PaidNumberDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return context?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
                .setTitle(R.string.warning)
                .setMessage(R.string.premium_sms)
                .setNeutralButton(
                    android.R.string.ok
                ) { _, _ ->
                }
            builder.create()
        } ?: throw IllegalStateException("Context cannot be null")
    }
}
