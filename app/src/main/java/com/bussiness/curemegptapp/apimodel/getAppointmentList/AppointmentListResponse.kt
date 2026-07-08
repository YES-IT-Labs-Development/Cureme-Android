package com.bussiness.curemegptapp.apimodel.getAppointmentList

import com.bussiness.curemegptapp.repository.BaseResponse

data class AppointmentListResponse(
    val code: Int? = null,
    val data: AppointmentDataWrapper? = null,
    override val success: Boolean?,
    override val message: String?
) : BaseResponse

/*
data class ProfileResponse(
    val code: Int? = null,
    val data: Data1? = null,
    override val success: Boolean?,
    override val message: String?
) : BaseResponse
 */

data class AppointmentDataWrapper(
    val data: List<AppointmentItem>
)

data class AppointmentItem(
    val id: Int,
    val user_id: Int,
    val family_member_id: Int?,
    val appointment_type_id: Int,
    val description: String,
    val date: String,
    val time: String,
    val preferred_doctor: String,
    val preferred_clinic: String,
    val appointment_reminder: String,
    val appointment_for_whom: String,
    val created_at: String,
    val updated_at: String,
    val user: User?,
    val family_member: FamilyMember?,
    val appointment_type: AppointmentType?,
    val recommended_chat_id :Int? =0,
    val complete_status :Int =0

)

data class AppointmentData(
    val id: Int? = null,
    val user_id: Int? = null,
    val family_member_id: Int? = null,
    val appointment_type_id: Int? = null,
    val description: String? = null,
    val date: String? = null,
    var time: String? = null,
    val preferred_doctor: String? = null,
    val preferred_clinic: String? = null,
    val appointment_reminder: String? = null,
    val appointment_for_whom: String? = null,
    val created_at: String? = null,
    val updated_at: String? = null,
    val user: User? = null,
    val family_member: FamilyMember? = null,
    val appointment_type: AppointmentType? = null,
    val recommended_chat_id :Int? =0,
    val complete_status :Int =0
)


data class User(
    val id: Int,
    val name: String
)

data class FamilyMember(
    val id: Int,
    val full_name: String
)

data class AppointmentType(
    val id: Int,
    val name: String
)