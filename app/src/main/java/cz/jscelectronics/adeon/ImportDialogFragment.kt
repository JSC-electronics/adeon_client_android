package cz.jscelectronics.adeon

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment

class ImportDialogFragment(private val fragment: Fragment) : DialogFragment() {

    internal lateinit var listener: ImportDialogListener

    interface ImportDialogListener {
        fun onDialogPositiveClick(dialog: DialogFragment)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = fragment as ImportDialogListener
        } catch (e: ClassCastException) {
            // The fragment doesn't implement the interface, throw exception
            throw ClassCastException((context.toString() +
                    " must implement ImportDialogListener"))
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return context?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
                .setTitle(R.string.warning)
                .setMessage(R.string.import_overwrite_msg)
                .setPositiveButton(
                    R.string.import_btn_label
                ) { _, _ ->
                    listener.onDialogPositiveClick(this)
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
