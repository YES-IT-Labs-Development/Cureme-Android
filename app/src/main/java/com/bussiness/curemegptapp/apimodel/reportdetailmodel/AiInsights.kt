package com.bussiness.curemegptapp.apimodel.reportdetailmodel

data class AiInsights(
    val action_type: String,
    val category: String,
    val reason: String,
    val severity: String,
    val symptom_analysis: List<String>
)