package com.bussiness.curemegptapp.apimodel.medication

import com.bussiness.curemegptapp.R
import com.bussiness.curemegptapp.apimodel.getAppointmentList.FamilyMember
import com.bussiness.curemegptapp.apimodel.getAppointmentList.User

data class Medication(
    val id: Int,
    val icon: Int = R.drawable.ic_medication_icon,
    val title: String,
    val patientName: String,
    val medicationType: String,
    val frequency: String,
    val days: String?,
    val times: List<MedicationTime>,
    val startDate: String,
    val endDate: String,
    val instructions: String,
    val isVisibleItem: Boolean
)

data class MedicationTime(
    val time: String,
    val isTaken: Boolean
)

data class MedicationItem(
    val id: Int,
    val medication_type: String,
    val medication_name: String,
    val dosage: String,
    val frequency: String,
    val days: String?,
    val reminder_time: List<String>?,
    val start_date: String,
    val end_date: String,
    val notes: String?,
    val medication_for_whom: String,
    val reminder_status: Int,
    val user: User?,
    val family_member: FamilyMember?
)

data class MedicationItemDetail(
    val id: Int,
    val user_id: Int?,
    val family_member_id: Int?,
    val medication_type: String,
    val medication_name: String,
    val dosage: String,
    val frequency: String,
    val days: String?,
    val reminder_time: List<String>?,
    val start_date: String,
    val end_date: String,
    val prescription_docs: String?,
    val notes: String?,
    val medication_for_whom: String,
    val reminder_status: Int,
    val created_at: String?,
    val updated_at: String?,
    val user: User?,
    val family_member: FamilyMember?
)

data class MedicationDetailUiState(
    val isMedicationLoading: Boolean = false,
    val isFamilyLoading: Boolean = false,
    val medication: MedicationItemDetail? = null,
    val error: String? = null
)