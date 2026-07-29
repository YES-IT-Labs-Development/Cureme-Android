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
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var sessionManager: SessionManager
    @Inject
    lateinit var repository: Repository

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
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // ✅ Deep link ka signal turant (synchronously) check karo — AppsFlyer intent mein af_deeplink extra daalta hai
        val hasDeepLinkSignal = intent?.data != null ||
                intent?.getBooleanExtra("af_deeplink", false) == true ||
                intent?.extras?.keySet()?.any { it.startsWith("af_") } == true

        setContent {
            MaterialTheme {
                val useDarkIcons = MaterialTheme.colorScheme.background.luminance() > 0.5f
                SetStatusBarColor(color = Color.Transparent, darkIcons = useDarkIcons)
                val mainNavController = rememberNavController()
                val loaderViewModel: GlobalLoaderViewModel = hiltViewModel()
                val isLoading by loaderViewModel.isLoading.collectAsState()
                var showSessionDialog by remember { mutableStateOf(false) }
                var activeFcmNotification by remember { mutableStateOf<FcmNotificationEvent?>(null) }

                // ✅ Jab tak deep link process na ho jaye, dusra navigation (Home/session) rukega
                var isDeepLinkPending by remember { mutableStateOf(hasDeepLinkSignal) }

                LaunchedEffect(Unit) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 101)
                    }
                    SessionEventBus.sessionExpiredFlow.collect {
                        showSessionDialog = true
                    }
                }


                LaunchedEffect(Unit) {
                    DeepLinkManager.deepLinkData.collect { data ->
                        if (data == null) {
                            // Deep link data nahi mili (ho sakta hai ye normal cold start ho, deep link se nahi)
                            isDeepLinkPending = false
                            return@collect
                        }

                        when (data.deepLinkType) {

                            "chat" -> {

                                repository.getPromptQuestions().collectLatest { result ->
                                    if (result is NetworkResult.Success) {
                                        val familyList = result.data?.family_details ?: emptyList()

                                        mainNavController.currentBackStackEntry
                                            ?.savedStateHandle?.set("chatId", data.chatId)
                                        mainNavController.currentBackStackEntry
                                            ?.savedStateHandle?.set("familyMemberId", data.familyMemberId)
                                        mainNavController.currentBackStackEntry
                                            ?.savedStateHandle?.set("type", data.type)
                                        mainNavController.currentBackStackEntry
                                            ?.savedStateHandle?.set("chatHistory", data.chatHistory)
                                        mainNavController.currentBackStackEntry
                                            ?.savedStateHandle?.set("familyList", familyList)
                                        mainNavController.currentBackStackEntry
                                            ?.savedStateHandle?.set("isFromDeepLink", true)

                                        Log.d("MainActivity@@@@@@", "onCreate: $familyList")

                                        mainNavController.navigate(AppDestination.ChatDataScreen)
                                        DeepLinkManager.clear()
                                        isDeepLinkPending = false   // ✅ ab dusre navigation allow karo
                                    } else if (result is NetworkResult.Error) {
                                        // Family list fail hui to bhi navigate karo, empty list ke saath
                                        mainNavController.currentBackStackEntry
                                            ?.savedStateHandle?.set("chatId", data.chatId)
                                        mainNavController.currentBackStackEntry
                                            ?.savedStateHandle?.set("familyMemberId", data.familyMemberId)
                                        mainNavController.currentBackStackEntry
                                            ?.savedStateHandle?.set("type", data.type)
                                        mainNavController.currentBackStackEntry
                                            ?.savedStateHandle?.set("chatHistory", data.chatHistory)
                                        mainNavController.currentBackStackEntry
                                            ?.savedStateHandle?.set("familyList", emptyList<Any>())

                                        mainNavController.navigate(AppDestination.ChatDataScreen)
                                        DeepLinkManager.clear()
                                        isDeepLinkPending = false
                                    }
                                }
                            }

                            "report" -> {
                                DeepLinkManager.setReportDeepLink(data.reportId)
                                mainNavController.navigate(AppDestination.MainScreen) {
                                    popUpTo(AppDestination.Splash) {
                                        inclusive = true
                                    }
                                    launchSingleTop = true
                                }
                                isDeepLinkPending = false
                            }

                            else -> {
                                isDeepLinkPending = false
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
                    // ✅ isDeepLinkPending pass kiya — AppNavGraph ko bhi update karna hoga
                    AppNavGraph(navController = mainNavController, isDeepLinkPending = isDeepLinkPending)
                    LoaderOverlay(isVisible = isLoading)
                    // Session Expired Dialog
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
                    // FCM Notification Dialog
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