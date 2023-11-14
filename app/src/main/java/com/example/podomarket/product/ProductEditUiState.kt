package com.example.podomarket.product

import com.example.podomarket.viewmodel.AuthViewModel
import java.io.File

class ProductEditUiState ( val content: String, val pictures: List<String>, val title: String) {
        private val authViewModel = AuthViewModel()

        // 각 요소 중 하나라도 빈 값이 있는지 확인하는 함수
        val isInputValid: Boolean
        get() = isContentValid() && isPicturesValid() && isTitleValid()

        fun isContentValid(): Boolean {
            return content.isNotEmpty()
        }

        fun isPicturesValid(): Boolean {
            return pictures.isNotEmpty()
        }

        fun isTitleValid(): Boolean {
            return title.isNotEmpty()
        }

    }