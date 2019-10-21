package cz.jscelectronics.adeon.ui.help.content

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.URLSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import cz.jscelectronics.adeon.R
import cz.jscelectronics.adeon.databinding.FragmentHelpSendCommandsBinding
import cz.jscelectronics.adeon.ui.help.CHECKBOX_PLACEHOLDER
import cz.jscelectronics.adeon.ui.help.DOCUMENTATION_ROOT_URI
import cz.jscelectronics.adeon.ui.help.SEND_COMMAND_PLACEHOLDER
import cz.jscelectronics.adeon.ui.widget.CenteredImageSpan

class SendCommandsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentHelpSendCommandsBinding.inflate(inflater, container, false).apply {
            context?.let { context ->
                val sendTextCommand = SpannableStringBuilder(
                    context.getString(R.string.article_05_bullet_02_text)).apply {
                    val startIdx = this.indexOf(SEND_COMMAND_PLACEHOLDER)
                    setSpan(
                        CenteredImageSpan.getInstance(context, R.drawable.ic_send_text_command_help_24px),
                        startIdx, startIdx + SEND_COMMAND_PLACEHOLDER.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                secondBulletPointText.text = sendTextCommand

                val docsLabel = context.getString(R.string.article_01_bullet_06_text_docs)
                val urlSpan = URLSpan(DOCUMENTATION_ROOT_URI)
                val chooseCommandText = SpannableStringBuilder(
                    context.getString(R.string.article_05_bullet_03_text, docsLabel)
                ).apply {
                    var startIdx = this.indexOf(CHECKBOX_PLACEHOLDER)
                    setSpan(
                        CenteredImageSpan.getInstance(context, R.drawable.ic_baseline_check_box_24px),
                        startIdx, startIdx + CHECKBOX_PLACEHOLDER.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    startIdx = this.indexOf(docsLabel)
                    setSpan(
                        urlSpan, startIdx,
                        startIdx + docsLabel.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                thirdBulletPointText.text = chooseCommandText
                thirdBulletPointText.setOnClickListener {
                    urlSpan.onClick(it)
                }

                val sendMessageText = context.getString(R.string.send_sms_button)
                val clickOnSendButtonText = SpannableStringBuilder(
                    context.getString(
                        R.string.article_05_bullet_04_text,
                        sendMessageText
                    )
                ).apply {
                    val startIdx = this.indexOf(sendMessageText)
                    setSpan(
                        android.text.style.StyleSpan(android.graphics.Typeface.BOLD), startIdx,
                        startIdx + sendMessageText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                fourthBulletPointText.text = clickOnSendButtonText
            }
        }
        return binding.root
    }
}
