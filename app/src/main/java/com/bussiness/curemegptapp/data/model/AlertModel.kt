package com.bussiness.curemegptapp.data.model

data class AlertModel(
    val id: Int,
    val name: String,
    val title: String,
    val description: String,
    val time: String,
    val priority: String = "",
    val actionRequired: Boolean = false,
)
