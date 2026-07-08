package com.bussiness.curemegptapp.apimodel.homemodel

data class HealthSummary(
    val allergies: MutableList<String>,
    val current_medications: MutableList<String>
)