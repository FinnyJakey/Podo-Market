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
            //로그인 Changes Commit Suggestion
            if (isLoginInputValid(loginEmail, loginPassword)) {
                doLogin(loginEmail, loginPassword) //검증성공시 로그인실행
            }
            else{
                Toast.makeText(this, "이메일, 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<TextView>(R.id.sign_up_button)?.setOnClickListener { moveAccountActivity() }
    }

    private fun doLogin(loginEmail: String, loginPassword: String){
        val authViewModel = AuthViewModel()
        authViewModel.signIn(loginEmail, loginPassword) { isSuccess ->
            if (isSuccess)moveMainActivity()
            else Toast.makeText(this, "등록된 회원 정보가 존재하지 않습니다", Toast.LENGTH_SHORT).show()
        }
    }

    //로그인 검증
    private fun isLoginInputValid(email: String, password: String): Boolean {
        val loginUiState = LoginUiState(email, password)
        if (loginUiState.showEmailError) {
            Toast.makeText(this, "이메일 형식이 올바르지 않습니다.", Toast.LENGTH_SHORT).show()
        }
        else if(loginUiState.showPasswordError) {
            Toast.makeText(this, "비밀번호 형식이 올바르지 않습니다.", Toast.LENGTH_SHORT).show()
        }
        return loginUiState.isInputValid
    }
    private fun moveAccountActivity(){
        startActivity(
            Intent(this, SignUpActivity::class.java) // SignUpActivity
        )
    }

    private fun moveMainActivity(){
        startActivity( //로그인 성공시 -> MainActivity
            Intent(this, MainActivity::class.java)
        )
        finish()
    }
}
