package com.bussiness.curemegptapp.ui.screen.intro

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

    LaunchedEffect(Unit) {
        delay(2000)

        // ✅ Deep link process hone tak wait karo — jab tak isDeepLinkPending false na ho
        var waited = 0
        while (isDeepLinkPending && waited < 5000) {
            delay(100)
            waited += 100
        }

        // ✅ Agar deep link abhi bhi pending hai (5 sec timeout ke baad bhi), to skip karo
        // MainActivity ka LaunchedEffect khud ChatDataScreen pe navigate kar dega
        if (!isDeepLinkPending) {
            navigateToNext(navController, sessionManager)
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