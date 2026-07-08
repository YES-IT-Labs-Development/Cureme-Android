package com.bussiness.curemegptapp.data.model

data class ExpandableItem(
    val id: Int,
    val title: String,
    val content: String,
    val isExpanded: Boolean = false
)