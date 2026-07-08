package com.bussiness.curemegptapp.ui.screen.auth


import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.bussiness.curemegptapp.viewmodel.signupviewmodel.SignUpViewModel

@Composable
fun CreateAccountScreen(navController: NavHostController,viewModel: SignUpViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize().imePadding()
            .background(Color.White).verticalScroll(rememberScrollState())
    ) {

        GradientHeader( heading = stringResource(R.string.create_account_title),
            description = stringResource(R.string.create_account_description))
        Spacer(modifier = Modifier.height(55.dp))
        GradientIconInputField(icon = R.drawable.profile_ic,
            placeholder = "Full Name",
            value = state.name,
            onValueChange = {viewModel.onNameChange(it)})
        Spacer(Modifier.height(20.dp))
        GradientIconInputField(icon = R.drawable.mail_ic,
            placeholder = "Email/Phone",
            value = state.emailOrPhone,
            onValueChange = {viewModel.onEmailPhoneChange(it)},
            keyboardType = KeyboardType.Email)
        Spacer(Modifier.height(20.dp))
        GradientIconInputField(icon = R.drawable.pass_ic,
            placeholder = stringResource(R.string.password_placeholder),
            value = state.password,
            onValueChange = {viewModel.onPasswordChange(it)},
            isPassword = true)
        Spacer(Modifier.height(20.dp))
        GradientIconInputField(icon = R.drawable.pass_ic,
            placeholder = stringResource(R.string.confirm_password_placeholder),
            value = state.cnfPassword,
            onValueChange = {viewModel.onCnfPasswordChange(it)},
            isPassword = true)
        Spacer(Modifier.height(20.dp))
        GradientButton( text = stringResource(R.string.sign_up_button),
            onClick = {
                viewModel.registerRequest(
                    onError = { msg -> Toast.makeText(context, msg, Toast.LENGTH_SHORT).show() },
                    onSuccess = { data->
                        Toast.makeText(context,"Otp :- "+data.user?.otp, Toast.LENGTH_SHORT).show()
                        navController.navigate("verifyOtp?from=create&email=${state.emailOrPhone}")
                    }
                )
            }
        )
        Spacer(modifier = Modifier.weight(1f))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 30.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.already_have_account),//"Already have an account?" ,
                fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                fontWeight = FontWeight.Medium,
                fontSize = 17.sp
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = stringResource(R.string.login_link), //" Login",
                color = Color(0xFF4338CA),
                fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                fontWeight = FontWeight.Medium,
                fontSize = 17.sp,
                modifier = Modifier.clickable( interactionSource = remember { MutableInteractionSource() },
                    indication = null) { navController.navigate(AppDestination.Login) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreateAccountScreenPreview() {
    val navController = rememberNavController()
    CreateAccountScreen(navController = navController)
}