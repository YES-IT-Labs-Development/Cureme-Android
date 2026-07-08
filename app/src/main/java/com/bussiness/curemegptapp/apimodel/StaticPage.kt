package com.bussiness.curemegptapp.apimodel

data class StaticPage(
    val id: Int,
    val title: String,
    val content: String,
    val slug: String,
    val status: String
)
