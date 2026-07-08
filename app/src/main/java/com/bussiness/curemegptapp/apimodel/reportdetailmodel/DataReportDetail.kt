package com.bussiness.curemegptapp.apimodel.reportdetailmodel

data class DataReportDetail(
    val ai_insights: AiInsights?=null,
    val attachments: MutableList<String>?=null,
    val chat_date: String?="",
    val chat_id: Int?=0,
    val detailed_analysis: String?="",
    val family_name: String?="",
    val severity: String?="",
    val summary: String?="",
    val title: String?="",
    val user_name: String?=""
)