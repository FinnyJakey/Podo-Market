package com.example.podomarket.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.podomarket.model.ChatModel
import com.example.podomarket.model.ChatRoomModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class ChatViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    val chatCollection = db.collection("Chat")

    fun getMyAllChatRooms(uid: String, onComplete: (List<ChatRoomModel>) -> Unit) {
        chatCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                return@addSnapshotListener
            }

            val myChatRooms = mutableListOf<ChatRoomModel>()

            snapshot?.forEach { document ->
                val participants = document.get("participants") as List<String>

                if (participants.contains(uid)) {
                    val boardId = document.get("board_id") as String
                    val chats = document.get("chats") as List<HashMap<String, Any>>

                    val chatsList = mutableListOf<ChatModel>()

                    chats.forEach {
                        val createdAt = it["created_at"] as Timestamp
                        val message = it["message"] as String
                        val userId = it["user_id"] as String
                        val userName = it["user_name"] as String

                        chatsList.add(ChatModel(createdAt, message, userId, userName))
                    }

                    myChatRooms.add(ChatRoomModel(boardId, chatsList, participants))
                }

                onComplete(myChatRooms)
            }
        }
    }

    fun getAllChatsFromRoom(chatRoomUuid: String, onComplete: (List<ChatModel>) -> Unit) {
        chatCollection.document(chatRoomUuid).get()
            .addOnSuccessListener { document ->
                val chats = document.get("chats") as List<HashMap<String, Any>>

                val chatsList = mutableListOf<ChatModel>()

                chats.forEach {
                    val createdAt = it["created_at"] as Timestamp
                    val message = it["message"] as String
                    val userId = it["user_id"] as String
                    val userName = it["user_name"] as String

                    chatsList.add(ChatModel(createdAt, message, userId, userName))
                }

                onComplete(chatsList)
            }
    }

    fun createChatRoom(boardId: String, myUid: String, theOtherUid: String, onCreateComplete: (Boolean) -> Unit) {
        chatCollection
            .add(
                hashMapOf(
                    "board_id" to boardId,
                    "chats" to emptyList<HashMap<String, Any>>(),
                    "participants" to mutableListOf(myUid, theOtherUid),
                )
            )
            .addOnSuccessListener {
                onCreateComplete(true)
            }
            .addOnFailureListener {
                onCreateComplete(false)
            }
    }

    fun sendChat(chatRoomUuid: String, uid: String, userName: String, message: String, onSendComplete: (Boolean) -> Unit) {
        val newChatItem = mapOf(
            "created_at" to Timestamp.now(),
            "message" to message,
            "user_id" to uid,
            "user_name" to userName,
        )

        chatCollection.document(chatRoomUuid)
            .update("chats", FieldValue.arrayUnion(newChatItem))
            .addOnSuccessListener {
                onSendComplete(true)
            }
            .addOnFailureListener {
                onSendComplete(false)
            }
    }
}
