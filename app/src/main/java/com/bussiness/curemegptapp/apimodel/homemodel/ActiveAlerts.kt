package com.bussiness.curemegptapp.apimodel.homemodel

data class ActiveAlerts(
    val appointments: List<AppointmentAlert>?,
    val medications: List<MedicationAlert>?
)

data class AppointmentAlert(
    val id: Int?,
    val user_id: Int?,
    val family_member_id: Int?,
    val appointment_type_id: Int?,
    val recommended_chat_id: Int?,
    val description: String?,
    val date: String?,
    val time: String?,
    val preferred_doctor: String?,
    val preferred_clinic: String?,
    val appointment_reminder: String?,
    val appointment_for_whom: String?,
    val complete_status: Int?,
    val created_at: String?,
    val updated_at: String?
)

data class MedicationAlert(
    val id: Int?,
    val user_id: Int?,
    val family_member_id: Int?,
    val medication_type: String?,
    val medication_name: String?,
    val dosage: String?,
    val frequency: String?,
    val days: String?,
    val reminder_time: String?,
    val start_date: String?,
    val end_date: String?,
    val prescription_docs: String?,
    val notes: String?,
    val medication_for_whom: String?,
    val reminder_status: Int?,
    val created_at: String?,
    val updated_at: String?
)