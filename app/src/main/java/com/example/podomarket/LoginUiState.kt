package com.example.podomarket

import java.util.regex.Matcher
import java.util.regex.Pattern

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val successToSignIn: Boolean = false,
    val userMessage: String? = null
) {
    val isInputValid: Boolean
        get() = isEmailValid && isPasswordValid

    private val isEmailValid: Boolean
        get() {
            return if (email.isEmpty()) {
                false
            } else {
                android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
            }
        }


    val isPasswordValid: Boolean
        get() {
            val pwPattern = "^(?=.*\\d)(?=.*[~`!@#$%\\^&*()-])(?=.*[a-z])(?=.*[A-Z]).{8,13}$" //1개 이상의 숫자, 특수문자, 소문자, 대문자, 총길이 8-13자리여야함
            val matcher: Matcher = Pattern.compile(pwPattern).matcher(password)

            val pwPattern2 = "(.)\\1\\1" //연속된 숫자,문자 3개 불가능(ex. 111, aaa)
            val matcher2: Matcher = Pattern.compile(pwPattern2).matcher(password)

            return matcher.matches() && !matcher2.find()
        }

    val showEmailError: Boolean
        get() = email.isNotEmpty() && !isEmailValid

    val showPasswordError: Boolean
        get() = password.isNotEmpty() && !isPasswordValid
}