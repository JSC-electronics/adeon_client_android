package cz.jscelectronics.adeon.data

import android.content.Context
import cz.jscelectronics.adeon.utilities.ONBOARDING_PREF_FILE_NAME


class PrefManager(val context: Context) {
    companion object {
        private const val PRIVATE_MODE = 0
        private const val IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch"
    }

    fun isFirstTimeLaunch(): Boolean {
        return context.getSharedPreferences(ONBOARDING_PREF_FILE_NAME, PRIVATE_MODE)
            .getBoolean(IS_FIRST_TIME_LAUNCH, true)
    }

    fun setFirstTimeLaunch(isFirstTime: Boolean) {
        val editor = context.getSharedPreferences(ONBOARDING_PREF_FILE_NAME, PRIVATE_MODE).edit()
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime)
        editor.apply()
    }
}