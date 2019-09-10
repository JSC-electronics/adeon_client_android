package cz.jscelectronics.adeon.ui.device.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import cz.jscelectronics.adeon.R
import cz.jscelectronics.adeon.adapters.GalleryAdapter
import cz.jscelectronics.adeon.databinding.DialogAdeonGalleryBinding


class AdeonGalleryDialogFragment : DialogFragment() {

    interface AdeonGalleryDialogListener {
        fun onAdeonImageClick(iconResId: Int)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return context?.let {
            activity?.let { activity ->
                val binding =
                    DialogAdeonGalleryBinding.inflate(activity.layoutInflater, null, false).apply {
                        val adapter = object: GalleryAdapter() {
                            override fun onClick(iconResId: Int) {
                                (targetFragment as AdeonGalleryDialogListener)
                                    .onAdeonImageClick(iconResId)
                                dismiss()
                            }
                        }
                        initializeDeviceIcons(adapter)
                        this.imageList.adapter = adapter
                    }

                // Use the Builder class for convenient dialog construction
                val builder = AlertDialog.Builder(it)
                    .setTitle(R.string.select_image)
                    .setView(binding.root)
                builder.create()
            }
        } ?: throw IllegalStateException("Context cannot be null")
    }


    private fun initializeDeviceIcons(adapter: GalleryAdapter) {
        val icons = ArrayList<Int>()

        val list = resources.obtainTypedArray(R.array.device_icons)
        for (i in 0 until list.length()) {
            icons.add(list.getResourceId(i, -1))
        }
        list.recycle()

        adapter.submitList(icons)
    }
}
