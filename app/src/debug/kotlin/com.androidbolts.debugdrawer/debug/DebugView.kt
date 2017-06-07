package com.androidbolts.debugdrawer.debug

import android.content.Context
import android.support.v4.widget.NestedScrollView
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.*
import com.androidbolts.debugdrawer.*
import com.androidbolts.debugdrawer.data.remote.AppUrls
import com.jakewharton.processphoenix.ProcessPhoenix
import com.jakewharton.scalpel.ScalpelFrameLayout
import okhttp3.HttpUrl
import java.text.SimpleDateFormat
import java.util.*

/**
 * Braindigit
 * Created on 2/8/17.
 */

class DebugView(context: Context, attrs: AttributeSet) : NestedScrollView(context, attrs) {
    private var scalpelFrame: ScalpelFrameLayout? = null

    init {
        if (!isInEditMode) {
            val root = LayoutInflater.from(context).inflate(R.layout.debug_view_content, this, true)
            val scalpel = root.findViewById(R.id.scalpel) as Switch
            val wireframe = root.findViewById(R.id.wireframe) as Switch
            val drawIds = root.findViewById(R.id.drawIds) as Switch
            val tvEndpoint = root.findViewById(R.id.tvEndpoint) as TextView
            val tvBaseUrl = root.findViewById(R.id.tvEndpoint) as TextView
            val tvBaseImageUrl = root.findViewById(R.id.tvBaseImageUrl) as TextView
            val tvVersionCode = root.findViewById(R.id.tvVersionCode) as TextView
            val tvVersionName = root.findViewById(R.id.tvVersionName) as TextView
            val tvBuildTime = root.findViewById(R.id.tvBuildTime) as TextView
            val tvGitSha = root.findViewById(R.id.tvGitSha) as TextView
            val showImageLogs = root.findViewById(R.id.showImageLogs) as Switch
            val tvGitCommitMessage = root.findViewById(R.id.tvGitCommitMessage) as TextView
            emptyArray<String>()
            val btnShowLog = root.findViewById(R.id.btnShowLog) as Button
            scalpel.setOnCheckedChangeListener({ _, checked ->
                if (scalpelFrame != null) {
                    scalpelFrame?.isLayerInteractionEnabled = checked
                    wireframe.isEnabled = checked
                    drawIds.isEnabled = checked
                }
            })
            wireframe.setOnCheckedChangeListener({ _, checked -> scalpelFrame!!.setDrawViews(!checked) })
            drawIds.setOnCheckedChangeListener({ _, checked -> scalpelFrame!!.setDrawIds(checked) })
            val urls = App.component(context).urls()
            tvEndpoint.text = context.getString(R.string.endpoint_format,
                    context.getString(if (urls.custom) R.string.custom else R.string.staging))
            tvBaseUrl.text = (context.getString(R.string.base_url_format) + "\n" + urls.baseUrl()).toHtml()
            tvBaseImageUrl.text = (context.getString(R.string.base_image_url_format) + "\n" + urls.baseImageUrl()).toHtml()
            tvEndpoint.setOnClickListener({ showCustomize(urls) })
            btnShowLog.setOnClickListener({ showLogDialog() })
            tvVersionCode.text = BuildConfig.VERSION_CODE.toString()
            tvVersionName.text = BuildConfig.VERSION_NAME
            val dateFormat = SimpleDateFormat("hh:mm a dd.MM.yyyy", Locale.US)
            tvBuildTime.text = dateFormat.format(Date(BuildConfig.BUILD_TIMESTAMP))
            tvGitSha.text = BuildConfig.GIT_SHA
            tvGitCommitMessage.text = BuildConfig.GIT_COMMIT_MESSAGE
            tvGitCommitMessage.setOnClickListener({
                AlertDialog.Builder(context)
                        .setTitle("Git Commit Message").setMessage(BuildConfig.GIT_COMMIT_MESSAGE).show()
            })

            val buildTypeComponent = App.component(context).buildType()
            showImageLogs.isChecked = buildTypeComponent.debugPrefs().showImageLogs
            showImageLogs.setOnCheckedChangeListener({ _, isChecked ->
                buildTypeComponent.debugPrefs().showImageLogs = isChecked
                buildTypeComponent.logInterceptor().showImageLogs(isChecked)
            })
        }
    }

    private fun showLogDialog() {
        val context = context
        val component = App.component(context).buildType()
        val adapter = LogAdapter(context, component.logStorage().allLogs)
        AlertDialog.Builder(context)
                .setTitle(R.string.network_logs)
                .setAdapter(adapter, { _, position ->
                    val logEntry = adapter.getLogEntry(position)
                    val date = SimpleDateFormat("yyyy.MM.dd G 'at' h:mm:ss a",
                            Locale.US).format(Date(logEntry.createdAt))
                    val log = logEntry.log + "\n\n Log Date: " + date
                    context.email(subject = "Redeem Network Log", body = log)
                })
                .setNegativeButton(R.string.clear_logs, { _, _ -> component.logStorage().clearLogs() })
                .show()
    }

    fun showCustomize(urls: AppUrls) {
        val context = context
        val dialogRoot = LayoutInflater.from(context).inflate(R.layout.layout_url_setup_dialog, this, false)
        val etBaseUrl = dialogRoot.findViewById(R.id.etBaseUrl) as EditText
        val etBaseImageUrl = dialogRoot.findViewById(R.id.etBaseImageUrl) as EditText
        val rgEndpoint = dialogRoot.findViewById(R.id.rgEndpoint) as RadioGroup
        val rbCustom = dialogRoot.findViewById(R.id.rbCustom) as RadioButton
        val rbStaging = dialogRoot.findViewById(R.id.rbStaging) as RadioButton
        if (!TextUtils.isEmpty(urls.customBaseUrl)) {
            etBaseUrl.setText(urls.customBaseUrl)
        }
        if (!TextUtils.isEmpty(urls.customBaseImageUrl)) {
            etBaseImageUrl.setText(urls.customBaseImageUrl)
        }
        rgEndpoint.setOnCheckedChangeListener({ _, checkedId ->
            etBaseImageUrl.isEnabled = checkedId == R.id.rbCustom
            etBaseUrl.isEnabled = checkedId == R.id.rbCustom
        })
        if (urls.custom)
            rbCustom.isChecked = true
        else
            rbStaging.isChecked = true
        AlertDialog.Builder(context)
                .setView(dialogRoot)
                .setPositiveButton("Save", { _, _ ->
                    if (rbCustom.isChecked) {
                        val baseUrl = HttpUrl.parse(etBaseUrl.text.toString().trim())
                        val baseImageUrl = HttpUrl.parse(etBaseImageUrl.text.toString().trim())
                        if (baseUrl != null && baseImageUrl != null) {
                            urls.setCustomUrls(baseUrl.toString(), baseImageUrl.toString())
                            ProcessPhoenix.triggerRebirth(context)
                        } else {
                            Toast.makeText(context, "Invalid Urls", Toast.LENGTH_SHORT).show()
                        }
                    } else if (rbStaging.isChecked && urls.custom) {
                        urls.setStagingUrl()
                        ProcessPhoenix.triggerRebirth(context)
                    }
                }).show()
    }

    fun setScalpelFrame(scalpelFrame: ScalpelFrameLayout) {
        this.scalpelFrame = scalpelFrame
    }
}
