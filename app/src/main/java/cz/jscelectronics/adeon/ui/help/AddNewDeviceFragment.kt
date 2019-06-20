package cz.jscelectronics.adeon.ui.help

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.BulletSpan
import android.text.style.DynamicDrawableSpan
import android.text.style.ImageSpan
import android.text.style.URLSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import cz.jscelectronics.adeon.R
import cz.jscelectronics.adeon.databinding.FragmentHelpAddNewDeviceBinding
import cz.jscelectronics.adeon.ui.widget.CenteredImageSpan

class AddNewDeviceFragment : Fragment() {

    companion object {
        const val DOCUMENTATION_URI = "https://github.com/JSC-electronics/adeon/wiki"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentHelpAddNewDeviceBinding.inflate(inflater, container, false).apply {
            context?.let { context ->
                val fabPlaceholder = "[FAB]"
                val cameraPlaceholder = "[CAMERA]"
                val moreOptionsPlaceholder = "[MORE_OPTIONS]"

                val clickOnFabHelpText = SpannableStringBuilder(
                    context.getString(R.string.article_01_bullet_01_text)
                ).apply {
                    val startIdx = this.indexOf(fabPlaceholder)
                    setSpan(
                        CenteredImageSpan(context, R.drawable.ic_baseline_add_circle_24px, ImageSpan.ALIGN_BASELINE),
                        startIdx, startIdx + fabPlaceholder.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                firstBulletPointText.text = clickOnFabHelpText

                val capturePhotoHelpText = SpannableStringBuilder(
                    context.getString(R.string.article_01_bullet_03_text)
                ).apply {
                    val startIdx = this.indexOf(cameraPlaceholder)
                    setSpan(
                        CenteredImageSpan(context, R.drawable.ic_baseline_camera_alt_24px, ImageSpan.ALIGN_BASELINE),
                        startIdx, startIdx + cameraPlaceholder.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                thirdBulletPointText.text = capturePhotoHelpText

                val docsLabel = context.getString(R.string.article_01_bullet_06_text_docs)
                val urlSpan = URLSpan(DOCUMENTATION_URI)
                val docsHelpText = SpannableStringBuilder(
                    context.getString(
                        R.string.article_01_bullet_06_text, docsLabel
                    )
                ).apply {
                    val startIdx = this.indexOf(docsLabel)
                    setSpan(
                        urlSpan, startIdx,
                        startIdx + docsLabel.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                sixthBulletPointText.text = docsHelpText
                sixthBulletPointText.setOnClickListener {
                    urlSpan.onClick(it)
                }

                val addAnotherCommandHelpText = SpannableStringBuilder(
                    context.getString(
                        R.string.article_01_bullet_09_text
                    )
                ).apply {
                    val startIdx = this.indexOf(fabPlaceholder)
                    setSpan(
                        CenteredImageSpan(context, R.drawable.ic_baseline_add_circle_24px, ImageSpan.ALIGN_BASELINE),
                        startIdx, startIdx + fabPlaceholder.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                ninthBulletPointText.text = addAnotherCommandHelpText

                val addDeviceButton = context.getString(R.string.add_device_button)
                val addDeviceHelpText = SpannableStringBuilder(
                    context.getString(
                        R.string.article_01_bullet_10_text,
                        addDeviceButton
                    )
                ).apply {
                    val startIdx = this.indexOf(addDeviceButton)
                    setSpan(
                        android.text.style.StyleSpan(android.graphics.Typeface.BOLD), startIdx,
                        startIdx + addDeviceButton.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                tenthBulletPointText.text = addDeviceHelpText

                val duplicateDeviceText = context.getString(R.string.action_duplicate)
                val noteHelpText = SpannableStringBuilder(
                    context.getString(
                        R.string.article_01_note_text,
                        duplicateDeviceText
                    )
                ).apply {
                    var startIdx = this.indexOf(duplicateDeviceText)
                    setSpan(
                        android.text.style.StyleSpan(android.graphics.Typeface.BOLD), startIdx,
                        startIdx + duplicateDeviceText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    startIdx = this.indexOf(moreOptionsPlaceholder)
                    setSpan(
                        CenteredImageSpan(context, R.drawable.ic_more_options, ImageSpan.ALIGN_BASELINE),
                        startIdx, startIdx + moreOptionsPlaceholder.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                noteText.text = noteHelpText
            }
        }
        return binding.root
    }
}
