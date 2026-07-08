package com.bussiness.curemegptapp.di

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

data class    FcmNotificationEvent(
    val title: String,
    val body: String,
    val type: String
)

object SessionEventBus {
    private val _sessionExpiredFlow = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val sessionExpiredFlow = _sessionExpiredFlow.asSharedFlow()

    private val _fcmNotificationFlow = MutableSharedFlow<FcmNotificationEvent>(extraBufferCapacity = 1)
    val fcmNotificationFlow = _fcmNotificationFlow.asSharedFlow()

    var isAppInForeground: Boolean = false

    fun emitSessionExpired() {
        _sessionExpiredFlow.tryEmit(Unit)
    }

    fun emitFcmNotification(event: FcmNotificationEvent) {
        _fcmNotificationFlow.tryEmit(event)
    }
}