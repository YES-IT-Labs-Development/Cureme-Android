package com.bussiness.curemegptapp.data.model

data class AttentionItem(
    val id: Int,
    val title: String,
    val subtitle: String,
    val isUrgent: Boolean,
    val forPerson: String,
    val isScheduled: Boolean = false,
    val createdAt: String = "", // For future API integration
    val dueDate: String = "" // For future API integration
)
