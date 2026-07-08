package com.bussiness.curemegptapp.ui.screen.main.editProfile

//EditProfileScreen

import android.os.Build
import android.util.Patterns
import android.widget.Toast
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
import com.bussiness.curemegptapp.ui.dialog.MemberProfileUpdatedDialog
import com.bussiness.curemegptapp.ui.viewModel.main.EditProfileViewModel
import com.bussiness.curemegptapp.util.ValidationUtils

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditProfileScreen(
    navController: NavHostController,
    viewModel: EditProfileViewModel = hiltViewModel()
) {
    val currentStep by remember { viewModel.currentStep }
    val profileData by remember { viewModel.profileData }
    var showAlertDialog by remember { mutableStateOf(false) }
    var showAlertDialog2 by remember { mutableStateOf(false) }
    var showAlertDialog3 by remember { mutableStateOf(false) }
    var showCompleteDialog by remember { mutableStateOf(false) }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .imePadding()
            .background(Color(0xFFFFFFFF))
    ) {
        // Top Bar
        TopBarHeader(
            currentStep = currentStep,
            onBackClick = {
                if (currentStep == 0) {
                    navController.navigateUp()
                } else {
                    viewModel.goToPreviousStep()
                }
            },
            title = stringResource(R.string.edit_profile),
            skipDisplay = false
        )

        when (currentStep) {
            0 -> PersonalInfoStep(
                viewModel = viewModel,
                profileData = profileData,
                onNext = {

                        viewModel.goToNextStep()

                }
            )

            1 -> GeneralInfoStep(
                viewModel = viewModel,
                profileData = profileData,
                onNext = {

                        viewModel.goToNextStep()

                }
            )

            2 -> HistoryStep(
                viewModel = viewModel,
                profileData = profileData,
                onNext = {

                        viewModel.goToNextStep()

                }
            )

            3 -> DocumentsStep(
                viewModel = viewModel,
                profileData = profileData,
                onNext = {

                        viewModel.submitProfile()
                        showAlertDialog = true

                }
            )
        }
    }

/*    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding() .imePadding()
            .background(Color(0xFFFFFFFF))
    ) {
        // Top Bar
        TopBarHeader(
            currentStep = currentStep,
            onBackClick = {      if (currentStep == 0) {
                navController.navigateUp()
            } else {
                viewModel.goToPreviousStep()
            } },
            title = stringResource(R.string.edit_profile)*//*"Edit Profile"*//*,
            skipDisplay = false
        )

        when (currentStep) {
            0 -> PersonalInfoStep(
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
                onNext = {
                    viewModel.submitProfile()
                    showAlertDialog = true  // Dialog show करें
                }
            )
        }
    }*/


    if (showAlertDialog) {
        /*       AlertCardDialog(
                   icon = R.drawable.ic_check,
                   title = "Profile Setup Completed!",
                   message = "You can now add your family members or start asking AI for help.",
                   confirmText = "Go to Ask AI",
                   cancelText = "Add Member",
                   onDismiss = {
                       showAlertDialog = false
                       navController.navigateUp()
                               },
                   onConfirm = {  showAlertDialog = false
                       showAlertDialog2 = true
                   navController.navigateUp()
                   }
               )*/

        MemberProfileUpdatedDialog(
            title = "Profile Updated \nSuccessfully"/*"Member’s Profile Updated Successfully"*/,
            description = "Your profile has been updated."/*"Member’s Profile has been updated."*/,
            onDismiss = { showAlertDialog = false },
            onOkClick = {
                showAlertDialog = false
                navController.navigateUp()
            }
        )

    }
    if (showAlertDialog2) {
        AlertCardDialog(
            icon = R.drawable.ic_medication,
            title = "Medication Reminder",
            message = "Time to take your",
            highlightText = "Lisinopril 10mg.",
            warningText = "Don't forget to take it with food",
            cancelText = "Snooze",
            confirmText = "Mark As Taken",
            onDismiss = { showAlertDialog2 = false },
            onConfirm = {
                showAlertDialog2 = false
                showAlertDialog3 = true
            }
        )

    }

    if (showAlertDialog3) {
        AlertCardDialog(
            icon = R.drawable.ic_appointment,
            title = "Dental Cleaning Today",
            message = "Don't forget your dental cleaning appointment ",
            highlightText = "today at 2:00 PM with Dr. Sarah Johnson.",
            cancelText = "Remind Me Later",
            confirmText = "Got It",
            onDismiss = { showAlertDialog3 = false },
            onConfirm = {
                showAlertDialog3 = false
                showCompleteDialog = true
            }
        )

        if (showCompleteDialog) {
            CompleteProfileDialog(
                icon = R.drawable.ic_person_complete_icon,
                title = "Complete Your Profile",
                checklist = listOf(
                    "Faster AI answers tailored to you",
                    "Safer medication & allergy checks",
                    "Quicker reminders & records Complete Now"
                ),
                cancelText = "Remind Me Later",
                confirmText = "Complete Now",
                skipText = "Skip for Now",

                onDismiss = {
                    showCompleteDialog = false
                },
                onConfirm = {
                    showCompleteDialog = false
                    navController.navigate(AppDestination.MainScreen)
                },
                onSkip = {
                    showCompleteDialog = false
                    // TODO – "Skip for Now"
                }
            )
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun EditProfileScreenPreview() {
    val navController = rememberNavController()
    EditProfileScreen(navController = navController)
}