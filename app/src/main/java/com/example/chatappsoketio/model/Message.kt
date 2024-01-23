package com.example.chatappsoketio.model

data class Message(
    val content: String,
    val isSentMessage: Boolean,
    val timestamp: Long
)
