package com.bussiness.curemegptapp.data.model

import android.net.Uri

data class HomeProfileData(   val fullName: String = "",
                              val contactNumber: String = "",
                              val email: String = "",
                              val dateOfBirth: String = "",
                              val gender: String = "",
                              val height: String = "",
                              val weight: String = "",
                              val profilePhotoUri: Uri? = null,
                              val bloodGroup: String = "",
                              val allergies: List<String> = emptyList(),
                              val emergencyContactName: String = "",
                              val emergencyContactPhone: String = "",
                              val chronicConditions: List<String> = emptyList(),
                              val surgicalHistory: String = "",
                              val currentMedications: List<String> = emptyList(),
                              val currentSupplements: List<String> = emptyList(),
                              val uploadedFiles: List<Uri> = emptyList()
)

// HomeScreen ke liye HealthData model
data class HealthData(
    val mood: String = "",
    val profileCompletion: Float = 0f,
    val medications: List<String> = emptyList(),
    val allergies: List<String> = emptyList(),
    val recommendedSteps: List<String> = emptyList(),
    val activeAlerts: List<String> = emptyList(),
    val lastCheckup: String = "",
    val age: Int = 0
)
