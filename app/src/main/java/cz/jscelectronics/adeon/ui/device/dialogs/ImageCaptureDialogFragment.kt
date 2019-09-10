package cz.jscelectronics.adeon.ui.device.dialogs

import android.app.Dialog
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import cz.jscelectronics.adeon.R

class ImageCaptureDialogFragment : DialogFragment() {

    interface ImageCaptureDialogListener {
        fun onDialogSelectFromAdeonLibraryActionClick()
        fun onDialogSelectImageActionClick()
        fun onDialogTakePhotoActionClick()
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
                        0 -> {
                            (targetFragment as ImageCaptureDialogListener)
                                .onDialogSelectFromAdeonLibraryActionClick()
                        }
                        1 -> {
                            (targetFragment as ImageCaptureDialogListener)
                                .onDialogSelectImageActionClick()
                        }
                        2 -> {
                            (targetFragment as ImageCaptureDialogListener)
                                .onDialogTakePhotoActionClick()
                        }
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
