package cz.jscelectronics.adeon

import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment

class ImageCaptureDialogFragment(private val fragment: Fragment) : DialogFragment() {

    internal lateinit var listener: ImageCaptureDialogListener

    interface ImageCaptureDialogListener {
        fun onDialogTakePhotoActionClick(dialog: DialogFragment)
        fun onDialogSelectImageActionClick(dialog: DialogFragment)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = fragment as ImageCaptureDialogListener
        } catch (e: ClassCastException) {
            // The fragment doesn't implement the interface, throw exception
            throw ClassCastException((context.toString() +
                    " must implement ImageCaptureDialogListener"))
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return context?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
                .setTitle(R.string.change_device_image)
                .setItems(
                    // Devices with no camera can still set device icon
                    if (it.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY))
                        R.array.change_device_image_actions
                    else
                        R.array.change_device_image_no_camera_actions
                ) { _, which ->
                    // The 'which' argument contains the index position
                    // of the selected item
                    when (which) {
                        0 -> listener.onDialogSelectImageActionClick(this)
                        1 -> listener.onDialogTakePhotoActionClick(this)
                    }
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