package cz.jscelectronics.adeon.ui.device.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import cz.jscelectronics.adeon.R

class WipeDialogFragment(private val fragment: Fragment) : DialogFragment() {

    internal lateinit var listener: OnDialogClickListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = fragment as OnDialogClickListener
        } catch (e: ClassCastException) {
            // The fragment doesn't implement the interface, throw exception
            throw ClassCastException((context.toString() +
                    " must implement OnDialogClickListener"))
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return context?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
                .setTitle(R.string.warning)
                .setMessage(R.string.wipe_msg)
                .setPositiveButton(
                    R.string.wipe_btn_label
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
