package com.example.podomarket

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class AccountCreateActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        //뒤로가기 버튼 구현(Manifest에 부모 액티비티를 LoginActivity로 설정)
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        findViewById<Button>(R.id.create_account_button)?.setOnClickListener {
            //아이디, 비번, 이름, 날짜 로직 정확한지 비교해야함 성공하면 계정생성 성공, 아닐시에는 재입력 요구창 뜨게하기
            val userCreateEmail = findViewById<EditText>(R.id.create_account_email)?.text.toString()
            val userCreatePassword = findViewById<EditText>(R.id.create_account_password)?.text.toString()
            //계정생성ㅅ
            createAccount(userCreateEmail, userCreatePassword)
        }


    }
    //메서드 생성
    private fun createAccount(userEmail: String, password: String) {
        Firebase.auth.createUserWithEmailAndPassword(userEmail, password)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) { //계정 생성 성공시 로그인 -> MainActivity
                    doLogin(userEmail, password)
                } else {
                    Log.w("LoginActivity", "createUserWithEmail", it.exception)
                    Toast.makeText(this, "create account failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }
    private fun doLogin(userEmail: String, password: String) {
        Firebase.auth.signInWithEmailAndPassword(userEmail, password)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    startActivity( //로그인 성공시 -> MainActivity
                        Intent(this, MainActivity::class.java)
                    )
                    finish()
                } else {
                    Log.w("LoginActivity", "signInWithEmail", it.exception)
                    Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
