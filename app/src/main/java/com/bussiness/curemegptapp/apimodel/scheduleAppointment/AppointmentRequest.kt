package com.bussiness.curemegptapp.apimodel.scheduleAppointment

data class AppointmentRequest(
    val forWhomId: String? ="",
    val appointmentTypeId: String="",
    val description: String="",
    val date: String ="", // Recommended format: "yyyy-MM-dd"
    val time: String ="", // Recommended format: "HH:mm"
    val preferredDoctor: String="",
    val preferredClinic: String="",
    val appointmentReminder: String = ""
)
