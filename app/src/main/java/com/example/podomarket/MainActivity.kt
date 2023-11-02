package com.example.podomarket

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Firebase.auth.currentUser == null) {
            //로그인 안되어있을시 LoginActivity로 시작
            startActivity(
                Intent(this, LoginActivity::class.java))
            finish()
        }

        findViewById<TextView>(R.id.textUID)?.text = Firebase.auth.currentUser?.uid ?: "No User"

        findViewById<Button>(R.id.button_signout)?.setOnClickListener {
            //signout 버튼 클릭시 로그아웃+LoginActivity로 이동
            Firebase.auth.signOut()
            startActivity(
                Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
