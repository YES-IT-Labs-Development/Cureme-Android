package com.bussiness.curemegptapp.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import com.bussiness.curemegptapp.di.FcmNotificationEvent
import com.bussiness.curemegptapp.ui.dialog.FcmNotificationDialog
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.bussiness.curemegptapp.di.SessionEventBus
import com.bussiness.curemegptapp.navigation.AppNavGraph
import com.bussiness.curemegptapp.ui.component.LoaderOverlay
import com.bussiness.curemegptapp.ui.component.SetStatusBarColor
import com.bussiness.curemegptapp.navigation.AppDestination
import com.bussiness.curemegptapp.repository.NetworkResult
import com.bussiness.curemegptapp.repository.Repository
import com.bussiness.curemegptapp.ui.dialog.AlertErrorDialog
import com.bussiness.curemegptapp.util.DeepLinkManager
import com.bussiness.curemegptapp.util.SessionManager
import com.bussiness.curemegptapp.viewmodel.GlobalLoaderViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var sessionManager: SessionManager
    @Inject
    lateinit var repository: Repository

    // Track if we detected a deep link in the intent
    private var isDeepLinkDetected by mutableStateOf(false)

    override fun onResume() {
        super.onResume()
        SessionEventBus.isAppInForeground = true
    }

    override fun onPause() {
        super.onPause()
        SessionEventBus.isAppInForeground = false
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        checkDeepLinkSignal(intent)
    }

    private fun checkDeepLinkSignal(intent: Intent?) {
        val hasSignal = intent?.data != null

        Log.d("DeepLink", "intent.data = ${intent?.data}")
        Log.d("DeepLink", "hasSignal = $hasSignal")

        isDeepLinkDetected = hasSignal

        if (hasSignal) {
            DeepLinkManager.startProcessing()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        checkDeepLinkSignal(intent)

        setContent {
            MaterialTheme {
                val useDarkIcons = MaterialTheme.colorScheme.background.luminance() > 0.5f
                SetStatusBarColor(color = Color.Transparent, darkIcons = useDarkIcons)

                val mainNavController = rememberNavController()
                val loaderViewModel: GlobalLoaderViewModel = hiltViewModel()
                val isLoading by loaderViewModel.isLoading.collectAsState()

                var showSessionDialog by remember { mutableStateOf(false) }
                var activeFcmNotification by remember { mutableStateOf<FcmNotificationEvent?>(null) }

                // Reactive isDeepLinkPending state
                val isAppsFlyerProcessing by DeepLinkManager.isProcessing.collectAsState()

                // ✅ Single source of truth — synchronously derived, no LaunchedEffect gap, no manual reassignment
                val isDeepLinkPending by remember {
                    derivedStateOf { isDeepLinkDetected && isAppsFlyerProcessing }
                }

                LaunchedEffect(Unit) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 101)
                    }
                    SessionEventBus.sessionExpiredFlow.collect {
                        showSessionDialog = true
                    }
                }

                // Handle DeepLink Data from Manager
                LaunchedEffect(Unit) {
                    DeepLinkManager.deepLinkData.collect { data ->
                        if (data == null) {
                            // Koi data nahi mila — agar AppsFlyer bhi processing khatam kar chuka hai,
                            // to isDeepLinkDetected false karo taaki derivedStateOf khud pending=false compute kare
                            if (!isAppsFlyerProcessing) {
                                isDeepLinkDetected = false
                            }
                            return@collect
                        }

                        when (data.deepLinkType) {
                            "chat" -> {
                                // Fetch family list before navigating
                                val result = withTimeoutOrNull(8000) {
                                    repository.getPromptQuestions()
                                        .filter { it is NetworkResult.Success || it is NetworkResult.Error }
                                        .first()
                                }

                                val familyList = if (result is NetworkResult.Success) {
                                    result.data?.family_details ?: emptyList()
                                } else {
                                    emptyList()
                                }

                                // Navigate to ChatDataScreen and pop Splash inclusively
                                mainNavController.navigate(AppDestination.ChatDataScreen) {
                                    popUpTo(AppDestination.Splash) { inclusive = true }
                                    launchSingleTop = true
                                }

                                // Pass data to ChatDataScreen via its savedStateHandle
                                mainNavController.currentBackStackEntry?.savedStateHandle?.let { h ->
                                    h.set("chatId", data.chatId)
                                    h.set("familyMemberId", data.familyMemberId)
                                    h.set("type", data.type)
                                    h.set("chatHistory", data.chatHistory)
                                    h.set("familyList", familyList)
                                    h.set("isFromDeepLink", true)
                                    h.set("memberName", data.memberName)
                                }

                                DeepLinkManager.clear()
                                DeepLinkManager.stopProcessing()
                                isDeepLinkDetected = false
                            }

                            "report" -> {
                                DeepLinkManager.setReportDeepLink(data.reportId)

                                // ✅ Splash-based branches ke saath consistent popUpTo target
                                mainNavController.navigate(AppDestination.MainScreen) {
                                    popUpTo(AppDestination.Splash) { inclusive = true }
                                    launchSingleTop = true
                                }

                                DeepLinkManager.stopProcessing()
                                isDeepLinkDetected = false
                            }

                            else -> {
                                DeepLinkManager.stopProcessing()
                                isDeepLinkDetected = false
                            }
                        }
                    }
                }

                LaunchedEffect(Unit) {
                    SessionEventBus.fcmNotificationFlow.collect { event ->
                        activeFcmNotification = event
                    }
                }

                Box(Modifier.fillMaxSize()) {
                    AppNavGraph(navController = mainNavController, isDeepLinkPending = isDeepLinkPending)
                    LoaderOverlay(isVisible = isLoading)

                    if (showSessionDialog) {
                        AlertErrorDialog(
                            message = "Your session has expired. Please log in again to continue.",
                            onDismiss = {
                                showSessionDialog = false
                                sessionManager.clearSession()
                                mainNavController.navigate(AppDestination.Login) {
                                    popUpTo(AppDestination.MainScreen) { inclusive = true }
                                }
                            },
                            onConfirm = {
                                showSessionDialog = false
                                sessionManager.clearSession()
                                mainNavController.navigate(AppDestination.Login) {
                                    popUpTo(AppDestination.MainScreen) { inclusive = true }
                                }
                            }
                        )
                    }
                    activeFcmNotification?.let { event ->
                        FcmNotificationDialog(
                            title = event.title,
                            body = event.body,
                            type = event.type,
                            onDismiss = { activeFcmNotification = null },
                            onConfirm = {
                                activeFcmNotification = null
                            }
                        )
                    }
                }
            }
        }
    }
}