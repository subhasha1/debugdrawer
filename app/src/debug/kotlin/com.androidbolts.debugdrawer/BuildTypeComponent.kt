package com.androidbolts.debugdrawer

import com.androidbolts.debugdrawer.data.local.DebugPrefs
import com.androidbolts.debugdrawer.data.remote.LogStoreInterceptor
import com.redeem.data.local.LogStorage

import dagger.Subcomponent

@BuildScope
@Subcomponent
interface BuildTypeComponent {
    fun logStorage(): LogStorage

    fun debugPrefs(): DebugPrefs

    fun logInterceptor(): LogStoreInterceptor
}
