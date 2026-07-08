package com.bussiness.curemegptapp.apimodel.homemodel

import com.bussiness.curemegptapp.repository.BaseResponse

data class HomeDataModel(
    val `data`: Data?=null,
    override  val success: Boolean? = null,
    val code: Int? = null,
    override val message: String? = null
): BaseResponse
