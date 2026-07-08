package com.bussiness.curemegptapp.apimodel.reportdetailmodel

import com.bussiness.curemegptapp.repository.BaseResponse

data class ReportDetailsModel(
    var `data`: DataReportDetail?,
    override  val success: Boolean? = null,
    val code: Int? = null,
    override val message: String? = null
): BaseResponse