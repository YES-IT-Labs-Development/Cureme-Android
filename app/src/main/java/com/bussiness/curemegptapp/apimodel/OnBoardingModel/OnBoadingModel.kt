package com.bussiness.curemegptapp.apimodel.OnBoardingModel

import com.bussiness.curemegptapp.repository.BaseResponse

data class OnboardingResponse(
    val code: Int?,
    val data: OnboardingDataWrapper?,
    override val success: Boolean?,
    override val message: String?
) : BaseResponse


data class OnboardingDataWrapper(
    val data: List<OnboardingItem>?
)


data class OnboardingItem(
    val id: Int?,
    val heading: String?,
    val description: String?,
    val image: String?,
    val created_at: String?,
    val updated_at: String?
)