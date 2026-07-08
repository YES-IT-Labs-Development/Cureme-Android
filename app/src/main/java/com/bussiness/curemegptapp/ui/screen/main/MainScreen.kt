package com.bussiness.curemegptapp.ui.screen.main


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.bussiness.curemegptapp.R
import com.bussiness.curemegptapp.data.model.BottomItem
import com.bussiness.curemegptapp.navigation.AppDestination
import com.bussiness.curemegptapp.navigation.MainNavGraph
import com.bussiness.curemegptapp.ui.component.input.CustomBottomBar
import com.bussiness.curemegptapp.util.AppConstant
import androidx.compose.runtime.getValue



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(authNavController: NavHostController,) {

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    //val currentRoute = getCurrentRoute(navController)

    // Bottom nav sirf in routes par dikhana hai
    val bottomNavRoutes = listOf(
        AppDestination.Home::class.qualifiedName!!,
        AppDestination.Schedule::class.qualifiedName!!,
        AppDestination.Family::class.qualifiedName!!,
        AppDestination.Reports::class.qualifiedName!!
    )

    val bottomBarItems = listOf(
        BottomItem(AppConstant.BOTTOM_NAV_HOME, R.drawable.ic_home_icon, AppDestination.Home::class.qualifiedName!!),
        BottomItem(AppConstant.BOTTOM_NAV_SCHEDULE, R.drawable.ic_schedule_icon, AppDestination.Schedule::class.qualifiedName!!),
        BottomItem(AppConstant.BOTTOM_NAV_FAMILY, R.drawable.ic_family_icon, AppDestination.Family::class.qualifiedName!!),
        BottomItem(AppConstant.BOTTOM_NAV_REPORTS, R.drawable.ic_report_icon, AppDestination.Reports::class.qualifiedName!!)
    )

    val selectedIndex = bottomBarItems.indexOfFirst { it.route == currentRoute }.coerceAtLeast(0)

    Box(
        Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        MainNavGraph(
            authNavController = authNavController,
            navController = navController,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = if (currentRoute in bottomNavRoutes) 80.dp else 0.dp)
        )

        /** Bottom nav only on selected screens */
//        AnimatedVisibility(
//            visible = currentRoute in bottomNavRoutes,
//            enter = fadeIn(),
//            exit = fadeOut(),
//            modifier = Modifier.align(Alignment.BottomCenter)
//        ) {
        if (currentRoute in bottomNavRoutes) {
            CustomBottomBar(
                modifier = Modifier.align(Alignment.BottomCenter),
                selectedIndex = selectedIndex,
                onItemSelected = { index ->
                    val item = bottomBarItems[index]

//                    navController.navigate(item.route) {
//                        popUpTo(navController.graph.startDestinationId) { inclusive = false }
//                        launchSingleTop = true
//                    }
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }

                },
                onClickAIIcon = {
                   // navController.navigate(AppDestination.OpenChatScreen1)
                    navController.navigate("openChat?from=main")
                }
            )
        }
    }
}



@Composable
fun getCurrentRoute(navController: NavController): String {
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    return navBackStackEntry.value?.destination?.route ?: ""
}

/*
package com.bussiness.curemegptapp.ui.screen.main

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.bussiness.curemegptapp.R
import com.bussiness.curemegptapp.data.model.BottomItem
import com.bussiness.curemegptapp.navigation.AppDestination
import com.bussiness.curemegptapp.navigation.MainNavGraph
import com.bussiness.curemegptapp.ui.component.input.CustomBottomBar
import com.bussiness.curemegptapp.util.AppConstant
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(authNavController: NavHostController) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomBarItems = listOf(
        BottomItem(AppConstant.BOTTOM_NAV_HOME, R.drawable.ic_home_icon, AppDestination.Home::class.qualifiedName!!),
        BottomItem(AppConstant.BOTTOM_NAV_SCHEDULE, R.drawable.ic_schedule_icon, AppDestination.Schedule::class.qualifiedName!!),
        BottomItem(AppConstant.BOTTOM_NAV_FAMILY, R.drawable.ic_family_icon, AppDestination.Family::class.qualifiedName!!),
        BottomItem(AppConstant.BOTTOM_NAV_REPORTS, R.drawable.ic_report_icon, AppDestination.Reports::class.qualifiedName!!)
    )

    val bottomNavRoutes = bottomBarItems.map { it.route }
    val selectedIndex = bottomBarItems.indexOfFirst { it.route == currentRoute }.coerceAtLeast(0)

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        containerColor = Color.White,
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            MainNavGraph(
                authNavController = authNavController,
                navController = navController,
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(bottom = if (currentRoute in bottomNavRoutes) 100.dp else 0.dp)
            )

            if (currentRoute in bottomNavRoutes) {
                CustomBottomBar(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    selectedIndex = selectedIndex,
                    onItemSelected = { index ->
                        val item = bottomBarItems[index]
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onClickAIIcon = {
                        navController.navigate("openChat?from=main")
                    }
                )
            }
        }
    }
}
 */

