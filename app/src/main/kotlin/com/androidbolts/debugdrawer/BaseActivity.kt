package com.androidbolts.debugdrawer

import android.os.Bundle
import com.androidbolts.debugdrawer.data.Data
import com.androidbolts.debugdrawer.data.remote.AppUrls
import javax.inject.Inject

/**
 * Created by Subhash Acharya on 6/5/17.
 */
open class BaseActivity : BuildTypeBaseActivity() {
    @Inject
    lateinit var data: Data

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.component(this).inject(this)
    }
}