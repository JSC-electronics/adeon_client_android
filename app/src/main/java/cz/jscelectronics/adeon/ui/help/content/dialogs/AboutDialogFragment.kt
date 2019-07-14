package cz.jscelectronics.adeon.ui.help.content.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import cz.jscelectronics.adeon.BuildConfig
import cz.jscelectronics.adeon.R
import cz.jscelectronics.adeon.databinding.DialogAboutBinding

class AboutDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return context?.let {
            activity?.let { activity ->
                val binding = DialogAboutBinding.inflate(activity.layoutInflater, null, false).apply {
                    this.version.text = it.getString(R.string.app_version, BuildConfig.VERSION_NAME)
                }

                // Use the Builder class for convenient dialog construction
                val builder = AlertDialog.Builder(it)
                    .setView(binding.alertDialog)
                builder.create()
            }
        } ?: throw IllegalStateException("Context cannot be null")
    }
}
