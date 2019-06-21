package cz.jscelectronics.adeon.ui.help.content.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import cz.jscelectronics.adeon.R

class AboutDialogFragment() : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return context?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
                .setView(R.layout.dialog_about)
                builder.create()
        } ?: throw IllegalStateException("Context cannot be null")
    }
}
