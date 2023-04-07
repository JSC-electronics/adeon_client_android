package cz.jscelectronics.adeon.ui.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.github.appintro.AppIntro2
import com.github.appintro.AppIntroFragment
import com.github.appintro.model.SliderPage
import cz.jscelectronics.adeon.R


class WhatsNewActivity: AppIntro2() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sliderPage1 = SliderPage()
        sliderPage1.title = getString(R.string.whatsnew_title)
        sliderPage1.description = getString(R.string.whatsnew_page1_description)
        sliderPage1.titleTypefaceFontRes = R.font.opensans_regular
        sliderPage1.descriptionTypefaceFontRes = R.font.opensans_regular
        sliderPage1.imageDrawable = R.drawable.whatsnew_slide1
        sliderPage1.backgroundDrawable = R.drawable.ic_launcher_background
        addSlide(AppIntroFragment.createInstance(sliderPage1))

        val sliderPage2 = SliderPage()
        sliderPage2.title = getString(R.string.whatsnew_title)
        sliderPage2.description = getString(R.string.whatsnew_page2_description)
        sliderPage2.titleTypefaceFontRes = R.font.opensans_regular
        sliderPage2.descriptionTypefaceFontRes = R.font.opensans_regular
        sliderPage2.imageDrawable = R.drawable.whatsnew_slide2
        sliderPage2.backgroundDrawable = R.drawable.ic_launcher_background
        addSlide(AppIntroFragment.createInstance(sliderPage2))

        val sliderPage3 = SliderPage()
        sliderPage3.title = getString(R.string.whatsnew_title)
        sliderPage3.description = getString(R.string.whatsnew_page3_description)
        sliderPage3.titleTypefaceFontRes = R.font.opensans_regular
        sliderPage3.descriptionTypefaceFontRes = R.font.opensans_regular
        sliderPage3.imageDrawable = R.drawable.whatsnew_slide3
        sliderPage3.backgroundDrawable = R.drawable.ic_launcher_background
        addSlide(AppIntroFragment.createInstance(sliderPage3))
    }

    override fun onSkipPressed(currentFragment: Fragment?) {
        super.onSkipPressed(currentFragment)
        finish()
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        finish()
    }
}