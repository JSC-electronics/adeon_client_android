package cz.jscelectronics.adeon.ui.help.content

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import cz.jscelectronics.adeon.R
import cz.jscelectronics.adeon.databinding.FragmentHelpImportConfigurationBinding
import cz.jscelectronics.adeon.ui.help.MORE_OPTIONS_PLACEHOLDER
import cz.jscelectronics.adeon.ui.widget.CenteredImageSpan

class ImportConfigurationFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentHelpImportConfigurationBinding.inflate(inflater, container, false).apply {
            context?.let { context ->
                val clickOnMoreText = SpannableStringBuilder(
                    context.getString(
                        R.string.article_03_bullet_01_text
                    )
                ).apply {
                    val startIdx = this.indexOf(MORE_OPTIONS_PLACEHOLDER)
                    setSpan(
                        CenteredImageSpan.getInstance(context, R.drawable.ic_more_options),
                        startIdx, startIdx + MORE_OPTIONS_PLACEHOLDER.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                firstBulletPointText.text = clickOnMoreText

                val importConfigurationText = context.getString(R.string.import_configuration)
                val selectExportCfgText = SpannableStringBuilder(
                    context.getString(
                        R.string.article_03_bullet_02_text,
                        importConfigurationText
                    )
                ).apply {
                    val startIdx = this.indexOf(importConfigurationText)
                    setSpan(
                        android.text.style.StyleSpan(android.graphics.Typeface.BOLD), startIdx,
                        startIdx + importConfigurationText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                secondBulletPointText.text = selectExportCfgText
            }
        }
        return binding.root
    }
}
