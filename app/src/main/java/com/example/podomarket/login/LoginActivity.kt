package com.example.podomarket.login

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.podomarket.MainActivity
import com.example.podomarket.R
import com.example.podomarket.signup.SignUpActivity
import com.example.podomarket.viewmodel.AuthViewModel

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //로그인 버튼
        addLoginButton()

        //엔터 클릭 이벤트
        addLoginEnter()

        findViewById<TextView>(R.id.sign_up_button)?.setOnClickListener { moveAccountActivity() }
    }

    private fun addLoginButton(){
        findViewById<Button>(R.id.login_button)?.setOnClickListener {
            testLoginvalid()
        }
    }

    private fun addLoginEnter(){
        val loginPasswordEditText = findViewById<EditText>(R.id.login_password)
        loginPasswordEditText.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                //엔터키 누르면 작동
                testLoginvalid()
                return@setOnKeyListener true
            }
            false
        }
    }

    //함수화
    private fun testLoginvalid(){
        val loginEmail = findViewById<EditText>(R.id.login_email)?.text.toString()
        val loginPassword = findViewById<EditText>(R.id.login_password)?.text.toString()
        if (isLoginInputValid(loginEmail, loginPassword)) {
            doLogin(loginEmail, loginPassword) //검증성공시 로그인실행
        }
    }

    private fun doLogin(loginEmail: String, loginPassword: String){
        val authViewModel = AuthViewModel()
        authViewModel.signIn(loginEmail, loginPassword) { isSuccess ->
            if (isSuccess)moveMainActivity()
            else displayLoginError(LoginErrorMessages.NO_REGISTERED_USER) // 등록된 이메일이 없거나 비밀번호가 틀린 경우
        }
    }

    //로그인 검증
    private fun isLoginInputValid(email: String, password: String): Boolean {
        val loginUiState = LoginUiState(email, password)
        if(loginUiState.isInputEmpty){
            displayLoginError(LoginErrorMessages.EMPTY_EMAIL_PASSWORD)
        }
        else if (loginUiState.showEmailError) {
            // 이메일 형식이 잘못된 경우
            displayLoginError(LoginErrorMessages.INVALID_EMAIL_FORMAT)
        }
        else if(loginUiState.showPasswordError) {
            // 비밀번호 형식이 잘못된 경우
            displayLoginError(LoginErrorMessages.INVALID_PASSWORD_FORMAT)
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

    private fun displayLoginError(errorMessage: String){
        val errorTextview = findViewById<TextView>(R.id.login_error_textview);
        errorTextview.text = errorMessage;
        errorTextview.visibility = View.VISIBLE;
    }
}
