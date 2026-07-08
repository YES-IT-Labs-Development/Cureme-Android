package com.bussiness.curemegptapp.apimodel.chatModel


data class ChatHistoryItem(
    val id: String? = null,
    var title: String? = null,
    val createdAt: String? = null,
    var type :String ="normal"
)