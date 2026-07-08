package com.bussiness.curemegptapp.apimodel.personalmodel

data class User(
    val account_verification_status: Int,
    val allergies: Any,
    val blood_group: Any,
    val chronic_condition: Any,
    val created_at: String,
    val current_medications: Any,
    val current_supplements: Any,
    val device_type: String,
    val dob: String?,
    val email: String?,
    val email_verified_at: Any,
    val emergency_contact_name: Any,
    val emergency_contact_number: Any,
    val fcm_token: String?,
    val gender: String?,
    val height: String?,
    val id: Int,
    val medical_documents: Any,
    val name: String?,
    val otp: String?,
    val phone: String?,
    val profile_image: String?,
    val surgical_history: Any,
    val updated_at: String,
    val user_status: Int,
    val weight: String?
)

