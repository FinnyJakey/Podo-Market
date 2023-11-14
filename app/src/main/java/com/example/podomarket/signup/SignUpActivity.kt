package com.example.podomarket.signup

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.podomarket.MainActivity
import com.example.podomarket.R
import com.example.podomarket.viewmodel.AuthViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

class SignUpActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        //뒤로가기 버튼 구현(Manifest에 부모 액티비티를 LoginActivity로 설정)
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        findViewById<EditText>(R.id.create_account_birth)?.setOnClickListener {
            // DatePicker를 표시
            showDatePickerDialog(it)
        }

        findViewById<Button>(R.id.create_account_button)?.setOnClickListener {
            //아이디, 비번, 이름, 날짜 형식 정확한지 검증 -> 성공시 ViewModel로 인자값 전달
            val createEmail = findViewById<EditText>(R.id.create_account_email)?.text.toString()
            val createName = findViewById<EditText>(R.id.create_account_name)?.text.toString()
            val createPassword = findViewById<EditText>(R.id.create_account_password)?.text.toString()
            val createRepassword = findViewById<EditText>(R.id.create_account_repassword)?.text.toString()
            val createBirth = findViewById<EditText>(R.id.create_account_birth)?.text.toString()
            if(isSignUpInputValid(createEmail, createPassword,createRepassword, createName, createBirth)){
                doSignUp(createEmail, createPassword, createName, makeTimeStamp(createBirth))
            }
        }
    }

    //생년월일 타입변환(String -> Timestamp) 메서드
    private fun makeTimeStamp(birth: String):Timestamp {
        val birthComponents = birth.split("/")
        val birthMonth = birthComponents[0].toIntOrNull() ?: 0
        val birthDay = birthComponents[1].toIntOrNull() ?: 0
        val birthYear = birthComponents[2].toIntOrNull() ?: 0

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, birthYear)
        calendar.set(Calendar.MONTH, birthMonth - 1)
        calendar.set(Calendar.DAY_OF_MONTH, birthDay)
        calendar.set(Calendar.HOUR, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)

        return Timestamp(calendar.time)
    }

    //계정 생성 검증 메서드
    private fun isSignUpInputValid(email: String, password: String, repassword: String, name:String,  birth: String):Boolean{
        val signUpUiState = SignUpUiState(email, password, repassword, name, birth)
        if (signUpUiState.isInputEmpty){
            displayLoginError(SignUpErrorMessages.MISSING_FIELDS)
        }
        else if (signUpUiState.showEmailError) {
            displayLoginError(SignUpErrorMessages.INVALID_EMAIL_FORMAT)
        } else if (signUpUiState.showPasswordError) {
            displayLoginError(SignUpErrorMessages.INVALID_PASSWORD_FORMAT)
        } else if (signUpUiState.showRepasswordError) {
            displayLoginError(SignUpErrorMessages.PASSWORD_MISMATCH)
        } else if (signUpUiState.showNameError) {
            displayLoginError(SignUpErrorMessages.INVALID_NAME_FORMAT)
        } else if (signUpUiState.showBirthError) {
            displayLoginError(SignUpErrorMessages.INVALID_BIRTH_FORMAT)
        }
        return signUpUiState.isInputValid
    }

    // DatePicker를 표시하는 메서드
   fun showDatePickerDialog(view: View) {
        val builder = MaterialDatePicker.Builder.datePicker()
        val picker = builder.build()
        picker.addOnPositiveButtonClickListener { selection ->
            val date = Date(selection)
            val format = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
            findViewById<EditText>(R.id.create_account_birth)?.setText(format.format(date))
        }
        picker.show(supportFragmentManager, picker.toString())
    }

    //계정 생성 메서드
    private fun doSignUp(email: String, password: String, name:String, birth: Timestamp){

        val authViewModel = AuthViewModel()
        authViewModel.signUp(email, password, name, birth) { isSuccess ->
            if (isSuccess)moveMainActivity()
            else displayLoginError(SignUpErrorMessages.SIGN_UP_FAILED)
        }
    }

    private fun moveMainActivity(){
        startActivity( //로그인 성공시 -> MainActivity
            Intent(this, MainActivity::class.java)
        )
        finish()
    }

    private fun displayLoginError(errorMessage: String){
        val errorTextview = findViewById<TextView>(R.id.sign_up_error_textview);
        errorTextview.text = errorMessage;
        errorTextview.visibility = View.VISIBLE;
    }
}
