package com.example.podomarket.product

import com.example.podomarket.viewmodel.AuthViewModel
import java.io.File

class ProductAddUiState(
    val content: String,
    val pictures: List<File>,
    val price: Number,
    val title: String,
) {
    private val authViewModel = AuthViewModel()
    // 각 요소 중 하나라도 빈 값이 있는지 확인하는 함수
    val isInputValid: Boolean
        get() = isContentValid() && isPicturesValid() && isPriceValid() && isTitleValid() && isUserIdValid()


    private fun isContentValid(): Boolean {
        return content.isNotEmpty()
    }

    private fun isPicturesValid(): Boolean {
        return pictures.isNotEmpty()
    }

    private fun isPriceValid(): Boolean {
        return price.toString().isNotBlank()
    }

    private fun isTitleValid(): Boolean {
        return title.isNotEmpty()
    }

    private fun isUserIdValid(): Boolean {
            return authViewModel.getCurrentUserUid()?.isNotEmpty() ?: false
    }
}