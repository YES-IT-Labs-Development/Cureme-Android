package com.bussiness.curemegptapp.data.model

data class HealthProfile(
    val id: Int,
    val name: String,
    val age: String,
    val relation: String,
    val lastCheckup: String,
    val alerts: List<String>
)
