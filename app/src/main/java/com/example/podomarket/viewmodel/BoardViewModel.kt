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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File

class BoardViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val boardCollection = db.collection("Board")
    private val storageRef = FirebaseStorage.getInstance().reference
    private val authViewModel = AuthViewModel()

    private val boardsLiveData = MutableLiveData<List<BoardModel>>()

    fun getBoardsLiveData(): LiveData<List<BoardModel>> {
        return boardsLiveData
    }

    fun getAllBoards(soldFiltered: Boolean = false) {
        boardCollection.orderBy("created_at", Query.Direction.DESCENDING).addSnapshotListener { snapshot, error ->
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
                val deleted = document.get("deleted") as Boolean

                val board: BoardModel = BoardModel(uuid, content, createdAt, pictures, price, sold, title, userId, userName)

                if (deleted) {
                    return@forEach
                }

                if (soldFiltered && board.sold) {
                    return@forEach
                }
                boards.add(board)
            }
            boardsLiveData.value = boards
        }
    }

    fun addBoard(content: String, createdAt: Timestamp, pictures: List<File>, price: Number, title: String, onAddComplete: (Boolean) -> Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            val picturesList = mutableListOf<String>()

            for (picture in pictures) {
                val file = Uri.fromFile(picture)
                val pictureRef = storageRef.child("${authViewModel.getCurrentUserUid()}/${Timestamp.now().nanoseconds}${file.lastPathSegment}")
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
                        picturesList.add(downloadUri.toString())
                    }
                }.await()
            }

            authViewModel.getCurrentUser { _, name, _ ->
                boardCollection
                    .add(
                        hashMapOf(
                            "content" to content,
                            "created_at" to createdAt,
                            "deleted" to false,
                            "pictures" to picturesList,
                            "price" to price,
                            "sold" to false,
                            "title" to title,
                            "user_id" to authViewModel.getCurrentUserUid(),
                            "user_name" to name,
                        )
                    )
                    .addOnSuccessListener {
                        onAddComplete(true)
                    }
                    .addOnFailureListener {
                        onAddComplete(false)
                    }
            }
        }

    }

    fun deleteBoard(boardId: String, onDeleteComplete: (Boolean) -> Unit) {
        boardCollection.document(boardId)
            .update("deleted", true)
            .addOnSuccessListener {
                onDeleteComplete(true)
            }
            .addOnFailureListener {
                onDeleteComplete(false)
            }
    }

    fun updateBoard(board: BoardModel, newPictures: List<File> = emptyList(), onUpdateComplete: (Boolean) -> Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            val newPicturesList = mutableListOf<String>()

            for (picture in newPictures) {
                val file = Uri.fromFile(picture)
                val pictureRef = storageRef.child("${authViewModel.getCurrentUserUid()}/${Timestamp.now().nanoseconds}${file.lastPathSegment}")
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
                        newPicturesList.add(downloadUri.toString())
                    }
                }.await()
            }

            boardCollection.document(board.uuid)
                .update(
                    hashMapOf(
                        "content" to board.content,
                        "created_at" to board.createdAt,
                        "deleted" to false,
                        "pictures" to board.pictures + newPicturesList,
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

    }

    fun soldUpdate(boardId: String, sold: Boolean, onSoldUpdateComplete: (Boolean) -> Unit) {
        boardCollection.document(boardId)
            .update("sold", sold)
            .addOnSuccessListener {
                onSoldUpdateComplete(true)
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