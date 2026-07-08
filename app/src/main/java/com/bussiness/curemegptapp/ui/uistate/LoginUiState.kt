package com.bussiness.curemegptapp.ui.uistate

data class LoginUiState(
    val emailOrPhone: String = "",
    val password: String = "",
    val errorMessage: String? = null,
    val loginSuccess: Boolean = false
)