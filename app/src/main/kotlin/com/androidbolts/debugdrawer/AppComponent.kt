package com.androidbolts.debugdrawer

import android.content.Context
import com.androidbolts.debugdrawer.data.Data
import com.androidbolts.debugdrawer.data.DataModule
import com.androidbolts.debugdrawer.data.remote.AppUrls
import dagger.Component
import javax.inject.Singleton

/**
 * Created by Subhash Acharya on 6/5/17.
 */
@Component(modules = arrayOf(AppModule::class, DataModule::class))
@Singleton
interface AppComponent {
    fun context(): Context

    fun urls(): AppUrls

    fun buildType(): BuildTypeComponent

    fun data(): Data

    fun inject(baseActivity: BaseActivity)
}