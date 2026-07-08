package com.bussiness.curemegptapp.apimodel.chatModel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MedicationSummary(
    val name: String? = null,
    val dosage: String? = null,
    val instructions: String? = null
) : Parcelable

@Parcelize
data class SummaryData(
    val chat_id: Int? = null,
    val title: String? = null,
    val summary: String? = null,
    val symptoms: List<String>? = null,
    val recommendations: List<String>? = null,
    val medications: List<MedicationSummary>? = null,
    val next_steps: List<String>? = null
) : Parcelable

@Parcelize
data class SummaryResponse(
    val success: Boolean,
    val code: Int,
    val message: String,
    val data: SummaryData? = null
) : Parcelable
