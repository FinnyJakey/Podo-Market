package com.example.podomarket

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.podomarket.login.LoginActivity
import com.example.podomarket.product.ProductListFragment
import com.example.podomarket.viewmodel.AuthViewModel

class MainActivity : AppCompatActivity() {
    private val authViewModel = AuthViewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_market)

        //로그인 한 상태로 실행되도록 하는 코드(바로 MainActivity갈경우만 필요) - 추후지워야함
        //doTestLogin()

        if(authViewModel.getCurrentUserUid() == null){
            //로그인 안되어있을시 LoginActivity로 시작
            moveLoginActivity()
        }
/*        findViewById<Button>(R.id.button_signout)?.setOnClickListener {
            //signout 버튼 클릭시 로그아웃+LoginActivity로 이동
            authViewModel.signOut()
            moveLoginActivity()
        }*/
        val productListFragment = ProductListFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, productListFragment)
            .commit()
    }

    //바로 로그인 원할시 사용
    private fun doTestLogin(){
        var loginId = "android@hansung.ac.kr"
        var loginPwd = "hansungandroid"
        authViewModel.signIn(loginId, loginPwd) { isSuccess ->
            if (isSuccess)
            else Toast.makeText(this, "로그인 정보가 옳지 않습니다", Toast.LENGTH_SHORT).show()
        }
    }

    fun Logout() {
        authViewModel.signOut()
        moveLoginActivity()
    }

    private fun moveLoginActivity(){
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
