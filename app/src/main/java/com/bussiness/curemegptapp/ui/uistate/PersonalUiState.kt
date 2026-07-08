package com.bussiness.curemegptapp.ui.uistate

data class PersonalUiState(
    val name: String = "",
    var email: String = "",
    var phone: String = "",
    val dob: String = "",
    val gender: String = "Male",
    val height: String = "",
    val heightType: String = "Cm",
    val weight: String = "",
    val weightType: String = "Kg",
    val imageProfile: String = "selected_file",
    val imageProfilePath: String = "",
    val errorMessage: String? = null,
    val loginSuccess: Boolean = false,
    var phoneCopy: String ="",
    var emailCopy :String = "",
    var phoneVerify :Boolean = false,
    var emailVerify :Boolean = false,
)