package com.bussiness.curemegptapp.apimodel.personalmodel

import com.bussiness.curemegptapp.repository.BaseResponse

data class PersonalModel(
    val `data`: Data?,
    override  val success: Boolean? = null,
    val code: Int? = null,
    override val message: String? = null
) : BaseResponse
