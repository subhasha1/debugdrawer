package com.androidbolts.debugdrawer.data.model


import android.provider.BaseColumns

class NetworkLogEntry : BaseColumns {

    var log: String? = null
    var createdAt: Long = 0

    constructor(log: String) {
        this.log = log
        this.createdAt = System.currentTimeMillis()
    }

    constructor(log: String, createdAt: Long) {
        this.log = log
        this.createdAt = createdAt
    }

    companion object {
        val TABLE_NAME = "log_entry"
        val _ID = BaseColumns._ID
        val COLUMN_NAME_LOG = "log"
        val COLUMN_NAME_TIME = "created_at"
    }
}
