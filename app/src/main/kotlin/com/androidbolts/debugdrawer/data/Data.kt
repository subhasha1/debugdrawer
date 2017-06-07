package com.androidbolts.debugdrawer.data

import com.androidbolts.debugdrawer.data.remote.Api
import retrofit2.Call
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Subhash Acharya on 6/7/17.
 */

@Singleton
class Data @Inject constructor(private val api: Api) {

    fun helloWorld() = api.helloWorld()

}