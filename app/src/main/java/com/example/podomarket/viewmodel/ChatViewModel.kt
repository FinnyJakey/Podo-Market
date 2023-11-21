package com.example.podomarket.viewmodel

import androidx.lifecycle.ViewModel
import com.example.podomarket.model.ChatModel
import com.example.podomarket.model.ChatRoomModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ChatViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val authViewModel = AuthViewModel()
    private val userCollection = db.collection("User")
    val chatCollection = db.collection("Chat")

    fun getAllMyChatRooms(onComplete: (List<ChatRoomModel>) -> Unit) {
        authViewModel.getCurrentUser { _, _, chats_uiid ->
            val myChatRooms = mutableListOf<ChatRoomModel>()

            CoroutineScope(Dispatchers.Main).launch {
                chats_uiid.forEach { chat_uuid ->
                    chatCollection.document(chat_uuid)
                        .get()
                        .addOnSuccessListener { document ->
                            val chatRoomUuid = document.id
                            val boardId = document.get("board_id") as String
                            val chats = document.get("chats") as List<HashMap<String, Any>>
                            val participants = document.get("participants") as List<String>
                            val recentTime = document.get("recent_time") as Timestamp

                            val chatsList = mutableListOf<ChatModel>()

                            chats.forEach {
                                val createdAt = it["created_at"] as Timestamp
                                val message = it["message"] as String
                                val userId = it["user_id"] as String
                                val userName = it["user_name"] as String

                                chatsList.add(ChatModel(createdAt, message, userId, userName))
                            }

                            myChatRooms.add(ChatRoomModel(chatRoomUuid, boardId, chatsList, participants, recentTime))
                        }
                        .await()
                }

                myChatRooms.sortByDescending {
                    it.recentTime
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

    fun createChatRoom(boardId: String, myUid: String, theOtherUid: String, onCreateComplete: (ChatRoomModel) -> Unit) {
        getAllMyChatRooms { chatRoomModels ->
            chatRoomModels.forEach { chatRoomModel ->
                if (chatRoomModel.boardUuid == boardId && chatRoomModel.participants.contains(myUid) && chatRoomModel.participants.contains(theOtherUid)) {
                    onCreateComplete(chatRoomModel)
                    return@getAllMyChatRooms
                }
            }

            chatCollection
                .add(
                    hashMapOf(
                        "board_id" to boardId,
                        "chats" to emptyList<HashMap<String, Any>>(),
                        "participants" to mutableListOf(myUid, theOtherUid),
                        "recent_time" to Timestamp.now(),
                    )
                )
                .addOnSuccessListener {
                    it.get().addOnSuccessListener { chatDocument ->
                        CoroutineScope(Dispatchers.Main).launch {
                            userCollection.document(myUid)
                                .update("chats", FieldValue.arrayUnion(chatDocument.id))
                                .await()

                            userCollection.document(theOtherUid)
                                .update("chats", FieldValue.arrayUnion(chatDocument.id))
                                .await()

                            onCreateComplete(ChatRoomModel(chatDocument.id,boardId, emptyList<ChatModel>(), listOf(myUid, theOtherUid), Timestamp.now()))
                        }
                    }

                }
        }
    }

    fun sendChat(chatRoomUuid: String, message: String, onSendComplete: (Boolean) -> Unit) {
        authViewModel.getCurrentUser { _, name, _ ->
            val newChatItem = mapOf(
                "created_at" to Timestamp.now(),
                "message" to message,
                "user_id" to authViewModel.getCurrentUserUid(),
                "user_name" to name,
            )

            chatCollection.document(chatRoomUuid)
                .update("chats", FieldValue.arrayUnion(newChatItem))
                .addOnSuccessListener {
                    chatCollection.document(chatRoomUuid)
                        .update("recent_time", Timestamp.now())
                        .addOnSuccessListener {
                            onSendComplete(true)
                        }
                        .addOnFailureListener {
                            onSendComplete(false)
                        }
                }
                .addOnFailureListener {
                    onSendComplete(false)
                }
        }
    }
}
