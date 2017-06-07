package com.androidbolts.debugdrawer.data.remote

import retrofit2.Call
import retrofit2.http.GET

/**
 * Created by Subhash Acharya on 6/7/17.
 */
interface Api {

    @GET("5937888611000082096bb648")
    fun helloWorld(): Call<HashMap<String, String>>
}