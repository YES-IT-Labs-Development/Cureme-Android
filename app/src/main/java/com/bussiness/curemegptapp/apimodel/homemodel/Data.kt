package com.bussiness.curemegptapp.apimodel.homemodel

data class Data(
    val health_summary: HealthSummary?,
    val members_details: MembersDetails?,
    var recommended_next_steps: MutableList<String>?= null,
    val things_need_attention: ThingsNeedAttention?,
    val user_context: UserContext,
    var healthList: MutableList<Family>?= null,
    var needAttentionList: MutableList<FamilyX>?= null
)