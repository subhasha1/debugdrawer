package com.androidbolts.debugdrawer

import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.view.ViewGroup
import com.jakewharton.scalpel.ScalpelFrameLayout
import com.mattprecious.telescope.EmailDeviceInfoLens
import com.mattprecious.telescope.ScreenshotMode
import com.mattprecious.telescope.TelescopeLayout
import com.androidbolts.debugdrawer.debug.DebugView


/**
 * Created by Subhash Acharya on 6/2/17.
 */
open class BuildTypeBaseActivity : AppCompatActivity() {

    override fun onStart() {
        super.onStart()
        val decorView = window.decorView
        val rootView = decorView.findViewById(android.R.id.content) as ViewGroup
        if (rootView.childCount > 0 && rootView.findViewById(R.id.scalpelFrame) == null) {
            val content = rootView.getChildAt(0)
            rootView.removeView(content)
            val debugDrawer: DrawerLayout = layoutInflater.inflate(R.layout.layout_debug_drawer, rootView, false) as DrawerLayout
            val telescopeLayout: TelescopeLayout = debugDrawer.findViewById(R.id.telescope) as TelescopeLayout
            val scalpelFrame: ScalpelFrameLayout = debugDrawer.findViewById(R.id.scalpelFrame) as ScalpelFrameLayout
            val debugView: DebugView = debugDrawer.findViewById(R.id.debugView) as DebugView
            telescopeLayout.setLens(EmailDeviceInfoLens(this, "Bug Report"))
            telescopeLayout.setPointerCount(3)
            telescopeLayout.setScreenshotMode(ScreenshotMode.CANVAS)
            scalpelFrame.addView(content)
            debugView.setScalpelFrame(scalpelFrame)
            rootView.addView(debugDrawer)
        }
    }
}