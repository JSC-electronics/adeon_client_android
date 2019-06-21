package cz.jscelectronics.adeon.ui.help.content

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import cz.jscelectronics.adeon.R
import cz.jscelectronics.adeon.databinding.FragmentHelpDeleteDeviceBinding
import cz.jscelectronics.adeon.ui.help.MORE_OPTIONS_PLACEHOLDER
import cz.jscelectronics.adeon.ui.widget.CenteredImageSpan

class DeleteDeviceFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentHelpDeleteDeviceBinding.inflate(inflater, container, false).apply {
            context?.let { context ->
                val deleteDeviceText = context.getString(R.string.action_remove_device)
                val methodOneHelpText = SpannableStringBuilder(
                    context.getString(
                        R.string.article_02_bullet_01_text,
                        deleteDeviceText
                    )
                ).apply {
                    var startIdx = this.indexOf(deleteDeviceText)
                    setSpan(
                        android.text.style.StyleSpan(android.graphics.Typeface.BOLD), startIdx,
                        startIdx + deleteDeviceText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    startIdx = this.indexOf(MORE_OPTIONS_PLACEHOLDER)
                    setSpan(
                        CenteredImageSpan(context, R.drawable.ic_more_options, ImageSpan.ALIGN_BASELINE),
                        startIdx, startIdx + MORE_OPTIONS_PLACEHOLDER.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                firstBulletPointText.text = methodOneHelpText

                val undoText = context.getString(R.string.undo)
                val noteHelpText = SpannableStringBuilder(
                    context.getString(
                        R.string.article_02_note_text,
                        undoText
                    )
                ).apply {
                    val startIdx = this.indexOf(undoText)
                    setSpan(
                        android.text.style.StyleSpan(android.graphics.Typeface.BOLD), startIdx,
                        startIdx + undoText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                noteText.text = noteHelpText
            }
        }
        return binding.root
    }
}
