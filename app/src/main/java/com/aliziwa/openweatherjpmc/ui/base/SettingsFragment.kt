package com.aliziwa.openweatherjpmc.ui.base

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.aliziwa.openweatherjpmc.R

/**
 * Using this to hold the API KEY
 *
 * Please add Openweather API Key to settings screen and should be able to make calls
 * Units of Measure
 * Forecast count
 * and others were to be added here, time constrained. :)
 */
class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.config_settings, rootKey)
    }
}