package com.example.podomarket.model

import com.google.firebase.Timestamp
import java.io.File

data class BoardModel(
    val uuid: String,
    val content: String,
    val createdAt: Timestamp,
    val pictures: List<String>,
    val price: Number,
    val sold: Boolean,
    val title: String,
    val userId: String,
    val userName: String
)
