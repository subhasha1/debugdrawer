package com.androidbolts.debugdrawer

import android.content.Context
import dagger.Module
import dagger.Provides

/**
 * Created by Subhash Acharya on 6/5/17.
 */
@Module
class AppModule(val app: App) {

    @Provides
    fun provideContext(): Context = app

}