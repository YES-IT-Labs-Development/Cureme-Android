package com.bussiness.curemegptapp.data.model

// New data class for StepItem
data class StepItem(
    val title: String,
    val icon: Int,        // default icon
    val selectedIcon: Int // tick icon when completed/selected
)
