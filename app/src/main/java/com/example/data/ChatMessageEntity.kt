package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey val id: String,
    val senderId: String,
    val receiverId: String,
    val senderName: String,
    val messageText: String,
    val isFromMe: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)
