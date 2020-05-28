package cz.jscelectronics.adeon.data

import android.content.Context
import cz.jscelectronics.adeon.utilities.ONBOARDING_PREF_FILE_NAME


class PrefManager(val context: Context) {
    companion object {
        private const val PRIVATE_MODE = 0
        // We can't redefine the value. Otherwise, users receiving Adeon update
        // would see First Launch Screen again
        private const val ONBOARDING_FIRST_LAUNCH = "IsFirstTimeLaunch"
        private const val ONBOARDING_WHATS_NEW = "OnboardingWhatsNew"
    }

    /**
     * In an old logic, we checked if an app was launched for the first time.
     * Now, we reworked the logic to show if an onboarding screen was shown.
     * Users who don't have an updated app and who saw the intro screen,
     * have the value IsFirstTimeLaunch set to false.
     *
     * However, we reverted the logic. Therefore, we check if an app contains value
     * for ONBOARDING_FIRST_LAUNCH and not for ONBOARDING_WHATS_NEW, so that we
     * can differentiate new users from the old ones who saw the intro onboarding.
     */
    fun firstTimeOnboardingShown(): Boolean {
        return if (context.getSharedPreferences(ONBOARDING_PREF_FILE_NAME, PRIVATE_MODE)
                .contains(ONBOARDING_FIRST_LAUNCH) &&
            !context.getSharedPreferences(ONBOARDING_PREF_FILE_NAME, PRIVATE_MODE)
                .contains(ONBOARDING_WHATS_NEW)
        ) {
            !context.getSharedPreferences(ONBOARDING_PREF_FILE_NAME, PRIVATE_MODE)
                .getBoolean(ONBOARDING_FIRST_LAUNCH, false)
        } else {
            context.getSharedPreferences(ONBOARDING_PREF_FILE_NAME, PRIVATE_MODE)
                .getBoolean(ONBOARDING_FIRST_LAUNCH, false)
        }
    }

    fun whatsNewOnboardingShown(): Boolean {
        return context.getSharedPreferences(ONBOARDING_PREF_FILE_NAME, PRIVATE_MODE)
            .getBoolean(ONBOARDING_WHATS_NEW, false)
    }

    fun setOnboardingCompleted() {
        val editor = context.getSharedPreferences(ONBOARDING_PREF_FILE_NAME, PRIVATE_MODE).edit()
        editor.putBoolean(ONBOARDING_FIRST_LAUNCH, true)
        editor.putBoolean(ONBOARDING_WHATS_NEW, true)
        editor.apply()
    }
}