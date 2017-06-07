package com.redeem.data.local


import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

import com.androidbolts.debugdrawer.data.model.NetworkLogEntry

import javax.inject.Inject

class LogStorage @Inject constructor(context: Context) : SQLiteOpenHelper(context, LogStorage.DB_NAME, null, LogStorage.DB_VERSION) {

    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, i: Int, i1: Int) {
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES)
        onCreate(sqLiteDatabase)
    }

    fun insertLog(entry: NetworkLogEntry) {
        val values = ContentValues()
        values.put(NetworkLogEntry.COLUMN_NAME_LOG, entry.log)
        values.put(NetworkLogEntry.COLUMN_NAME_TIME, entry.createdAt)
        val db = writableDatabase
        db.insert(NetworkLogEntry.TABLE_NAME, null, values)
    }

    val allLogs: Cursor
        get() = readableDatabase.rawQuery("select * from " + NetworkLogEntry.TABLE_NAME + " order by " + NetworkLogEntry._ID + " desc", null)

    fun clearLogs() {
        writableDatabase.execSQL("delete from " + NetworkLogEntry.TABLE_NAME)
    }

    companion object {
        val DB_NAME = "log_db"
        val DB_VERSION = 1

        private val SQL_CREATE_ENTRIES =
                "CREATE TABLE " + NetworkLogEntry.TABLE_NAME + " (" +
                        NetworkLogEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        NetworkLogEntry.COLUMN_NAME_LOG + " TEXT," +
                        NetworkLogEntry.COLUMN_NAME_TIME + " INTEGER)"

        private val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + NetworkLogEntry.TABLE_NAME
    }
}
