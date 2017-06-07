package com.androidbolts.debugdrawer

import android.content.Context
import android.os.Build
import android.text.Html
import android.text.Html.FROM_HTML_MODE_LEGACY
import android.widget.Toast
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.text.TextUtils


/**
 * Created by subhashacharya on 6/4/17.
 */

fun String.toHtml() = if (Build.VERSION.SDK_INT < 24) Html.fromHtml(this) else Html.fromHtml(this, FROM_HTML_MODE_LEGACY)


fun Context.email(email: String = "", subject: String, body: String) {
    val intent = Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", email, null))
    if (!TextUtils.isEmpty(subject))
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
    if (!TextUtils.isEmpty(body))
        intent.putExtra(Intent.EXTRA_TEXT, body)
    try {
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        e.printStackTrace()
        Toast.makeText(this, "No email clients found", Toast.LENGTH_SHORT).show()
    }
}