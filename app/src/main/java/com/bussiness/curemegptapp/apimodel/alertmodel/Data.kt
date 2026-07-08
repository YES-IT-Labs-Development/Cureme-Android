package com.bussiness.curemegptapp.apimodel.alertmodel

data class Data(
    val action_required: Int?=0,
    val appointment_complete_status: String?="",
    val family_member_name: String?="",
    val id: Int?=0,
    val is_read: Int?=0,
    val message: String?="",
    val notification_time: String?="",
    val reference_id: Int,
    val severity: String?="",
    val title: String?="",
    val type: String?="",
    val user_name: String?=""
)