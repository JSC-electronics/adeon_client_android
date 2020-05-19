package cz.jscelectronics.adeon.ui.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.github.paolorotolo.appintro.AppIntro2
import com.github.paolorotolo.appintro.AppIntroFragment
import com.github.paolorotolo.appintro.model.SliderPage
import cz.jscelectronics.adeon.R


class WhatsNewActivity: AppIntro2() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setFadeAnimation()

        val sliderPage1 = SliderPage()
        sliderPage1.title = getString(R.string.intro_page1_title)
        sliderPage1.description = getString(R.string.intro_page1_description)
        sliderPage1.titleTypefaceFontRes = R.font.opensans_regular
        sliderPage1.descTypefaceFontRes = R.font.opensans_regular
        sliderPage1.imageDrawable = R.drawable.ic_intro_slide1
        sliderPage1.bgDrawable = R.drawable.ic_launcher_background
        addSlide(AppIntroFragment.newInstance(sliderPage1))

        val sliderPage2 = SliderPage()
        sliderPage2.title = getString(R.string.intro_page2_title)
        sliderPage2.description = getString(R.string.intro_page2_description)
        sliderPage2.titleTypefaceFontRes = R.font.opensans_regular
        sliderPage2.descTypefaceFontRes = R.font.opensans_regular
        sliderPage2.imageDrawable = R.drawable.ic_intro_slide2
        sliderPage2.bgDrawable = R.drawable.ic_launcher_background
        addSlide(AppIntroFragment.newInstance(sliderPage2))
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