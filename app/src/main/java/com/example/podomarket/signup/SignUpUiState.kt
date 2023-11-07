package com.example.podomarket.signup

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
        get() = password.length >= 6

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
