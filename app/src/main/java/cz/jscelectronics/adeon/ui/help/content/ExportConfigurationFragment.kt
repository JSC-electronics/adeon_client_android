package cz.jscelectronics.adeon.ui.help.content

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import cz.jscelectronics.adeon.R
import cz.jscelectronics.adeon.databinding.FragmentHelpExportConfigurationBinding
import cz.jscelectronics.adeon.ui.help.MORE_OPTIONS_PLACEHOLDER
import cz.jscelectronics.adeon.ui.widget.CenteredImageSpan

class ExportConfigurationFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentHelpExportConfigurationBinding.inflate(inflater, container, false).apply {
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

                val exportConfigurationText = context.getString(R.string.export_configuration)
                val selectExportCfgText = SpannableStringBuilder(
                    context.getString(
                        R.string.article_03_bullet_02_text,
                        exportConfigurationText
                    )
                ).apply {
                    val startIdx = this.indexOf(exportConfigurationText)
                    setSpan(
                        android.text.style.StyleSpan(android.graphics.Typeface.BOLD), startIdx,
                        startIdx + exportConfigurationText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                secondBulletPointText.text = selectExportCfgText

                val saveButtonText = context.getString(R.string.save_button)
                val clickOnSaveText = SpannableStringBuilder(
                    context.getString(
                        R.string.article_03_bullet_05_text,
                        saveButtonText
                    )
                ).apply {
                    val startIdx = this.indexOf(saveButtonText)
                    setSpan(
                        android.text.style.StyleSpan(android.graphics.Typeface.BOLD), startIdx,
                        startIdx + saveButtonText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                fifthBulletPointText.text = clickOnSaveText
            }
        }
        return binding.root
    }
}
