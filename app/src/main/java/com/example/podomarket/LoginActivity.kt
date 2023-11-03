package com.example.podomarket

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.podomarket.viewmodel.AuthViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        findViewById<Button>(R.id.login)?.setOnClickListener {
            val loginEmail = findViewById<EditText>(R.id.username)?.text.toString()
            val loginPassword = findViewById<EditText>(R.id.password)?.text.toString()
            //로그인
            doLogin(loginEmail, loginPassword)
        }

        findViewById<Button>(R.id.move_account_activity)?.setOnClickListener { moveAccountActivity() }
    }

    private fun doLogin(loginEmail: String, loginPassword: String){
        val authViewModel = AuthViewModel()
        authViewModel.signIn(loginEmail, loginPassword) { isSuccess ->
            if (isSuccess)moveMainActivity()
            else Toast.makeText(this, "로그인 정보가 옳지 않습니다", Toast.LENGTH_SHORT).show()
        }
    }

    private fun moveAccountActivity(){
        startActivity(
            Intent(this, AccountCreateActivity::class.java)
        )
    }

    private fun moveMainActivity(){
        startActivity( //로그인 성공시 -> MainActivity
            Intent(this, MainActivity::class.java)
        )
        finish()
    }
}
