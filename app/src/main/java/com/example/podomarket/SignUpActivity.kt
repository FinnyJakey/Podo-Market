package com.example.podomarket

import android.app.DatePickerDialog
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
    //생년월일 관련 변수
    private var birthYear = 0
    private var birthMonth = 0
    private var birthDay = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        //뒤로가기 버튼 구현(Manifest에 부모 액티비티를 LoginActivity로 설정)
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        //editText클릭시 DatePicker표시
        findViewById<EditText>(R.id.create_account_birth)?.setOnClickListener {
            showBrithDatePicker()
        }

        findViewById<Button>(R.id.create_account_button)?.setOnClickListener {
            //아이디, 비번, 이름, 날짜 형식 정확한지 검증 -> 성공시 ViewModel로 인자값 전달
            val createEmail = findViewById<EditText>(R.id.create_account_email)?.text.toString()
            //아이디 검증: @이나 com이란 단어 없거나 null값이면
            if (createEmail == null || !createEmail.contains("@") || !createEmail.contains("com")) {
                // 이메일 형식이 올바르지 않음을 사용자에게 알림
                Toast.makeText(this, "이메일 형식이 올바르지 않습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val createName = findViewById<EditText>(R.id.create_account_name)?.text.toString()
            //이름 검증: Null값이면 이름입력하라는 알림메세지 창 생성
            if (createName == null) {
                // 이름을 입력하라는 메시지를 사용자에게 알림
                Toast.makeText(this, "이름을 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val createPassword = findViewById<EditText>(R.id.create_account_password)?.text.toString()
            //비번 검증: Null값이거나 7자리 이하거나 13차리 초과면 형식에 옳지못하다는 창 생성
            if (createPassword == null || createPassword.length <= 7 || createPassword.length > 13) {
                // 비밀번호 형식이 올바르지 않음을 사용자에게 알림
                Toast.makeText(this, "비밀번호 형식이 올바르지 않습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val repassword = findViewById<EditText>(R.id.create_account_repassword)?.text.toString()
            //비밀번호 재입력 검증: 같은지를 확인
            if (createPassword != repassword) {
                // 비밀번호가 일치하지 않음을 사용자에게 알림
                Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //생년월일 검증: 년, 월, 달이 0이아닌지 확인
            if(birthYear == 0||birthMonth == 0||birthDay == 0){
                Toast.makeText(this, "생년월일 형식이 올바르지 않습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //사용자 생년월일 정수 -> 타임스탬프형식으로 변경
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.YEAR, birthYear)
            calendar.set(Calendar.MONTH, birthMonth)
            calendar.set(Calendar.DAY_OF_MONTH, birthDay)
            val createBirthTimestamp = Timestamp(calendar.time)

            //1차 검증 후 -> ViewModel로 인자전달
            val authViewModel = AuthViewModel()
            authViewModel.signUp(createEmail, createPassword, createName, createBirthTimestamp,
                onSignUpComplete = { isSuccess ->
                    if (isSuccess) {
                        // 계정 생성이 성공했을 때의 처리
                        moveMainActivity()
                    } else {
                        // 계정 생성이 실패했을 때의 처리
                        Toast.makeText(this, "계정 생성에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            )
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

    private fun moveMainActivity(){
        startActivity( //로그인 성공시 -> MainActivity
            Intent(this, MainActivity::class.java)
        )
        finish()
    }
}
