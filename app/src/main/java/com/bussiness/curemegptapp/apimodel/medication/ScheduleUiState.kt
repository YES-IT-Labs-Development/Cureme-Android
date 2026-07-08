package com.bussiness.curemegptapp.apimodel.medication

import com.bussiness.curemegptapp.ui.viewModel.main.AppointmentUIModel

data class ScheduleUiState(
    val isLoading: Boolean = false,
    val appointmentList: List<AppointmentUIModel> = emptyList(),
    val medicationList: List<Medication> = emptyList(),
    val error: String? = null
)
