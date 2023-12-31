package com.example.podomarket.login

data class LoginUiState(
    val email: String = "",
    val password: String = "",
) {
    val isInputValid: Boolean
        get() = isEmailValid && isPasswordValid

    val isInputEmpty: Boolean
        get() = email.isEmpty() || password.isEmpty()

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

    val showEmailError: Boolean
        get() = email.isNotEmpty() && !isEmailValid

    val showPasswordError: Boolean
        get() = password.isNotEmpty() && !isPasswordValid
}