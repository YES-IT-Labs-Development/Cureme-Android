package com.bussiness.curemegptapp.apimodel.chatModel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FamilyDetails(
    val id: Int,
    val name: String,
    val relationship: String,
    val profile_photo: String?
) : Parcelable

data class PromptQuestion(
    val id: Int,
    val question: String,
    val category: String,
    val description: String,
    val status: Int,
    val usage_count: Int,
    val created_at: String,
    val updated_at: String
)

data class PromptQuestionResponse(
    val family_details: List<FamilyDetails> = emptyList(),
    val prompt_questions: List<PromptQuestion> = emptyList()
)