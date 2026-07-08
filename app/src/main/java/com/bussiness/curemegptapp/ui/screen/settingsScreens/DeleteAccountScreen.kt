package com.bussiness.curemegptapp.ui.screen.settingsScreens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.bussiness.curemegptapp.R
import com.bussiness.curemegptapp.navigation.AppDestination
import com.bussiness.curemegptapp.ui.component.GradientButton
import com.bussiness.curemegptapp.ui.component.TopBarHeader2
import com.bussiness.curemegptapp.ui.dialog.LogOutDialog

@Composable
fun DeleteAccountScreen(navController: NavHostController, authNavController: NavController) {
    var selectedReason by remember { mutableStateOf<String?>(null) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFFFF))
            .statusBarsPadding()
    ) {
        TopBarHeader2(
            title = stringResource(R.string.delete_account)/*"Delete Account"*/,
            onBackClick = { navController.popBackStack() })

        // --------- IF selectedReason == null → IMAGE-1 SCREEN ---------
        if (selectedReason == null) {
            DeleteAccountOptionsUI(
                onReasonSelected = { reason ->
                    selectedReason = reason   // 🔥 Yeh value image2 me jayegi
                }
            )
        } else {
            // --------- IF selectedReason != null → IMAGE-2 SCREEN ---------
            DeleteAccountFeedbackUI(selectedReason!!, onDeleteClick = {
                // navController.navigate()
                authNavController.navigate(AppDestination.Login) {
                    popUpTo(AppDestination.MainScreen) { inclusive = true }
                }
            })
        }
    }
}


@Composable
fun DeleteAccountOptionsUI(onReasonSelected: (String) -> Unit) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(top = 20.dp, start = 20.dp, end = 20.dp)
    ) {
        Spacer(Modifier.height(20.dp))
        Text(
            stringResource(R.string.delete_my_account)/*"Delete my account"*/, fontSize = 18.sp,
            fontFamily = FontFamily(Font(R.font.urbanist_medium)),
            fontWeight = FontWeight.Medium,
            color = Color(0xFF3C3C3C)
        )

        Spacer(Modifier.height(10.dp))

        Text(
            stringResource(R.string.why_delete_account_question)/*"Why would you like to delete your account"*/,
            fontSize = 15.sp,
            fontFamily = FontFamily(Font(R.font.urbanist_regular)),
            fontWeight = FontWeight.Normal,
            color = Color(0xFF697383)
        )
        Spacer(Modifier.height(30.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9FD)),
            shape = RoundedCornerShape(20.dp)
        ) {

            Column(modifier = Modifier.padding(vertical = 8.dp)) {

                SettingsMenuItem(
                    icon = R.drawable.ic_info_setting_icon,
                    iconBoolean = false,
                    title = stringResource(R.string.reason_no_longer_use)/*"I no longer use the app"*/,
                    onClick = { onReasonSelected("I no longer use the app") }
                )

                SettingsMenuDivider()

                SettingsMenuItem(
                    icon = R.drawable.ic_privacy_policy_setting_icon,
                    iconBoolean = false,
                    title = stringResource(R.string.reason_data_privacy)/*"I’m concerned about my data privacy"*/,
                    onClick = { onReasonSelected("I’m concerned about my data privacy") }
                )

                SettingsMenuDivider()

                SettingsMenuItem(
                    icon = R.drawable.ic_terms_condition_setting_icon,
                    iconBoolean = false,
                    title = stringResource(R.string.reason_another_app)/*"I found another app I prefer"*/,
                    onClick = { onReasonSelected("I found another app I prefer") }
                )

                SettingsMenuDivider()

                SettingsMenuItem(
                    icon = R.drawable.ic_ask_setting_icon,
                    iconBoolean = false,
                    title = stringResource(R.string.reason_not_meet_needs)/*"The app doesn’t meet my needs"*/,
                    onClick = { onReasonSelected("The app doesn’t meet my needs") }
                )

                SettingsMenuDivider()

                SettingsMenuItem(
                    icon = R.drawable.ic_account_setting_icon,
                    iconBoolean = false,
                    title = stringResource(R.string.reason_remove_personal_data)/*"I want to remove all my personal data"*/,
                    onClick = { onReasonSelected("I want to remove all my personal data") }
                )
            }
        }
        Spacer(modifier = Modifier.height(17.dp))
    }
}

@Composable
fun DeleteAccountFeedbackUI(selectedReason: String, onDeleteClick: () -> Unit) {
    var showDialog by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 20.dp)
    ) {

//        Text(
//            text = selectedReason,
//            fontSize = 22.sp,
//            fontWeight = FontWeight.Bold,
//            color = Color.Black
//        )
        Column(Modifier.padding(horizontal = 20.dp)) {
        Text(
            text = stringResource(R.string.dont_want_use_anymore)/*"I don’t want to use CureMeGPT anymore"*/,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = FontFamily(Font(R.font.urbanist_medium)),
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = stringResource(R.string.feedback_question)/*"Do you have any feedback for us? (optional)"*/,
            fontSize = 15.sp,
            fontWeight = FontWeight.Normal,
            fontFamily = FontFamily(Font(R.font.urbanist_regular)),
            color = Color(0xFF697383)
        )

        Spacer(modifier = Modifier.height(16.dp))

        var feedback by remember { mutableStateOf("") }




        TextField(
            value = feedback,
            onValueChange = { feedback = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .border(width = 1.dp, color = Color(0xFF697383), shape = RoundedCornerShape(20.dp))
                .background(Color(0xFFFFFFFF), RoundedCornerShape(20.dp))
                .padding(0.dp),   // TextField ka already internal padding hota hai
            placeholder = {
                Text(
                    text = stringResource(R.string.share_feedback_placeholder)/*"Please share your feedback (optional)"*/,
                    color = Color(0xFF697383),
                    fontSize = 14.sp
                )
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFFFFFFF),
                unfocusedContainerColor = Color(0xFFFFFFFF),
                disabledContainerColor = Color(0xFFFFFFFF),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(20.dp),
            maxLines = 5
        )

        }
        Spacer(modifier = Modifier.height(30.dp))

        GradientButton(
            text = stringResource(R.string.delete_account_button)/*"Delete Account"*/,
            onClick = {
                showDialog = true
            }
        )
        Spacer(modifier = Modifier.height(30.dp))
    }

    if (showDialog) {
        LogOutDialog(
            title = "Confirm Delete"/*"Confirm Logout"*/,
            message = "Are you sure you want to delete of your account?"/*"Are you sure you want to log out of your account?"*/,
            cancelText = stringResource(R.string.cancel_button)/*"Cancel"*/,
            confirmText = "Yes, Delete"/*"Yes, Logout"*/,
            onDismiss = {
                showDialog = false
            },
            onConfirm = {
                showDialog = false
                onDeleteClick()
            }
        )
    }
}


@Composable
fun SettingsMenuItem(
    icon: Int,
    iconBoolean: Boolean = true,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Icon Circle Background
            if (iconBoolean) {
                Box(contentAlignment = Alignment.Center) {
                    Image(
                        painter = painterResource(id = icon),
                        contentDescription = title,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }



            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                color = Color(0xFF000000)
            )
        }

        // Arrow Icon
        Image(
            painter = painterResource(id = R.drawable.ic_next_arrow_calender),
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
fun DeleteAccountScreenPreview() {
    val navController = rememberNavController()
    val navController1 = rememberNavController()
    DeleteAccountScreen(navController, navController1)
}