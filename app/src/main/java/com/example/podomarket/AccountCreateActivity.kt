package com.example.podomarket

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.*


class AccountCreateActivity: AppCompatActivity() {
    //생년월일 관련 변수
    private var birthYear = 0
    private var birthMonth = 0
    private var birthDay = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        //뒤로가기 버튼 구현(Manifest에 부모 액티비티를 LoginActivity로 설정)
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        //editText클릭시 DatePicker표시
        findViewById<EditText>(R.id.create_account_birth)?.setOnClickListener {
            showBrithDatePicker()
        }

        findViewById<Button>(R.id.create_account_button)?.setOnClickListener {
            //아이디, 비번, 이름, 날짜 로직 정확한지 비교해야함 성공하면 계정생성 성공, 아닐시에는 재입력 요구창 뜨게하기
            val createEmail = findViewById<EditText>(R.id.create_account_email)?.text.toString()
            //아이디 검증: @이나 com이란 단어 없거나 null값이면

            val createName = findViewById<EditText>(R.id.create_account_name)?.text.toString()
            //이름 검증: Null값이면 이름입력하라는 알림메세지 창 생성

            val createPassword = findViewById<EditText>(R.id.create_account_password)?.text.toString()
            //비번 검증: Null값이거나 7자리 이하거나 13차리 초과면 형식에 옳지못하다는 창 생성

            val repassword = findViewById<EditText>(R.id.create_account_repassword)?.text.toString()
            //createPassword랑 repassword랑 같은지 확인, 아니면 같지않다는 알림메세지 창 생성


            //1차 검증 후 -> 계정생성 ->ViewModel로 인자값 전달(null값이나 비번 몇자리)

        }
    }

    //메서드 생성
    // DatePicker를 표시하는 함수
    private fun showBrithDatePicker() {
        val calendar = Calendar.getInstance()
        birthYear  = calendar.get(Calendar.YEAR)
        birthMonth = calendar.get(Calendar.MONTH)
        birthDay = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                findViewById<EditText>(R.id.create_account_birth)?.setText("$birthYear-${birthMonth + 1}-$birthDay")
            },
            birthYear,
            birthMonth,
            birthDay
        )
        datePickerDialog.show()
    }
    //계정 생성
    private fun createAccount(createEmail: String, createPassword: String) {
        Firebase.auth.createUserWithEmailAndPassword(createEmail, createPassword)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) { //계정 생성 성공시 -> 로그인 -> 로그인 성공시 MainActivity

                    //로그인
                    doLogin(createEmail, createPassword)
                } else {
                    Log.w("LoginActivity", "createUserWithEmail", it.exception)
                    Toast.makeText(this, "create account failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }
    //로그인
    private fun doLogin(loginEmail: String, loginPassword: String) {
        Firebase.auth.signInWithEmailAndPassword(loginEmail, loginPassword)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    startActivity(
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
