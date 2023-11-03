package com.example.podomarket

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.example.podomarket.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val authViewModel = AuthViewModel()

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

    private fun moveLoginActivity(){
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
