package com.androidbolts.debugdrawer.data

import com.androidbolts.debugdrawer.data.remote.Api
import com.androidbolts.debugdrawer.data.remote.AppUrls
import com.androidbolts.debugdrawer.data.remote.LogStoreInterceptor
import com.facebook.stetho.okhttp3.StethoInterceptor
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * Created by Subhash Acharya on 6/7/17.
 */
@Module
class DataModule {

    @Provides
    @Singleton
    internal fun httpClient(logStoreInterceptor: LogStoreInterceptor): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient.Builder()
                .addInterceptor(logStoreInterceptor)
                .addNetworkInterceptor(loggingInterceptor)//For logcat
                .addNetworkInterceptor(StethoInterceptor())
                .build()
    }

    @Provides
    @Singleton
    internal fun remoteRepo(urls: AppUrls, client: OkHttpClient): Api {
        return Retrofit.Builder()
                .client(client)
                .baseUrl(urls.baseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(Api::class.java)
    }
}