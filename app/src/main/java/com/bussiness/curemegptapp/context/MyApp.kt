package com.bussiness.curemegptapp.context

import android.app.Application
import com.appsflyer.AppsFlyerLib
import com.appsflyer.share.deeplink.DeepLinkResult
import com.bussiness.curemegptapp.util.DeepLinkManager
import com.bussiness.curemegptapp.util.LinkEncryptionHelper
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class MyApp : Application(){
    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)

        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Timber.d("FCM Token: ${task.result}")
                }
            }

        AppsFlyerLib.getInstance().setDebugLog(true)

        AppsFlyerLib.getInstance().init(
            "eyUDbhV7uqoqsrG3fKPWaF",
            null,
            this
        )

        AppsFlyerLib.getInstance().subscribeForDeepLink { result ->

            Timber.d("AppsFlyer Result : $result")

            when (result.status) {

                DeepLinkResult.Status.FOUND -> {

                    val deepLink = result.deepLink
                    val deepLinkValue = deepLink.getStringValue("deep_link_value")

                    when (deepLinkValue) {

                        "chat" -> {

                            val encryptedChatId = deepLink.getStringValue("chatId")
                            val encryptedFamilyId = deepLink.getStringValue("familyMemberID")

                            DeepLinkManager.setChatDeepLink(
                                chatId = LinkEncryptionHelper.decrypt(encryptedChatId) ?: 0,
                                familyMemberId = LinkEncryptionHelper.decrypt(encryptedFamilyId) ?: 0,
                                type = deepLink.getStringValue("type") ?: "normal",
                                chatHistory = deepLink.getStringValue("chatHistory")?.toBoolean() ?: false
                            )
                        }

                        "report" -> {

                            val encryptedReportId = deepLink.getStringValue("reportId")

                            DeepLinkManager.setReportDeepLink(
                                reportId = LinkEncryptionHelper.decrypt(encryptedReportId) ?: 0
                            )
                        }
                    }
                }

                DeepLinkResult.Status.NOT_FOUND -> {
                    Timber.d("Deep Link Not Found")
                }

                DeepLinkResult.Status.ERROR -> {
                    Timber.e("Deep Link Error : ${result.error}")
                }
            }

        }

        AppsFlyerLib.getInstance().start()
    }

}