package com.example.podomarket.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.podomarket.model.BoardModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.io.File

class BoardViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val boardCollection = db.collection("Board")
    private val storageRef = FirebaseStorage.getInstance().reference

    private val boardsLiveData = MutableLiveData<List<BoardModel>>()

    fun getBoardsLiveData(): LiveData<List<BoardModel>> {
        return boardsLiveData
    }

    fun getAllBoards(soldFiltered: Boolean = false) {
        // TODO: CHECK DESCENDING OR ASCENDING
        boardCollection.orderBy("created_at", Query.Direction.ASCENDING).addSnapshotListener { snapshot, error ->
            if (error != null) {
                return@addSnapshotListener
            }

            val boards = mutableListOf<BoardModel>()
            snapshot?.forEach { document ->
                val uuid = document.id
                val content = document.get("content").toString()
                val createdAt = document.get("created_at") as Timestamp
                val pictures = document.get("pictures") as List<String>
                val price = document.get("price") as Number
                val sold = document.get("sold") as Boolean
                val title = document.get("title").toString()
                val userId = document.get("user_id").toString()
                val userName = document.get("user_name").toString()

                val board: BoardModel = BoardModel(uuid, content, createdAt, pictures, price, sold, title, userId, userName)

                if (soldFiltered && board.sold) {
                    return@forEach
                }
                boards.add(board)
            }
            boardsLiveData.value = boards
        }
    }

    suspend fun addBoard(content: String, createdAt: Timestamp, pictures: List<File>, price: Number, sold: Boolean, title: String, userId: String, userName: String, onAddComplete: (Boolean) -> Unit) {
        val picturesMutableList = mutableListOf<String>()

        for (picture in pictures) {
            val file = Uri.fromFile(picture)
            val pictureRef = storageRef.child("$userId/${Timestamp.now().nanoseconds}${file.lastPathSegment}}")
            val uploadTask = pictureRef.putFile(file)
            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                pictureRef.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    picturesMutableList.add(downloadUri.toString())
                }
            }.await()
        }

        val picturesList: List<String> = picturesMutableList

        boardCollection
            .add(
                hashMapOf(
                    "content" to content,
                    "created_at" to createdAt,
                    "pictures" to picturesList,
                    "price" to price,
                    "sold" to sold,
                    "title" to title,
                    "user_id" to userId,
                    "user_name" to userName,
                )
            )
            .addOnSuccessListener {
                onAddComplete(true)
            }
            .addOnFailureListener {
                onAddComplete(false)
            }
    }

    fun updateBoard(boardId: String, board: BoardModel, onUpdateComplete: (Boolean) -> Unit) {
        boardCollection.document(boardId)
            .set(
                hashMapOf(
                    "content" to board.content,
                    "created_at" to board.createdAt,
                    "pictures" to board.pictures,
                    "price" to board.price,
                    "sold" to board.sold,
                    "title" to board.title,
                    "user_id" to board.userId,
                    "user_name" to board.userName,
                )
            )
            .addOnSuccessListener {
                onUpdateComplete(true)
            }
            .addOnFailureListener {
                onUpdateComplete(false)
            }
    }

    fun soldUpdate(boardId: String, sold: Boolean, onSoldUpdateComplete: (Boolean) -> Unit) {
        boardCollection.document(boardId).get()
            .addOnSuccessListener {
                boardCollection.document(boardId)
                    .set(
                        hashMapOf(
                            "content" to it.data?.get("content"),
                            "created_at" to it.data?.get("created_at"),
                            "pictures" to it.data?.get("pictures"),
                            "price" to it.data?.get("price"),
                            "sold" to sold,
                            "title" to it.data?.get("title"),
                            "user_id" to it.data?.get("user_id"),
                            "user_name" to it.data?.get("user_name"),
                        )
                    )
                    .addOnSuccessListener {
                        onSoldUpdateComplete(true)
                    }
                    .addOnFailureListener {
                        onSoldUpdateComplete(false)
                    }
            }
            .addOnFailureListener {
                onSoldUpdateComplete(false)
            }
    }

    fun getBoard(uuid: String, onGetComplete: (BoardModel) -> Unit) {
        boardCollection.document(uuid)
            .get()
            .addOnSuccessListener { document ->
                val boardUuid = document.id
                val content = document.get("content").toString()
                val createdAt = document.get("created_at") as Timestamp
                val pictures = document.get("pictures") as List<String>
                val price = document.get("price") as Number
                val sold = document.get("sold") as Boolean
                val title = document.get("title").toString()
                val userId = document.get("user_id").toString()
                val userName = document.get("user_name").toString()

                onGetComplete(
                    BoardModel(boardUuid, content, createdAt, pictures, price, sold, title, userId, userName)
                )
            }
    }
}