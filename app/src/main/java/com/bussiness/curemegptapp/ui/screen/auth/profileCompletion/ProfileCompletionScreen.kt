package com.bussiness.curemegptapp.ui.screen.auth.profileCompletion


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.bussiness.curemegptapp.R
import com.bussiness.curemegptapp.ui.component.TopBarHeader
import com.bussiness.curemegptapp.ui.dialog.AlertCardDialog
import com.bussiness.curemegptapp.ui.viewModel.auth.ProfileCompletionViewModel
import com.bussiness.curemegptapp.viewmodel.personalviewmodel.ProfileCompeteViewModel

import com.bussiness.curemegptapp.navigation.AppDestination

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProfileCompletionScreen(
    navController: NavHostController,
    viewModel: ProfileCompletionViewModel = hiltViewModel(),
    viewModelProfile: ProfileCompeteViewModel = hiltViewModel()
) {
    val currentStep by remember { viewModel.currentStep }
    val profileData by remember { viewModel.profileData }
    var showAlertDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .background(Color(0xFFFFFFFF))) {
        // Top Bar
        TopBarHeader(
            currentStep = currentStep,
            onBackClick = { viewModel.goToPreviousStep() },
            onSkipClick = {
                navController.navigate("openChat?from=auth") {
                    popUpTo(AppDestination.ProfileCompletion) { inclusive = true }
                }
            }
        )

        when (currentStep) {
            0 -> PersonalInfoStep(
                viewModel = viewModelProfile,
                /*profileData = profileData,*/
                onNext = { viewModel.goToNextStep() }
            )
            1 -> GeneralInfoStep(
                viewModel = viewModel,
                profileData = profileData,
                onNext = { viewModel.goToNextStep() }
            )
            2 -> HistoryStep(
                viewModel = viewModel,
                profileData = profileData,
                onNext = { viewModel.goToNextStep() }
            )
            3 -> DocumentsStep(
                viewModel = viewModel,
                profileData = profileData,
                onNext = {

                    showAlertDialog = true  // Dialog show करें
                }
            )
        }
    }
    if (showAlertDialog) {
        AlertCardDialog(
            icon = R.drawable.ic_check,
            title = stringResource(R.string.profile_completed_title),
            message = stringResource(R.string.profile_completed_message),
            confirmText = stringResource(R.string.go_to_ask_ai),
            cancelText = stringResource(R.string.add_member),
            singleLineTitle = true,
            useWideWidth = true,
            onDismiss = {
                showAlertDialog = false
                navController.navigate("addFamilyMember?from=auth")
            },
            onConfirm = {
                showAlertDialog = false
                navController.navigate("openChat?from=auth")
            }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(
    showBackground = true,
    device = Devices.PIXEL_6,
    showSystemUi = true
)
@Composable
fun ProfileCompletionScreenPreview() {
    val navController = rememberNavController()
    ProfileCompletionScreen(navController = navController)
}
