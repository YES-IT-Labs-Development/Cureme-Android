package com.bussiness.curemegptapp.apimodel.alertmodel

import com.bussiness.curemegptapp.repository.BaseResponse

data class AlertDateModel(
    val `data`: MutableList<Data>?=null,
    override  val success: Boolean? = null,
    val code: Int? = null,
    override val message: String? = null
): BaseResponse