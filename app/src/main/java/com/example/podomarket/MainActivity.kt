package com.example.podomarket

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //파이어베이스 로그인 등록

        val textView = findViewById<TextView>(R.id.textView)
        Firebase.auth.signInWithEmailAndPassword("a@a.com", "123456")
            .addOnCompleteListener(this) { // it: Task<AuthResult!>
                if (it.isSuccessful) {
                    //textView레이아웃의 텍스트에 성공문구와 함께 현재 유저의 uid 출력
                    textView.text = "sign-in-success${Firebase.auth.currentUser?.uid}"
                } else {
                    textView.text = "sign-in-failed"
                }
            }
    }
}