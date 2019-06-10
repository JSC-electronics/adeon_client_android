package cz.jsc.electronics.smscontrol.intro

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.github.paolorotolo.appintro.AppIntro
import com.github.paolorotolo.appintro.AppIntroFragment
import com.github.paolorotolo.appintro.model.SliderPage
import cz.jsc.electronics.smscontrol.R


class IntroActivity: AppIntro() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setFadeAnimation()

        val sliderPage1 = SliderPage()
        sliderPage1.title = "Welcome!"
        sliderPage1.description = "This is a demo of the AppIntro library, using the second layout."
        sliderPage1.imageDrawable = R.drawable.ic_slide1
        sliderPage1.bgDrawable = R.drawable.back_slide1
        addSlide(AppIntroFragment.newInstance(sliderPage1))

        val sliderPage2 = SliderPage()
        sliderPage2.title = "Gradients!"
        sliderPage2.description = "This text is written on a gradient background"
        sliderPage2.imageDrawable = R.drawable.ic_slide1
        sliderPage2.bgDrawable = R.drawable.back_slide2
        addSlide(AppIntroFragment.newInstance(sliderPage2))

        val sliderPage3 = SliderPage()
        sliderPage3.title = "Clean App Intros"
        sliderPage3.description =
            "This library offers developers the ability to add clean app intros at the start of their apps."
        sliderPage3.imageDrawable = R.drawable.ic_slide2
        sliderPage3.bgDrawable = R.drawable.back_slide3
        addSlide(AppIntroFragment.newInstance(sliderPage3))

        val sliderPage4 = SliderPage()
        sliderPage4.title = "Simple, yet Customizable"
        sliderPage4.description =
            "The library offers a lot of customization, while keeping it simple for those that like simple."
        sliderPage4.titleTypefaceFontRes = R.font.opensans_regular
        sliderPage4.descTypefaceFontRes = R.font.opensans_regular
        sliderPage4.imageDrawable = R.drawable.ic_slide3
        sliderPage4.bgDrawable = R.drawable.back_slide4
        addSlide(AppIntroFragment.newInstance(sliderPage4))

        val sliderPage5 = SliderPage()
        sliderPage5.title = "Explore"
        sliderPage5.description = "Feel free to explore the rest of the library demo!"
        sliderPage5.imageDrawable = R.drawable.ic_slide4
        sliderPage5.bgDrawable = R.drawable.back_slide5
        addSlide(AppIntroFragment.newInstance(sliderPage5))
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