package com.bussiness.curemegptapp.ui.screen.auth

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.bussiness.curemegptapp.R
import com.bussiness.curemegptapp.navigation.AppDestination
import com.bussiness.curemegptapp.ui.component.GradientButton
import com.bussiness.curemegptapp.ui.component.GradientHeader
import com.bussiness.curemegptapp.ui.component.GradientIconInputField
import com.bussiness.curemegptapp.ui.dialog.SuccessfulDialog
import com.bussiness.curemegptapp.util.ValidationUtils
import com.bussiness.curemegptapp.viewmodel.signupviewmodel.SignUpViewModel

@Composable
fun NewPasswordScreen(navController: NavHostController, from: String, email: String, viewModel: SignUpViewModel = hiltViewModel()) {

    val context = LocalContext.current
    val state by viewModel.uiState.collectAsState()

    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        SuccessfulDialog(
            title = stringResource(R.string.password_updated_title),
            description = "Your password has been updated.",
            onDismiss = {
                showDialog = false
                if (from.equals("reset",true)) {
                    navController.navigate(AppDestination.Login)
                } else {
                    navController.navigateUp()
                }

            },
            onOkClick = {
                showDialog = false
                if (from.equals("reset",true)) {
                    navController.navigate(AppDestination.Login)
                } else {
                    navController.navigateUp()
                }

            }
        )
    }

    Column(modifier = Modifier
            .fillMaxSize()
            .background(Color.White)) {
        // Top Gradient Header
        GradientHeader(heading = stringResource(R.string.new_password_title), description = stringResource(R.string.new_password_description))
        Spacer(modifier = Modifier.height(55.dp))
        GradientIconInputField(
            icon = R.drawable.pass_ic,
            placeholder = stringResource(R.string.password_placeholder),
            value = state.password,
            onValueChange = { viewModel.onPasswordChange(it) },
            isPassword = true
        )
        Spacer(Modifier.height(20.dp))
        GradientIconInputField(
            icon = R.drawable.pass_ic,
            placeholder = stringResource(R.string.confirm_password_placeholder),
            value = state.cnfPassword,
            onValueChange = { viewModel.onCnfPasswordChange(it) },
            isPassword = true
        )
        Spacer(Modifier.height(20.dp))
        GradientButton(
            text = stringResource(R.string.submit_button),
            onClick = {
                viewModel.updatePasswordRequest(
                    email=email,
                    onError = { msg -> Toast.makeText(context, msg, Toast.LENGTH_SHORT).show() },
                    onSuccess = {
                        showDialog = true
                    }
                )
            }
        )
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Preview(showBackground = true)
@Composable
fun NewPasswordScreenPreview() {
    val navController = rememberNavController()
    NewPasswordScreen(navController = navController, "","")
}