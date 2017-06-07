package com.androidbolts.debugdrawer.data.local

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by subhashacharya on 6/4/17.
 */
@Singleton
class DebugPrefs @Inject
constructor(context: Context) {
    private val DEBUG_PREFERENCES = "_debug_preferences"
    private val IS_CUSTOM_URL = "_is_custom_url"
    private val CUSTOM_BASE_URL = "_custom_base_url"
    private val CUSTOM_BASE_IMAGE_URL = "_custom_image_base_url"
    private val SHOW_IMAGE_LOGS = "_show_image_logs"
    private val preferences: SharedPreferences

    init {
        this.preferences = context.getSharedPreferences(DEBUG_PREFERENCES, Context.MODE_PRIVATE)
    }

    var isCustomUrl: Boolean = this.preferences.getBoolean(IS_CUSTOM_URL, false)
    var customBaseUrl: String? = preferences.getString(CUSTOM_BASE_URL, null)
    var customImageUrl: String? = preferences.getString(CUSTOM_BASE_IMAGE_URL, null)
    var showImageLogs: Boolean
        get() = preferences.getBoolean(SHOW_IMAGE_LOGS, false)
        set(value) = preferences.edit().putBoolean(SHOW_IMAGE_LOGS, value).apply()


    fun setCustomUrls(baseUrl: String, baseImageUrl: String) =
            preferences.edit().putString(CUSTOM_BASE_URL, baseUrl)
                    .putString(CUSTOM_BASE_IMAGE_URL, baseImageUrl)
                    .putBoolean(IS_CUSTOM_URL, true)
                    .apply()

    fun setStagingUrl() = preferences.edit().putBoolean(IS_CUSTOM_URL, false).apply()
}