package com.bussiness.curemegptapp.ui.screen.intro

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import com.bussiness.curemegptapp.R
import com.bussiness.curemegptapp.navigation.AppDestination
import com.bussiness.curemegptapp.util.SessionManager
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavHostController, isDeepLinkPending: Boolean = false) {

    val context = LocalContext.current
    val sessionManager = SessionManager.getInstance(context)

   /* LaunchedEffect(isDeepLinkPending) {
        if (isDeepLinkPending) return@LaunchedEffect

        delay(2000)
        
        // ✅ Double check: Ensure Splash is still the current destination before navigating to Home/Onboarding.
        // This prevents overriding a deep link navigation that might have already started.
        val currentDestination = navController.currentDestination?.route
        val splashRoute = AppDestination.Splash::class.qualifiedName
        
        if (currentDestination == null || splashRoute == null || currentDestination.contains("Splash")) {
            navigateToNext(navController, sessionManager)
        }
    }*/
    LaunchedEffect(isDeepLinkPending) {
        Log.d("Splash", "DeepLink Pending = $isDeepLinkPending")

        if (isDeepLinkPending) {
            Log.d("Splash", "Returning because deep link pending")
            return@LaunchedEffect
        }

        delay(2000)

        val current = navController.currentDestination?.route
        Log.d("Splash", "Current Route = $current")


        if (current == null || current.contains("Splash")) {
            navigateToNext(navController, sessionManager)
        } else {
            Log.d("Splash", "Skipping default navigation — already navigated to $current")
        }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color.White)) {
        Image(painter = painterResource(R.drawable.splash1),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop)
    }
}

private fun navigateToNext(navController: NavHostController, sessionManager: SessionManager) {
    if (sessionManager.isLoggedIn()) {
        navController.navigate(AppDestination.MainScreen) {
            popUpTo(AppDestination.Splash) { inclusive = true }
        }
    } else {
        navController.navigate(AppDestination.Onboarding) {
            popUpTo(AppDestination.Splash) { inclusive = true }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SplashScreenPreview() {
    SplashScreen(navController = NavHostController(LocalContext.current))
}
