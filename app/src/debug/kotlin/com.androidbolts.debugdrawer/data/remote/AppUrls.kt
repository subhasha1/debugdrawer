package com.androidbolts.debugdrawer.data.remote

import android.content.Context
import com.androidbolts.debugdrawer.R
import javax.inject.Inject
import javax.inject.Singleton
import com.androidbolts.debugdrawer.data.local.DebugPrefs


/**
 * Created by subhashacharya on 6/4/17.
 */

@Singleton
class AppUrls @Inject
internal constructor(context: Context,private val debugPrefs: DebugPrefs) {
    private val baseUrl: String = context.getString(R.string.base_url)
    private val baseImageUrl: String = context.getString(R.string.base_image_url)
    val custom: Boolean = debugPrefs.isCustomUrl

    var customBaseUrl = debugPrefs.customBaseUrl ?: ""
    var customBaseImageUrl = debugPrefs.customImageUrl ?: ""

    fun baseUrl(): String = if (custom) customBaseUrl else baseUrl

    fun baseImageUrl(): String = if (custom) customBaseImageUrl else baseImageUrl


    fun setCustomUrls(baseUrl: String, baseImageUrl: String) {
        debugPrefs.setCustomUrls(baseUrl, baseImageUrl)
    }

    fun setStagingUrl() {
        debugPrefs.setStagingUrl()
    }
}
