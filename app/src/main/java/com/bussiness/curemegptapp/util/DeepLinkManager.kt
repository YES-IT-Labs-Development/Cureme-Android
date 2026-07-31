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
    val memberName: String = "",
    val reportId: Int = 0
)

object DeepLinkManager {

    private val _deepLinkData = MutableStateFlow<DeepLinkData?>(null)
    val deepLinkData: StateFlow<DeepLinkData?> = _deepLinkData.asStateFlow()

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()

    fun startProcessing() {
        _isProcessing.value = true
    }

    fun stopProcessing() {
        _isProcessing.value = false
    }

    fun setChatDeepLink(
        chatId: Int,
        familyMemberId: Int,
        type: String,
        chatHistory: Boolean,
        memberName: String = "",
    ) {
        _deepLinkData.value = DeepLinkData(
            deepLinkType = "chat",
            chatId = chatId,
            familyMemberId = familyMemberId,
            type = type,
            chatHistory = chatHistory,
            memberName = memberName,
        )
        stopProcessing()
    }

    fun setReportDeepLink(
        reportId: Int
    ) {
        _deepLinkData.value = DeepLinkData(
            deepLinkType = "report",
            reportId = reportId
        )
        stopProcessing()
    }

    fun clear() {
        _deepLinkData.value = null
    }
}
