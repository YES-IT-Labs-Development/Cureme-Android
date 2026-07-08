package com.bussiness.curemegptapp.apimodel.homemodel

data class Family(
    val active_alerts: ActiveAlerts?=null,
    val dob: String?,
    val id: Int?,
    val name: String?,
    val isSelected: Boolean?=false,
    val profile_image: String?,
    val last_appointment_days_ago: Int? = null
)