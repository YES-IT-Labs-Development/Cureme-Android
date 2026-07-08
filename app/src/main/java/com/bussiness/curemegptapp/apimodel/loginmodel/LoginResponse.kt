package com.bussiness.curemegptapp.apimodel.loginmodel

import com.bussiness.curemegptapp.repository.BaseResponse

data class LoginResponse(
    override  val success: Boolean? = null,
    val code: Int? = null,
    override val message: String? = null,
    val data: LoginData? = null,
) : BaseResponse

data class LoginData(
    val token: String? = null,
    val otp: String? = null,
    val user:UserModel?=null
)


data class UserModel(
    val id: Int? = null,
    val name: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val email_verified_at: String? = null,
    val device_type: String? = null,
    val fcm_token: String? = null,
    val otp: String? = null,
    val gender: String? = null,
    val height: String? = null,
    val weight: String? = null,
    val profile_image: String? = null,
    val account_verification_status: Int? = null,
    val user_status: Int? = null
)



