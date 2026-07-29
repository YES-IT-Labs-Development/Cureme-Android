package com.bussiness.curemegptapp.data.model

import com.google.gson.annotations.SerializedName

data class MoodSummaryResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("code") val code: Int,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: MoodSummaryData?
)

data class MoodSummaryData(
    @SerializedName("title") val title: String,
    @SerializedName("summary") val summary: String
)

