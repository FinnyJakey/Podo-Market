package com.example.podomarket

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.podomarket.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val authViewModel = AuthViewModel()

        //로그인 한 상태로 실행되도록 하는 코드(바로 MainActivity갈경우만 필요) - 추후지워야함
        doTestLogin()

        if(authViewModel.getCurrentUserUid() == null){
            //로그인 안되어있을시 LoginActivity로 시작
            moveLoginActivity()
        }

        findViewById<TextView>(R.id.textUID)?.text = authViewModel.getCurrentUserUid()?: "No User"

        findViewById<Button>(R.id.button_signout)?.setOnClickListener {
            //signout 버튼 클릭시 로그아웃+LoginActivity로 이동
            authViewModel.signOut()
            moveLoginActivity()
        }
    }

    //바로 로그인 원할시 사용
    private fun doTestLogin(){
        var loginId = "jisu@naver.com"
        var loginPwd = "@Abcde12"
        val authViewModel = AuthViewModel()
        authViewModel.signIn(loginId, loginPwd) { isSuccess ->
            if (isSuccess)
            else Toast.makeText(this, "로그인 정보가 옳지 않습니다", Toast.LENGTH_SHORT).show()
        }
    }
    private fun moveLoginActivity(){
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
