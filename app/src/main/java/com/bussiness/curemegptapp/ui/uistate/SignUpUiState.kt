package com.bussiness.curemegptapp.ui.uistate

data class SignUpUiState(
    val name: String = "",
    val emailOrPhone: String = "",
    val password: String = "",
    val cnfPassword: String = "",
    val otp: String = "",
    val errorMessage: String? = null,
    val loginSuccess: Boolean = false
)