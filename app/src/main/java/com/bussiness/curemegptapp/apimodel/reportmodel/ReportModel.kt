package com.bussiness.curemegptapp.apimodel.reportmodel

import com.bussiness.curemegptapp.repository.BaseResponse

data class ReportModel(
    val `data`: MutableList<Data>?,
    override  val success: Boolean? = null,
    val code: Int? = null,
    override val message: String? = null
): BaseResponse