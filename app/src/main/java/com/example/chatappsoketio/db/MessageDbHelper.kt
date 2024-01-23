package com.example.chatappsoketio.db
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

class MessageDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "message.db"
        const val DATABASE_VERSION = 1
    }

    object MessageEntry : BaseColumns {
        const val TABLE_NAME = "messages"
        const val COLUMN_CONTENT = "content"
        const val COLUMN_IS_SENT = "is_sent"
        const val COLUMN_TIMESTAMP = "timestamp"
    }

    private val SQL_CREATE_ENTRIES =
        "CREATE TABLE ${MessageEntry.TABLE_NAME} (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                "${MessageEntry.COLUMN_CONTENT} TEXT," +
                "${MessageEntry.COLUMN_IS_SENT} INTEGER," +
                "${MessageEntry.COLUMN_TIMESTAMP} INTEGER)"

    private val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${MessageEntry.TABLE_NAME}"

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }
}
