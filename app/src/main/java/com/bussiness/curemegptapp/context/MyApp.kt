package com.bussiness.curemegptapp.context

import android.app.Application
import com.appsflyer.AppsFlyerLib

import com.google.android.gms.tasks.Task
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
            .addOnCompleteListener { task: Task<String> ->
                if (task.isSuccessful) {
                    val token = task.result
                    Timber.d("FCM Token: $token")
                }
            }

        AppsFlyerLib.getInstance().setDebugLog(true)

        AppsFlyerLib.getInstance().init(
            "eyUDbhV7uqoqsrG3fKPWaF",
            null,
            this
        )

    }

}