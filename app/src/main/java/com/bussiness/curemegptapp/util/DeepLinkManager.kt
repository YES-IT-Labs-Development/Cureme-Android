package com.bussiness.curemegptapp.util

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class DeepLinkData(
    val deepLinkType: String,
    val chatId: Int = 0,
    val familyMemberId: Int = 0,
    val type: String = "normal",
    val chatHistory: Boolean = false,
    val reportId: Int = 0
)

object DeepLinkManager {

    private val _deepLinkData = MutableStateFlow<DeepLinkData?>(null)
    val deepLinkData: StateFlow<DeepLinkData?> = _deepLinkData.asStateFlow()

    fun setChatDeepLink(
        chatId: Int,
        familyMemberId: Int,
        type: String,
        chatHistory: Boolean
    ) {
        _deepLinkData.value = DeepLinkData(
            deepLinkType = "chat",
            chatId = chatId,
            familyMemberId = familyMemberId,
            type = type,
            chatHistory = chatHistory
        )
    }

    fun setReportDeepLink(
        reportId: Int
    ) {
        _deepLinkData.value = DeepLinkData(
            deepLinkType = "report",
            reportId = reportId
        )
    }

    fun clear() {
        _deepLinkData.value = null
    }
}