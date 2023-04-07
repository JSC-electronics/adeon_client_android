package cz.jscelectronics.adeon.ui.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.github.appintro.AppIntro2
import com.github.appintro.AppIntroFragment
import com.github.appintro.model.SliderPage
import cz.jscelectronics.adeon.R


class IntroActivity: AppIntro2() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sliderPage1 = SliderPage()
        sliderPage1.title = getString(R.string.intro_page1_title)
        sliderPage1.description = getString(R.string.intro_page1_description)
        sliderPage1.titleTypefaceFontRes = R.font.opensans_regular
        sliderPage1.descriptionTypefaceFontRes = R.font.opensans_regular
        sliderPage1.imageDrawable = R.drawable.ic_intro_slide1
        sliderPage1.backgroundDrawable = R.drawable.ic_launcher_background
        addSlide(AppIntroFragment.createInstance(sliderPage1))

        val sliderPage2 = SliderPage()
        sliderPage2.title = getString(R.string.intro_page2_title)
        sliderPage2.description = getString(R.string.intro_page2_description)
        sliderPage2.titleTypefaceFontRes = R.font.opensans_regular
        sliderPage2.descriptionTypefaceFontRes = R.font.opensans_regular
        sliderPage2.imageDrawable = R.drawable.ic_intro_slide2
        sliderPage2.backgroundDrawable = R.drawable.ic_launcher_background
        addSlide(AppIntroFragment.createInstance(sliderPage2))

        val sliderPage3 = SliderPage()
        sliderPage3.title = getString(R.string.intro_page3_title)
        sliderPage3.description = getString(R.string.intro_page3_description)
        sliderPage3.titleTypefaceFontRes = R.font.opensans_regular
        sliderPage3.descriptionTypefaceFontRes = R.font.opensans_regular
        sliderPage3.imageDrawable = R.drawable.ic_intro_slide3
        sliderPage3.backgroundDrawable = R.drawable.ic_launcher_background
        addSlide(AppIntroFragment.createInstance(sliderPage3))

        val sliderPage4 = SliderPage()
        sliderPage4.title = getString(R.string.intro_page4_title)
        sliderPage4.description = getString(R.string.intro_page4_description)
        sliderPage4.titleTypefaceFontRes = R.font.opensans_regular
        sliderPage4.descriptionTypefaceFontRes = R.font.opensans_regular
        sliderPage4.imageDrawable = R.drawable.ic_intro_slide4
        sliderPage4.backgroundDrawable = R.drawable.ic_launcher_background
        addSlide(AppIntroFragment.createInstance(sliderPage4))

        val sliderPage5 = SliderPage()
        sliderPage5.title = getString(R.string.intro_page5_title)
        sliderPage5.description = getString(R.string.intro_page5_description)
        sliderPage5.titleTypefaceFontRes = R.font.opensans_regular
        sliderPage5.descriptionTypefaceFontRes = R.font.opensans_regular
        sliderPage5.imageDrawable = R.drawable.ic_intro_slide5
        sliderPage5.backgroundDrawable = R.drawable.ic_launcher_background
        addSlide(AppIntroFragment.createInstance(sliderPage5))
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