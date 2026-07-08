package com.bussiness.curemegptapp.apimodel.familyProfile

import com.google.gson.annotations.SerializedName

data class FamilyMemberResponse(

    @SerializedName("total_family_members")
    val totalFamilyMembers: Int,

    @SerializedName("total_family_medication_count")
    val totalFamilyMedicationCount: Int,

    @SerializedName("total_family_appointment_count")
    val totalFamilyAppointmentCount: Int,

    @SerializedName("familyMembers")
    val familyMembers: List<FamilyMember>
)

data class FamilyMember(
    val id: Int,
    @SerializedName("user_id")
    val userId: Int,
    val relationship: String?,
    @SerializedName("full_name")
    val fullName: String?,
    @SerializedName("contact_number")
    val contactNumber: String?,
    val email: String?,
    val dob: String?,
    val gender: String?,
    val height: String?,
    val weight: String?,

    @SerializedName(value = "profile_image", alternate = ["profile_photo"])
    val profileImage: String?,

    @SerializedName("blood_group")
    val bloodGroup: String?,

    val allergies: String?,

    @SerializedName("emergency_contact_name")
    val emergencyContactName: String?,

    @SerializedName("emergency_contact_number")
    val emergencyContactNumber: String?,

    @SerializedName("chronic_condition")
    val chronicCondition: String?,

    @SerializedName("surgical_history")
    val surgicalHistory: String?,

    @SerializedName("current_medications")
    val currentMedications: String?,

    @SerializedName("current_supplements")
    val currentSupplements: String?,

    @SerializedName("medical_documents")
    val medicalDocuments: List<String>?,

    @SerializedName("created_at")
    val createdAt: String,

    @SerializedName("updated_at")
    val updatedAt: String,

    @SerializedName("deleted_at")
    val deletedAt: String?,

    @SerializedName("medication_count")
    val medicationCount: Int,

    @SerializedName("appointment_count")
    val appointmentCount: Int,

    @SerializedName(value = "completedAppointmentCount", alternate = ["completed_appointment_count"])
    val completedAppointmentCount: Int,

    @SerializedName(value = "completedMedicationCount", alternate = ["completed_medication_count"])
    val completedMedicationCount: Int = 0
)

data class FamilyMemberUi(
    val id: Int,
    val name: String,
    val age: Int?,
    val relationship: String,
    val appointments: String,
    val medications: String,
    val imageUrl: String?
)

data class FamilyUiState(
    val totalFamilyMembers: Int = 0,
    val totalFamilyMedicationCount: Int = 0,
    val totalFamilyAppointmentCount: Int = 0
)