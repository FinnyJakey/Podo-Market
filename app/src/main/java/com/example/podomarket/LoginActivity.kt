package com.example.podomarket

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.podomarket.viewmodel.AuthViewModel

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        findViewById<Button>(R.id.login_button)?.setOnClickListener {
            val loginEmail = findViewById<EditText>(R.id.login_email)?.text.toString()
            val loginPassword = findViewById<EditText>(R.id.login_password)?.text.toString()
            //로그인
            if (isLoginInputValid(loginEmail, loginPassword)) {
                doLogin(loginEmail, loginPassword)
            } else {
                Toast.makeText(this, "유효한 이메일과 6자 이상의 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<TextView>(R.id.sign_up_button)?.setOnClickListener { moveAccountActivity() }
    }

    private fun doLogin(loginEmail: String, loginPassword: String){
        val authViewModel = AuthViewModel()
        authViewModel.signIn(loginEmail, loginPassword) { isSuccess ->
            if (isSuccess)moveMainActivity()
            else Toast.makeText(this, "로그인 정보가 옳지 않습니다", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isLoginInputValid(email: String, password: String): Boolean {
        return LoginUiState(email, password).isInputValid
    }
    private fun moveAccountActivity(){
        startActivity(
            Intent(this, SignUpActivity::class.java)
        )
    }

    private fun moveMainActivity(){
        startActivity( //로그인 성공시 -> MainActivity
            Intent(this, MainActivity::class.java)
        )
        finish()
    }
}
