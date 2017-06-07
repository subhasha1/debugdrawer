package com.androidbolts.debugdrawer

import android.app.Application
import android.content.Context

/**
 * Created by subhashacharya on 6/4/17.
 */
class App : Application() {
    private lateinit var component: AppComponent

    override fun onCreate() {
        super.onCreate()
        component = DaggerAppComponent.builder().appModule(AppModule(this)).build()
    }

    companion object {
        fun component(context: Context): AppComponent = (context.applicationContext as App).component
    }
}