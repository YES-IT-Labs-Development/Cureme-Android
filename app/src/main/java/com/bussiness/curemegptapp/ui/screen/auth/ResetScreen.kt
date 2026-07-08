package com.bussiness.curemegptapp.ui.screen.auth

import android.widget.Toast
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
import androidx.compose.runtime.rememberCoroutineScope
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
import com.bussiness.curemegptapp.viewmodel.forgotviewmodel.ForgotPasswordViewModel
import com.bussiness.curemegptapp.viewmodel.signupviewmodel.SignUpViewModel

@Composable
fun ResetScreen(navController: NavHostController, fromScreen: String? = "",viewModel: ForgotPasswordViewModel = hiltViewModel()) {

    val context = LocalContext.current
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Top Gradient Header
        GradientHeader( heading = stringResource(R.string.reset_password_title), description = stringResource(R.string.reset_password_description))

        Spacer(modifier = Modifier.height(55.dp))

        // Email Field
        GradientIconInputField(icon = R.drawable.mail_ic,
            placeholder = stringResource(R.string.email_phone_placeholder),
            value = state.emailOrPhone,
            onValueChange = { viewModel.onEmailPhoneChange(it)
                            },
            keyboardType = KeyboardType.Email)

        Spacer(Modifier.height(20.dp))

        GradientButton(
            text = stringResource(R.string.send_code_button),
            onClick = {
                viewModel.forgotOtpRequest(
                    onError = { msg -> Toast.makeText(context, msg, Toast.LENGTH_SHORT).show() },
                    onSuccess = { data->
                        Toast.makeText(context,"Otp :- "+data.otp, Toast.LENGTH_SHORT).show()
                        val route = if (fromScreen == "auth") { "verifyOtp?from=reset&email=${state.emailOrPhone}" } else { "verifyOtp?from=reset1&email=${state.emailOrPhone}" }
                        navController.navigate(route) {
                            if (fromScreen != "auth") {
                                popUpTo("reset?from={from}") {
                                    inclusive = true
                                }
                            }
                        }
                    }
                )
            },
            modifier = Modifier.height(54.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        // BOTTOM: BACK TO LOGIN
        if (fromScreen == "main") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 42.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.back),//"Back",
                    fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                    fontSize = 17.sp,
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        navController.navigateUp()

                    }
                )

            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 42.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.back_to_text),//"Back to",
                    fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = stringResource(R.string.login_link),//"Login",
                    color = Color(0xFF4338CA),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        navController.navigate(AppDestination.Login)
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ResetScreenPreview() {
    val navController = rememberNavController()
    ResetScreen(navController = navController)
}