package cz.jscelectronics.adeon.ui.device.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import cz.jscelectronics.adeon.R

class ImportDialogFragment() : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return context?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
                .setTitle(R.string.warning)
                .setMessage(R.string.import_overwrite_msg)
                .setPositiveButton(
                    R.string.import_btn_label
                ) { _, _ ->
                    (targetFragment as OnDialogClickListener).onDialogPositiveClick(this)
                }
                .setNegativeButton(
                    android.R.string.cancel
                ) { _, _ ->
                    // User cancelled the dialog
                }
                builder.create()
        } ?: throw IllegalStateException("Context cannot be null")
    }
}
