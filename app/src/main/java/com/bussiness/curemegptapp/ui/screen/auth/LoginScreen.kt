package com.bussiness.curemegptapp.ui.screen.auth

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.bussiness.curemegptapp.R
import com.bussiness.curemegptapp.navigation.AppDestination
import com.bussiness.curemegptapp.ui.component.GradientButton
import com.bussiness.curemegptapp.ui.component.GradientHeader
import com.bussiness.curemegptapp.ui.component.GradientIconInputField
import com.bussiness.curemegptapp.util.ValidationUtils
import com.bussiness.curemegptapp.viewmodel.loginviewmodel.LoginViewModel

@Composable
fun LoginScreen(navController: NavHostController,viewModel: LoginViewModel = hiltViewModel()) {

    val context = LocalContext.current
    val state by viewModel.uiState.collectAsState()

    BackHandler() {
        (context as Activity).moveTaskToBack(true)
    }

    Column(modifier = Modifier
            .fillMaxSize()
            .background(Color.White)) {

        // Top Gradient Header
        GradientHeader(heading = stringResource(R.string.welcome_back_title),
            description = stringResource(R.string.welcome_back_description))

        Spacer(modifier = Modifier.height(55.dp))

        // Email Field
        GradientIconInputField(icon = R.drawable.mail_ic,
            placeholder = stringResource(R.string.email_phone_placeholder),
            value = state.emailOrPhone,
            onValueChange = { viewModel.onEmailPhoneChange(it) },
            keyboardType = KeyboardType.Text,)

        Spacer(Modifier.height(20.dp))

        GradientIconInputField(icon = R.drawable.pass_ic,
            placeholder = stringResource(R.string.password_placeholder),
            value = state.password,
            onValueChange = { viewModel.onPasswordChange(it) },
            isPassword = true)

        Spacer(Modifier.height(20.dp))

        // Forgot Password
        Text(
            text = stringResource(R.string.forgot_password),
            modifier = Modifier
                .padding(horizontal = 18.dp)
                .align(Alignment.End)
                .clickable( interactionSource = remember { MutableInteractionSource() },
                    indication = null){
                    navController.navigate("reset?from=auth") },
            fontSize = 17.sp,
            fontFamily = FontFamily(Font(R.font.urbanist_medium)),
            color = Color.Black,
            fontWeight = FontWeight.Medium
        )

        Spacer(Modifier.height(20.dp))

        // Gradient Login Button
        val loginSuccessful = stringResource(R.string.login_success)

        GradientButton(
            text = stringResource(R.string.login_button),
            onClick = {
                viewModel.loginRequest(
                    onError = { msg -> Toast.makeText(context, msg, Toast.LENGTH_SHORT).show() },
                    onSuccess = {
                        Toast.makeText(context, loginSuccessful, Toast.LENGTH_SHORT).show()
                        navController.navigate(AppDestination.MainScreen)
                    }
                )
            }
        )

        Spacer(modifier = Modifier.weight(1f))

        // Bottom Signup
        Row(modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 42.dp),
            horizontalArrangement = Arrangement.Center) {
            Text(
                text = stringResource(R.string.new_here),//"New here?" ,
                color = Color.Black,
                fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                fontWeight = FontWeight.Medium,
                fontSize = 17.sp
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = stringResource(R.string.create_account_link),//"Create an account",
                color = Color(0xFF4338CA),
                fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                fontWeight = FontWeight.Medium,
                fontSize = 17.sp,
                modifier = Modifier.clickable( interactionSource = remember { MutableInteractionSource() },
                    indication = null) { navController.navigate(AppDestination.CreateAccount) }
            )
        }

    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    val navController = rememberNavController()
    LoginScreen(navController = navController)
}