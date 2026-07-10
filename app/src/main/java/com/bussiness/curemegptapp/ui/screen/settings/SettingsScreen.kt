package com.bussiness.curemegptapp.ui.screen.settings


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.bussiness.curemegptapp.ui.component.TopBarHeader2
import com.bussiness.curemegptapp.R
import com.bussiness.curemegptapp.navigation.AppDestination
import com.bussiness.curemegptapp.ui.dialog.LogOutDialog
import com.bussiness.curemegptapp.util.SessionManager
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.bussiness.curemegptapp.viewmodel.setting.SettingsViewModel

@Composable
fun SettingsScreen(
    navController: NavHostController,
    authNavController : NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val staticPages by viewModel.staticPages.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    androidx.compose.runtime.LaunchedEffect(Unit) {
        viewModel.getAllStaticPages()
    }

    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val sessionManager = SessionManager.getInstance(context)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFFFF))
            .statusBarsPadding()
    ) {
        TopBarHeader2(title = stringResource(R.string.settings_title)/*"Settings"*/, onBackClick = { navController.popBackStack() })

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = 20.dp, start = 20.dp, end = 20.dp)
        ) {
            // Settings Card with menu items
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9FD)),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    val filteredPages = staticPages.filter {
                        val slug = it.slug.lowercase()
                        slug != "faq" && slug != "frequently-ask-questions" && slug != "frequently-asked-questions"
                    }
                    filteredPages.forEachIndexed { index, page ->
                        val iconRes = when (page.slug.lowercase()) {
                            "about-us" -> R.drawable.ic_info_setting_icon
                            "privacy-policy" -> R.drawable.ic_privacy_policy_setting_icon
                            "terms-conditions" -> R.drawable.ic_terms_condition_setting_icon
                            "account-privacy" -> R.drawable.ic_account_setting_icon
                            "help-support" -> R.drawable.ic_help_setting_icon
                            else -> R.drawable.ic_privacy_policy_setting_icon
                        }

                        SettingsMenuItem(
                            icon = iconRes,
                            title = page.title,
                            onClick = {
                                viewModel.getParticularStaticPage(page.slug) { fetchedPage ->
                                    when (fetchedPage.slug.lowercase()) {
                                        "about-us" -> {
                                            navController.currentBackStackEntry?.savedStateHandle?.set("title", fetchedPage.title)
                                            navController.currentBackStackEntry?.savedStateHandle?.set("content", fetchedPage.content)
                                            navController.navigate(AppDestination.AboutScreen)
                                        }
                                        "privacy-policy" -> {
                                            navController.currentBackStackEntry?.savedStateHandle?.set("title", fetchedPage.title)
                                            navController.currentBackStackEntry?.savedStateHandle?.set("content", fetchedPage.content)
                                            navController.navigate(AppDestination.PrivacyPolicyScreen)
                                        }
                                        "terms-conditions" -> {
                                            navController.currentBackStackEntry?.savedStateHandle?.set("title", fetchedPage.title)
                                            navController.currentBackStackEntry?.savedStateHandle?.set("content", fetchedPage.content)
                                            navController.navigate(AppDestination.TermsAndConditionsScreen)
                                        }
                                        "account-privacy" -> {
                                            navController.currentBackStackEntry?.savedStateHandle?.set("title", fetchedPage.title)
                                            navController.currentBackStackEntry?.savedStateHandle?.set("content", fetchedPage.content)
                                            navController.navigate(AppDestination.AccountPrivacyScreen)
                                        }
                                        "help-support" -> {
                                            navController.currentBackStackEntry?.savedStateHandle?.set("title", fetchedPage.title)
                                            navController.currentBackStackEntry?.savedStateHandle?.set("content", fetchedPage.content)
                                            navController.navigate(AppDestination.HelpSupportScreen)
                                        }
                                        else -> {
                                            navController.currentBackStackEntry?.savedStateHandle?.set("title", fetchedPage.title)
                                            navController.currentBackStackEntry?.savedStateHandle?.set("content", fetchedPage.content)
                                            navController.navigate(AppDestination.AboutScreen)
                                        }
                                    }
                                }
                            }
                        )

                        SettingsMenuDivider()
                    }

                    // Static Frequently Asked Questions Item
                    SettingsMenuItem(
                        icon = R.drawable.ic_ask_setting_icon,
                        title = "Frequently Asked Questions",
                        onClick = {
                            navController.navigate(AppDestination.FrequentlyAskQuestionsScreen)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(21.dp))

            // Logout Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable( interactionSource = remember { MutableInteractionSource() },
                        indication = null) {
                        showDialog = true
                    },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Logout Button with Icon
                Surface(
                    shape = RoundedCornerShape(105.dp),
                    color = Color.Unspecified,
                    shadowElevation = 8.dp
                ) {
                    // Hexagon background effect
                    Image(
                        painter = painterResource(id = R.drawable.ic_logout_icon2),
                        contentDescription = "Logout",
                        modifier = Modifier.size(83.dp, 80.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.settings_logout_button)/*"Logout"*/,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                    color = Color(0xFFEE1E1E)
                )
            }

            Spacer(modifier = Modifier.height(17.dp))

            // Large Background Text

                Image(
                    painter = painterResource(id = R.drawable.curemegpt_text),
                    contentDescription = "Logout",
                    modifier = Modifier
                        .height(55.dp)
                        .fillMaxWidth()
                )


            Spacer(modifier = Modifier.height(10.dp))

            // Version Text
            Text(
                text = stringResource(R.string.settings_version)/*"Version 0.0.1"*/,
                fontSize = 14.sp,
                color = Color(0xFF697383),
                fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Image(
                painter = painterResource(id = R.drawable.ic_bottom_icons),
                contentDescription = "",
                modifier = Modifier
                    .height(138.dp)
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
            )
        }
    }

    if (showDialog){
        LogOutDialog(
            title = stringResource(R.string.settings_logout_dialog_title)/*"Confirm Logout"*/,
            message = stringResource(R.string.settings_logout_dialog_message)/*"Are you sure you want to log out of your account?"*/,
            cancelText = stringResource(R.string.cancel_button)/*"Cancel"*/,
            confirmText = stringResource(R.string.settings_logout_dialog_confirm)/*"Yes, Logout"*/,
            onDismiss = {
                showDialog = false
            },
            onConfirm = {
                showDialog = false
                sessionManager.clearSession()
                authNavController.navigate(AppDestination.Login) {
                    popUpTo(AppDestination.MainScreen) { inclusive = true }
                }
            }
        )
    }
}

@Composable
fun SettingsMenuItem(
    icon: Int,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable( interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Icon Circle Background

                Box(contentAlignment = Alignment.Center) {
                    Image(
                        painter = painterResource(id = icon),
                        contentDescription = title,
                        modifier = Modifier.size(30.dp)
                    )
                }


            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                color = Color(0xFF000000)
            )
        }


        Image(
            painter = painterResource(id = R.drawable.ic_next_arrow_calender) ,
            contentDescription = "Navigate",
            modifier = Modifier.size(19.dp)
        )
    }
}


@Composable
fun SettingsMenuDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .height(1.dp)
            .background(Color(0xFFE7E6F8))
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SettingsScreenPreview() {
    val navController = rememberNavController()
    val navController1 = rememberNavController()
    SettingsScreen(navController,navController1)
}
