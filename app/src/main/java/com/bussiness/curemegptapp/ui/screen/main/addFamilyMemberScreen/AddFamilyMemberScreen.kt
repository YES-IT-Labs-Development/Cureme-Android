package com.bussiness.curemegptapp.ui.screen.main.addFamilyMemberScreen

//AddFamilyMemberScreen


import android.os.Build
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
import com.bussiness.curemegptapp.data.model.ProfileData
import com.bussiness.curemegptapp.navigation.AppDestination
import com.bussiness.curemegptapp.ui.component.BloodGroupDropdown
import com.bussiness.curemegptapp.ui.component.DisclaimerBox
import com.bussiness.curemegptapp.ui.component.Dropdown1
import com.bussiness.curemegptapp.ui.component.FileAttachment
import com.bussiness.curemegptapp.ui.component.GenderDropdown
import com.bussiness.curemegptapp.ui.component.GradientButton
import com.bussiness.curemegptapp.ui.component.ProfileInputField
import com.bussiness.curemegptapp.ui.component.ProfileInputMultipleLineField
import com.bussiness.curemegptapp.ui.component.ProfileInputWithoutLabelField
import com.bussiness.curemegptapp.ui.component.ProfilePhotoPicker
import com.bussiness.curemegptapp.ui.component.TopBarHeader
import com.bussiness.curemegptapp.ui.component.UniversalInputField
import com.bussiness.curemegptapp.ui.component.input.CustomPowerSpinner
import com.bussiness.curemegptapp.ui.dialog.AlertCardDialog
import com.bussiness.curemegptapp.ui.dialog.CalendarDialog
import com.bussiness.curemegptapp.ui.dialog.CompleteProfileDialog
import com.bussiness.curemegptapp.ui.dialog.LogOutDialog
import com.bussiness.curemegptapp.ui.dialog.MemberAddedSuccessfullyDialog
import com.bussiness.curemegptapp.ui.viewModel.main.AddFamilyMemberViewModel
import com.bussiness.curemegptapp.ui.viewModel.main.EditProfileViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddFamilyMemberScreen(
    navController: NavHostController, from: String? = "",
    viewModel: AddFamilyMemberViewModel = hiltViewModel()
) {
    val currentStep by remember { viewModel.currentStep }

    val profileData by viewModel.profileData.collectAsState()
    var showAlertDialog by remember { mutableStateOf(false) }
    var showAlertDialog2 by remember { mutableStateOf(false) }
    var showAlertDialog3 by remember { mutableStateOf(false) }
    var showCompleteDialog by remember { mutableStateOf(false) }
    fun isNumber(input: String?): Boolean {
        return input?.toDoubleOrNull() != null
    }


    BackHandler {
        if (currentStep == 0) {
            if (from == "main" || isNumber(from)) {
                navController.popBackStack()
            } else {
                navController.navigate(AppDestination.MainScreen) {
                    popUpTo(0)
                    launchSingleTop = true
                }
            }
        } else {
            viewModel.goToPreviousStep()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
            .background(Color(0xFFFFFFFF))
    ) {
        TopBarHeader(
            currentStep = currentStep,
            onBackClick = {
                if (currentStep == 0) {
                    if (from == "main" || isNumber(from)) {
                        navController.popBackStack()
                    } else {
                        navController.navigate(AppDestination.MainScreen) {
                            popUpTo(0)
                            launchSingleTop = true
                        }
                    }
                } else {
                    viewModel.goToPreviousStep()
                }
            },
            title = if(!isNumber(from))"Add Family Members" else "Edit Family Member Profile",
            skipDisplay = false
        )

        Log.d("TEST_ID", "Value of id inside add family member : $from")

        val isAddMode = !from.isNullOrBlank() && isNumber(from)

        Log.d("TEST_TITLE", "isAddMode: $isAddMode, from: $from")
        when (currentStep) {

            0 -> PersonalInfoStep(
                 id = if (!from.isNullOrBlank() && isNumber(from)) {
                         from.toInt()
                 } else {
                     0
                 },
                viewModel = viewModel,
                profileData = profileData,
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
                isEditMode = isAddMode,
                onNext = {
                    viewModel.submitProfile()
                    showAlertDialog = true  // Dialog show करें
                },
                onBack = {
                    viewModel.goToPreviousStep()
                }
            )
        }
    }

    if (showAlertDialog) {
        MemberAddedSuccessfullyDialog(
            title = "Member Added Successfully",
            message = "Your family member has been added.\n" +
                    "\n" +
                    "Would you like to add another member?",
            cancelText = "Add Another Member",
            confirmText = "Done",
            onDismiss = {
                showAlertDialog = false
                navController.navigateUp()
            },
            onConfirm = {
                showAlertDialog = false
                if (from == "auth") {
                    navController.navigate(AppDestination.MainScreen) {
                        popUpTo(0)
                        launchSingleTop = true
                    }
                } else {
                    navController.navigateUp()
                }
            }
        )

    }

}



@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun AddFamilyMemberScreenPreview() {
    val navController = rememberNavController()
    AddFamilyMemberScreen(navController = navController)
}