package com.bussiness.curemegptapp.apimodel.homemodel

data class ThingsNeedAttention(
    val family: MutableList<FamilyX>?= null,
    val myself: MutableList<String>?= null
)