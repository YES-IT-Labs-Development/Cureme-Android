package com.bussiness.curemegptapp.ui.screen.auth.profileCompletion

import android.os.Build
import android.util.Patterns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
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
import com.bussiness.curemegptapp.R
import com.bussiness.curemegptapp.apimodel.loginmodel.UserModel
import com.bussiness.curemegptapp.data.model.ProfileData
import com.bussiness.curemegptapp.navigation.AppDestination
import com.bussiness.curemegptapp.ui.component.Dropdown1
import com.bussiness.curemegptapp.ui.component.GradientButton
import com.bussiness.curemegptapp.ui.component.ProfileInputField
import com.bussiness.curemegptapp.ui.component.ProfilePhotoPicker
import com.bussiness.curemegptapp.ui.component.UniversalInputField
import com.bussiness.curemegptapp.ui.component.input.CustomPowerSpinner
import com.bussiness.curemegptapp.ui.dialog.CalendarDialog
import com.bussiness.curemegptapp.ui.dialog.OTPVerificationDialog
import com.bussiness.curemegptapp.ui.viewModel.auth.ProfileCompletionViewModel
import com.bussiness.curemegptapp.viewmodel.personalviewmodel.ProfileCompeteViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PersonalInfoStep(viewModel: ProfileCompeteViewModel, onNext: () -> Unit) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsState()
    var showCalendarDialog by remember { mutableStateOf(false) }
    var showEmailOTPDialog by remember { mutableStateOf(false) }
    var showPhoneOTPDialog by remember { mutableStateOf(false) }
    var otpType by remember { mutableStateOf("") } // "email" or "phone"

    LaunchedEffect(Unit) {
        viewModel.getPersonalRequest(
            onError = { msg -> Toast.makeText(context, msg, Toast.LENGTH_SHORT).show() },
            onSuccess = {
            })
    }

    if (showCalendarDialog) {
        CalendarDialog(
            onDismiss = { showCalendarDialog = false },
            onDateApplied = {
                showCalendarDialog = false
                viewModel.onDobChange(it)
            },
            allowFutureDates = false
        )
    }

    if (showEmailOTPDialog) {
        OTPVerificationDialog(
            onDismiss = { showEmailOTPDialog = false },
            onVerify = {otp ->

                if(otp.equals(viewModel.currentOtp)){
                    viewModel.onEmailVerified()
                    showEmailOTPDialog= false
                    Toast.makeText(context, "Email verified successfully!", Toast.LENGTH_SHORT).show()
                }

                else{
                    Toast.makeText(context, "Please Enter Valid OTP", Toast.LENGTH_SHORT).show()
                }
            },
            title = "Verify Email",
            message = "Enter the OTP sent to your email",
            onResend = {
                viewModel.verifyEmailPhoneRequest(
                    onSuccess = { otp ->
                        Toast.makeText(context, "OTP sent to your email " + otp, Toast.LENGTH_SHORT).show()
                    },
                    onError = { error ->
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                    },
                    state.emailCopy
                )
            }
        )
    }

    if (showPhoneOTPDialog) {
        OTPVerificationDialog(
            onDismiss = { showPhoneOTPDialog = false },
            onVerify = { otp ->
                          if(otp.equals(viewModel.currentOtp)){
                              viewModel.onPhoneVerified()
                              showPhoneOTPDialog= false

                              Toast.makeText(context, "Phone verified successfully!", Toast.LENGTH_SHORT).show()
                          }else{
                              Toast.makeText(context, "Please Enter Valid OTP", Toast.LENGTH_SHORT).show()
                          }
                       },
            title = "Verify Phone",
            message = "Enter the OTP sent to your phone",
            onResend = {
                viewModel.verifyEmailPhoneRequest(
                    onSuccess = { otp ->
                        Toast.makeText(context, "OTP sent to your phone " + otp, Toast.LENGTH_SHORT).show()
                    },
                    onError = { error ->
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                    },
                    state.phoneCopy
                )
            }
        )
    }

    val profilePhotoPickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            uri?.let {
                viewModel.onImagePathChange(it.toString())
                viewModel.onImageChange(it.lastPathSegment ?: "")
            }
        }
    Column(
        modifier =
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        ProfileInputField(
            label = stringResource(R.string.full_name_label),
            isImportant = true,
            placeholder = stringResource(R.string.full_name_placeholder),
            value = state.name,
            onValueChange = { viewModel.onNameChange(it) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        ProfileInputField(
            label = stringResource(R.string.contact_number_label),
            placeholder = stringResource(R.string.contact_number_placeholder),
            value = state.phoneCopy,
            onValueChange = {
                           viewModel.onPhoneChange(it)
             },
            keyboardType = KeyboardType.Phone,
            enable = state.phone.isBlank(),
            showVerifyBadge = state.phoneCopy.isNotBlank() && state.phone.isBlank(),
            onVerifyClick = {
                if (state.phoneCopy.isNotBlank() && !state.phoneCopy.matches(Regex("^[0-9]{10}$"))) {
                    Toast.makeText(context, "Please enter a valid 10-digit phone number", Toast.LENGTH_SHORT).show()
                    return@ProfileInputField
                }
                viewModel.verifyEmailPhoneRequest(
                    onSuccess = { otp ->
                        showPhoneOTPDialog = true
                        Toast.makeText(context, "OTP sent to your phone " + otp, Toast.LENGTH_SHORT).show()
                    },
                    onError = { error ->
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                    },
                    state.phoneCopy
                )
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        ProfileInputField(
            label = stringResource(R.string.email_label),
            placeholder = stringResource(R.string.email_placeholder),
            value = state.emailCopy,
            onValueChange = {
                            viewModel.onEmailChange(it)
                            },
            keyboardType = KeyboardType.Email,
            enable = state.email.isBlank(),
            showVerifyBadge = state.email.isBlank(),
            onVerifyClick = {

                if(!Patterns.EMAIL_ADDRESS.matcher(state.emailCopy).matches()){
                    Toast.makeText(context, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
                    return@ProfileInputField
                }

                viewModel.verifyEmailPhoneRequest(
                    onSuccess = { otp -> {
                        showEmailOTPDialog = true
                        Toast.makeText(context, "OTP sent to your email " + otp, Toast.LENGTH_SHORT).show()
                    }
                 },
                    onError = { error ->
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                    },
                      state.emailCopy
                )
            }
        )
        Spacer(modifier = Modifier.height(24.dp))

        UniversalInputField(
            title = stringResource(R.string.date_of_birth_label),
            isImportant = true,
            placeholder = stringResource(R.string.date_of_birth_placeholder),
            value = state.dob,
            rightIcon = R.drawable.ic_calender_icon
        ) {
            showCalendarDialog = true
        }
        Spacer(modifier = Modifier.height(16.dp))
        // Gender Row
        Row(modifier = Modifier.padding(horizontal = 9.dp)) {
            Text(
                text = stringResource(R.string.gender_label),
                color = Color.Black,
                fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(start = 3.dp, bottom = 6.dp)
            )
            Text(
                text = " *",
                color = Color.Red,
                fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }
        Row(modifier = Modifier.padding(horizontal = 7.dp)) {
            CustomPowerSpinner(
                selectedText = state.gender,
                onSelectionChanged = { reason ->
                    viewModel.onGenderChange(reason)
                },
                horizontalPadding = 14.dp,
                reasons = listOf(stringResource(R.string.gender_male),
                    stringResource(R.string.gender_female),
                    stringResource(R.string.gender_other))
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.padding(horizontal = 7.dp)) {
            Dropdown1(
                label = stringResource(R.string.height_label),
                isImportant = true,
                placeholder = stringResource(R.string.height_placeholder),
                value = state.height,
                onValueChange = { viewModel.onHeightChange(it) },
                dropdownItems = listOf(stringResource(R.string.height_unit_cm),
                    stringResource(R.string.height_unit_ft)),
                selectedUnit = state.heightType,
                onUnitSelected = { newUnit ->
                    viewModel.onHeightTypeChange(newUnit)
                }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.padding(horizontal = 7.dp)) {
            Dropdown1(
                label = stringResource(R.string.weight_label),
                isImportant = true,
                placeholder = stringResource(R.string.weight_placeholder),
                value = state.weight,
                onValueChange = { viewModel.onWeightChange(it) },
                dropdownItems = listOf(
                    stringResource(R.string.weight_unit_kg),
                    stringResource(R.string.weight_unit_lb)
                ),
                selectedUnit = state.weightType,
                onUnitSelected = { newUnit ->
                    viewModel.onWeightTypeChange(newUnit)
                }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.padding(horizontal = 9.dp)) {
            ProfilePhotoPicker(
                label = stringResource(R.string.profile_photo_label),
                fileName = state.imageProfile,
                onChooseClick = {
                    profilePhotoPickerLauncher.launch(arrayOf("image/*"))
                }
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        GradientButton(
            horizontalPadding = 2.dp,
            text = stringResource(R.string.save_and_continue),
            onClick = {
                viewModel.updatePersonalRequest(
                    context, onError = {
                        msg -> Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                       },
                    onSuccess = {
                        onNext()
                    })
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}
