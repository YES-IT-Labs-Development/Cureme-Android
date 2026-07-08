package com.bussiness.curemegptapp.apimodel.homemodel

data class MembersDetails(
    var family: MutableList<Family>,
    val myself: Myself
)