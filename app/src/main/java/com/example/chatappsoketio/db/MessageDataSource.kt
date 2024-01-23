package com.example.chatappsoketio.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import com.example.chatappsoketio.model.Message

class MessageDataSource(context: Context) {

    private val dbHelper = MessageDbHelper(context)
    private var database: SQLiteDatabase? = null

    fun open() {
        try {
            database = dbHelper.writableDatabase
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    fun close() {
        dbHelper.close()
    }

    fun insertMessage(message: Message) {
        val values = ContentValues().apply {
            put(MessageDbHelper.MessageEntry.COLUMN_CONTENT, message.content)
            put(MessageDbHelper.MessageEntry.COLUMN_IS_SENT, if (message.isSentMessage) 1 else 0)
            put(MessageDbHelper.MessageEntry.COLUMN_TIMESTAMP, message.timestamp)
        }

        database?.insert(MessageDbHelper.MessageEntry.TABLE_NAME, null, values)
    }

    fun getAllMessages(): List<Message> {
        val messages = mutableListOf<Message>()

        val cursor: Cursor? = database?.query(
            MessageDbHelper.MessageEntry.TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            MessageDbHelper.MessageEntry.COLUMN_TIMESTAMP
        )

        cursor?.use {
            while (it.moveToNext()) {
                val content = it.getString(it.getColumnIndex(MessageDbHelper.MessageEntry.COLUMN_CONTENT))
                val isSent = it.getInt(it.getColumnIndex(MessageDbHelper.MessageEntry.COLUMN_IS_SENT)) == 1
                val timestamp = it.getLong(it.getColumnIndex(MessageDbHelper.MessageEntry.COLUMN_TIMESTAMP))

                messages.add(Message(content, isSent, timestamp))
            }
        }

        return messages
    }
}
