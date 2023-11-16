package com.example.podomarket.model

import com.google.firebase.Timestamp

data class ChatRoomModel(
    val boardUuid: String,
    val chats: List<ChatModel>,
    val participants: List<String>,
    val recentTime: Timestamp
)

data class ChatModel(
    val createdAt: Timestamp,
    val message: String,
    val userId: String,
    val userName: String
)
