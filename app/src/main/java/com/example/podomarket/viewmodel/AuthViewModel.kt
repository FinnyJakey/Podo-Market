package com.example.podomarket.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    fun getCurrentUserUid(): String? {
        return auth.currentUser?.uid
    }

    fun getCurrentUser(onGetCurrentUserComplete: (email: String, name: String) -> Unit) {
        db.collection("User")
            .document(auth.currentUser?.uid.toString())
            .get()
            .addOnSuccessListener {
                onGetCurrentUserComplete(it.data?.get("email").toString(), it.data?.get("name").toString())
            }
    }

    fun getUser(uid: String, onGetUserComplete: (email: String, name: String) -> Unit) {
        db.collection("User")
            .document(uid)
            .get()
            .addOnSuccessListener {
                onGetUserComplete(it.data?.get("email").toString(), it.data?.get("name").toString())
            }
    }

    fun signIn(email: String, password: String, onSignInComplete: (Boolean) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                onSignInComplete(task.isSuccessful)
            }
    }

    fun signOut() {
        auth.signOut()
    }

    fun signUp(email: String, password: String, name: String, birth: Timestamp, onSignUpComplete: (Boolean) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                db.collection("User")
                    .document("${it.user?.uid}")
                    .set(
                        hashMapOf(
                            "birth" to birth,
                            "name" to name,
                            "email" to email,
                        )
                    )
                    .addOnSuccessListener {
                        onSignUpComplete(true)
                    }
                    .addOnFailureListener {
                        onSignUpComplete(false)
                    }
            }
            .addOnFailureListener {
                onSignUpComplete(false)
            }
    }
}