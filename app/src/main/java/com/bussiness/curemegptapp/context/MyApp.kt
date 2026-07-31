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

//        DeepLinkManager.startProcessing()

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
                            val encryptedMemberName = deepLink.getStringValue("memberName")

                            DeepLinkManager.setChatDeepLink(
                                chatId = encryptedChatId?.let { LinkEncryptionHelper.decrypt(it) } ?: 0,
                                familyMemberId = encryptedFamilyId?.let { LinkEncryptionHelper.decrypt(it) } ?: 0,
                                type = deepLink.getStringValue("type") ?: "normal",
                                chatHistory = deepLink.getStringValue("chatHistory")?.toBoolean() ?: false,
                                memberName = encryptedMemberName?.let { LinkEncryptionHelper.decryptString(it) } ?: "",
                            )
                        }

                        "report" -> {

                            val encryptedReportId = deepLink.getStringValue("reportId")

                            DeepLinkManager.setReportDeepLink(
                                reportId = encryptedReportId?.let { LinkEncryptionHelper.decrypt(it) } ?: 0
                            )
                        }
                        
                        else -> {
                            DeepLinkManager.stopProcessing()
                        }
                    }
                }

                DeepLinkResult.Status.NOT_FOUND -> {
                    Timber.d("Deep Link Not Found")
                    DeepLinkManager.stopProcessing()
                }

                DeepLinkResult.Status.ERROR -> {
                    Timber.e("Deep Link Error : ${result.error}")
                    DeepLinkManager.stopProcessing()
                }
            }

        }

        AppsFlyerLib.getInstance().start()
    }

}
