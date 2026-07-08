package com.bussiness.curemegptapp.apimodel.profilemodel

import com.bussiness.curemegptapp.repository.BaseResponse

data class UserProfileResponse(

    val code: Int,

    val data: Data, override val success: Boolean?, override val message: String?
) : BaseResponse

data class Data(
    val user: UserProfile
) {

    data class UserProfile(
        val id: Int? = null,
        val name: String? = null,
        val email: String? = null,
        val phone: String? = null,
        val email_verified_at: String? = null,
        val device_type: String? = null,
        val fcm_token: String? = null,
        val profile_image: String? = null,
        val otp: String? = null,
        val dob: String? = null,
        val gender: String? = null,
        val height: String? = null,
        val weight: String? = null,
        val blood_group: String? = null,
        val allergies: String? = null,
        val emergency_contact_name: String? = null,
        val emergency_contact_number: String? = null,
        val chronic_condition: String? = null,
        val surgical_history: String? = null,
        val current_medications: String? = null,
        val current_supplements: String? = null,
        val medical_documents: List<MedicalDocument>? = null,
        val account_verification_status: Int? = null,
        val user_status: Int? = null,
        val last_login_at: String? = null,
        val created_at: String? = null,
        val updated_at: String? = null,
        val deleted_at: String? = null
    )

    data class MedicalDocument(
        val path: String?,
        val name: String?
    )
}
