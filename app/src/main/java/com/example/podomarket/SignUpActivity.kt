package com.example.podomarket

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.podomarket.viewmodel.AuthViewModel
import com.google.firebase.Timestamp
import java.util.*

class SignUpActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        //뒤로가기 버튼 구현(Manifest에 부모 액티비티를 LoginActivity로 설정)
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        findViewById<Button>(R.id.create_account_button)?.setOnClickListener {
            //아이디, 비번, 이름, 날짜 형식 정확한지 검증 -> 성공시 ViewModel로 인자값 전달
            val createEmail = findViewById<EditText>(R.id.create_account_email)?.text.toString()
            val createName = findViewById<EditText>(R.id.create_account_name)?.text.toString()
            val createPassword = findViewById<EditText>(R.id.create_account_password)?.text.toString()
            val createRepassword = findViewById<EditText>(R.id.create_account_repassword)?.text.toString()
            val createBirth = findViewById<EditText>(R.id.create_account_birth)?.text.toString()
            if(isSignUpInputValid(createEmail, createPassword,createRepassword, createName, createBirth)){
                //birth를 timeStamp형식으로 바꿔야함 아니면 위에서 바꾸면 됨
                doSignUp(createEmail, createPassword, createName, makeTimeStamp(createBirth))
            }
        }
    }

    //생년월일 타입변환(String -> Timestamp) 메서드
    private fun makeTimeStamp(birth: String):Timestamp {
        val birthComponents = birth.split(".")
        val birthYear = birthComponents[0].toIntOrNull() ?: 0
        val birthMonth = birthComponents[1].toIntOrNull() ?: 0
        val birthDay = birthComponents[2].toIntOrNull() ?: 0

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, birthYear)
        calendar.set(Calendar.MONTH, birthMonth - 1)
        calendar.set(Calendar.DAY_OF_MONTH, birthDay)
        calendar.set(Calendar.HOUR, 12)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)

        return Timestamp(calendar.time)
    }

    //계정 생성 검증 메서드
    private fun isSignUpInputValid(email: String, password: String, repassword: String, name:String,  birth: String):Boolean{
        val signUpUiState = SignUpUiState(email, password, repassword, name, birth)
        if (signUpUiState.showEmailError) {
            Toast.makeText(this, "이메일 형식이 올바르지 않습니다.", Toast.LENGTH_SHORT).show()
        } else if (signUpUiState.showPasswordError) {
            Toast.makeText(this, "비밀번호 형식이 올바르지 않습니다.", Toast.LENGTH_SHORT).show()
        } else if (signUpUiState.showRepasswordError) {
            Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
        } else if (signUpUiState.showNameError) {
            Toast.makeText(this, "이름을 형식에 맞게 입력해주세요.", Toast.LENGTH_SHORT).show()
        } else if (signUpUiState.showBirthError) {
            Toast.makeText(this, "생년월일 형식이 올바르지 않습니다.", Toast.LENGTH_SHORT).show()
        }
        else{
            Toast.makeText(this, "입력을 완료해주세요", Toast.LENGTH_SHORT).show()
        }
        return signUpUiState.isInputValid
    }

    //계정 생성 메서드
    private fun doSignUp(email: String, password: String, name:String, birth: Timestamp){

        val authViewModel = AuthViewModel()
        authViewModel.signUp(email, password, name, birth) { isSuccess ->
            if (isSuccess)moveMainActivity()
            else Toast.makeText(this, "사용자 계정 형식이 옳지 못합니다", Toast.LENGTH_SHORT).show()
        }
    }

    private fun moveMainActivity(){
        startActivity( //로그인 성공시 -> MainActivity
            Intent(this, MainActivity::class.java)
        )
        finish()
    }
}
