package com.example.podomarket

import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

class SignUpUiState(
    val email: String = "",
    val password: String = "",
    val repassword: String = "",
    val name: String = "",
    val birth: String = ""
) {
    val isInputValid: Boolean
        get() = isEmailValid && isPasswordValid && isRepasswordValid && isNameValid && isBirthValid

    private val isEmailValid: Boolean
        get() {
            return if (email.isEmpty()) {
                false
            } else {
                android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
            }
        }

    private val isPasswordValid: Boolean
        get() {
            val pwPattern = "^(?=.*\\d)(?=.*[~`!@#$%\\^&*()-])(?=.*[a-z])(?=.*[A-Z]).{8,13}$" //1개 이상의 숫자, 특수문자, 소문자, 대문자, 총길이 8-13자리여야함
            val matcher: Matcher = Pattern.compile(pwPattern).matcher(password)

            val pwPattern2 = "(.)\\1\\1" //연속된 숫자,문자 3개 불가능(ex. 111, aaa)
            val matcher2: Matcher = Pattern.compile(pwPattern2).matcher(password)

            return matcher.matches() && !matcher2.find()
        }

    private val isRepasswordValid: Boolean
        get() = password == repassword

    private val isNameValid: Boolean
        get() = name.length in 2..12 && !name.any { it.isDigit() }

    private val isBirthValid: Boolean
        get() {
            val birthComponents = birth.split("/")
            if (birthComponents.size != 3 || birthComponents[0].length != 2 || birthComponents[1].length != 2 || birthComponents[2].length != 4) {
                return false
            }
            val birthMonth= birthComponents[0].toIntOrNull() ?: 0
            val birthDay = birthComponents[1].toIntOrNull() ?: 0
            val birthYear= birthComponents[2].toIntOrNull() ?: 0

            val currentCalendar = Calendar.getInstance()
            val currentTimestamp = currentCalendar.timeInMillis

            val userCalendar = Calendar.getInstance()
            userCalendar.set(birthYear, birthMonth - 1, birthDay, 0, 0, 0)
            val userTimestamp = userCalendar.timeInMillis

            return userTimestamp < currentTimestamp
        }

    val showEmailError: Boolean
        get() = email.isNotEmpty() && !isEmailValid

    val showPasswordError: Boolean
        get() = password.isNotEmpty() && !isPasswordValid

    val showRepasswordError: Boolean
        get() = repassword.isNotEmpty() && !isRepasswordValid

    val showNameError: Boolean
        get() = name.isNotEmpty() && (!isNameValid || name.any { it.isDigit() })

    val showBirthError: Boolean
        get() = birth.isNotEmpty() && !isBirthValid
}
