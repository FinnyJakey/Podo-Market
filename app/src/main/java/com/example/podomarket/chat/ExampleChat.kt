package com.example.podomarket.chat

// 예시용 메세지 정보 클래스
data class ExampleChat(
    val message: String,   // 메시지 내용
    val sender: String     // 보낸 ("me" or "you")
)