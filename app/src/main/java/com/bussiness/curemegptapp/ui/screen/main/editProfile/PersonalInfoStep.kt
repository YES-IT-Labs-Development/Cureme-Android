package com.bussiness.curemegptapp.ui.screen.main.editProfile

import android.net.Uri
import android.os.Build
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.bussiness.curemegptapp.R
import com.bussiness.curemegptapp.data.model.ProfileData
import com.bussiness.curemegptapp.ui.component.Dropdown1
import com.bussiness.curemegptapp.ui.component.GradientButton
import com.bussiness.curemegptapp.ui.component.ProfileInputField
import com.bussiness.curemegptapp.ui.component.ProfilePhotoPicker
import com.bussiness.curemegptapp.ui.component.UniversalInputField
import com.bussiness.curemegptapp.ui.component.input.CustomPowerSpinner
import com.bussiness.curemegptapp.ui.dialog.CalendarDialog
import com.bussiness.curemegptapp.ui.dialog.OTPVerificationDialog
import com.bussiness.curemegptapp.ui.viewModel.auth.ProfileCompletionViewModel
import com.bussiness.curemegptapp.ui.viewModel.main.AddFamilyMemberViewModel
import com.bussiness.curemegptapp.ui.viewModel.main.EditProfileViewModel
import com.bussiness.curemegptapp.util.CommonUtils
import com.bussiness.curemegptapp.util.UriToRequestBody
import com.google.android.material.color.utilities.MaterialDynamicColors.onError

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PersonalInfoStep(
    viewModel: EditProfileViewModel,
    profileData: ProfileData,
    onNext: () -> Unit
) {

    val profile = viewModel.profileFormState
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var showEmailOTPDialog by remember { mutableStateOf(false) }
    var showPhoneOTPDialog by remember { mutableStateOf(false) }
    val genderOptions = listOf(
        stringResource(R.string.gender_male),
        stringResource(R.string.gender_female),
        stringResource(R.string.gender_other)
    )


    LaunchedEffect(Unit) {
        viewModel.getProfileData {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }

    if (showDialog) {
        CalendarDialog(
            onDismiss = { showDialog = false },
            onDateApplied = {
                showDialog = false
                viewModel.updateField {
                    copy(dateOfBirth = it.toString())
                }
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
                    profile.emailCopy
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
                    profile.phoneCopy
                )
            }
        )
    }



    val (heightValue, heightUnit) = CommonUtils.splitValueUnit(profile.height, "Cm")
    val (weightValue, weightUnit) = CommonUtils.splitValueUnit(profile.weight, "Kg")

    var selectedProfilePhotoUri by remember { mutableStateOf<Uri?>(null) }

    var selectedProfilePhotoName by remember(profile.profileImage) {
        mutableStateOf(
            if (!profile.profileImage.isNullOrBlank()) {
                profile.profileImage.substringAfterLast("/")
            } else {
                "No file chosen"
            }
        )
    }

    val profilePhotoPickerLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            uri?.let {
                selectedProfilePhotoUri = it
                selectedProfilePhotoName = it.lastPathSegment ?: ""
            }
        }

    fun openProfilePhotoPicker() {
        profilePhotoPickerLauncher.launch(arrayOf("image/*"))
    }

    fun validateFields(): Boolean {

        if(!profile.emailVerify){
            Toast.makeText(context,"Please verify your email before updating profile",Toast.LENGTH_LONG).show()
            return false
        }
        if (profile.phoneCopy.isNotBlank()) {
            if(!profile.phoneVerify){
                Toast.makeText(context,"Please verify your phone before updating profile",Toast.LENGTH_SHORT).show()
                return false
            }
            val phonePattern = Regex("^[0-9]+$")
            if (!phonePattern.matches(profile.phoneCopy)) {
                Toast.makeText(context, "Phone number must contain only digits", Toast.LENGTH_SHORT).show()
                return false
            }

            if (profile.phoneCopy.length < 10) {
                Toast.makeText(context, "Phone number must be at least 10 digits", Toast.LENGTH_SHORT).show()
                return false
            }
        }
        if (profile.fullName.isBlank()) {
            Toast.makeText(context, "Full name is required", Toast.LENGTH_SHORT).show()
            return false
        }

        if (profile.fullName.length < 2) {
            Toast.makeText(context, "Name must be at least 2 characters", Toast.LENGTH_SHORT).show()
            return false
        }

        if (profile.email.isBlank()) {
            Toast.makeText(context, "Email is required", Toast.LENGTH_SHORT).show()
            return false
        }

        val emailPattern = Patterns.EMAIL_ADDRESS
        if (!emailPattern.matcher(profile.email).matches()) {
            Toast.makeText(context, "Enter a valid email address", Toast.LENGTH_SHORT).show()
            return false
        }

        if (profile.dateOfBirth.isBlank()) {
            Toast.makeText(context, "Date of birth is required", Toast.LENGTH_SHORT).show()
            return false
        }

        if (profile.gender.isBlank() || profile.gender !in genderOptions) {
            Toast.makeText(context, "Gender is required", Toast.LENGTH_SHORT).show()
            return false
        }

        if (heightValue.isBlank()) {
            Toast.makeText(context, "Height is required", Toast.LENGTH_SHORT).show()
            return false
        }

        if (weightValue.isBlank()) {
            Toast.makeText(context, "Weight is required", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {

        ProfileInputField(
            label = stringResource(R.string.full_name_label),
            isImportant = true,
            placeholder = stringResource(R.string.full_name_placeholder),
            value = profile.fullName,
            onValueChange = {
                viewModel.updateField {
                    copy(fullName = it)
                }
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        ProfileInputField(
            label = stringResource(R.string.contact_number_label),
            placeholder = stringResource(R.string.contact_number_placeholder),
            value = profile.phoneCopy,
            keyboardType = KeyboardType.Phone,

            onValueChange = {
                viewModel.onPhoneChange(it)
            },
            enable = profile.contactNumber.isBlank(),
            showVerifyBadge = profile.phoneCopy.isNotBlank() && profile.contactNumber.isBlank(),
            onVerifyClick = {
                if (profile.phoneCopy.isNotBlank() && !profile.phoneCopy.matches(Regex("^[0-9]{10}$"))) {
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
                    profile.phoneCopy
                )
            }

        )

        Spacer(modifier = Modifier.height(24.dp))

        ProfileInputField(
            label = stringResource(R.string.email_label),
            placeholder = stringResource(R.string.email_placeholder),
            value = profile.emailCopy,
            keyboardType = KeyboardType.Email,
            onValueChange = {
                viewModel.onEmailChange(it)
            },
            enable = profile.email.isBlank(),
            showVerifyBadge = profile.email.isBlank(),
            onVerifyClick = {
                if(!Patterns.EMAIL_ADDRESS.matcher(profile.emailCopy).matches()){
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
                    profile.emailCopy
                )
            }


        )

        Spacer(modifier = Modifier.height(24.dp))

        UniversalInputField(
            title = stringResource(R.string.date_of_birth_label),
            isImportant = true,
            placeholder = stringResource(R.string.date_of_birth_placeholder),
            value = profile.dateOfBirth,
            rightIcon = R.drawable.ic_calender_icon
        ) {
            showDialog = true
        }

        Spacer(modifier = Modifier.height(16.dp))

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
                selectedText = profile.gender,
                onSelectionChanged = {
                    viewModel.updateField {
                        copy(gender = it)
                    }
                },
                horizontalPadding = 14.dp,
                reasons = genderOptions
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
         Log.d("TESTING_HEIGHT_WEIGHT", "Height Value: $heightValue, Height Unit: $heightUnit")
         Log.d("TESTING_HEIGHT_WEIGHT", "Weight Value: $weightValue, Weight Unit: $weightUnit")
        Dropdown1(
            label = stringResource(R.string.height_label),
            isImportant = true,
            placeholder = stringResource(R.string.height_placeholder),
            value = heightValue,
            dropdownItems = listOf(
                stringResource(R.string.height_unit_cm),
                stringResource(R.string.height_unit_ft)
            ),
            selectedUnit = heightUnit,
            onValueChange = {
                viewModel.updateField {
                    copy(height = "$it $heightUnit")
                }
            },
            onUnitSelected = {
                viewModel.updateField {
                    copy(height = "$heightValue $it")
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Dropdown1(
            label = stringResource(R.string.weight_label),
            isImportant = true,
            placeholder = stringResource(R.string.weight_placeholder),
            value = weightValue,
            dropdownItems = listOf(
                stringResource(R.string.weight_unit_kg),
                stringResource(R.string.weight_unit_lb)
            ),
            selectedUnit = weightUnit,
            onValueChange = {
                viewModel.updateField {
                    copy(weight = "$it $weightUnit")
                }
            },
            onUnitSelected = {
                viewModel.updateField {
                    copy(weight = "$weightValue $it")
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.padding(horizontal = 9.dp)) {
            ProfilePhotoPicker(
                label = stringResource(R.string.profile_photo_label),
                fileName = selectedProfilePhotoName,
                onChooseClick = { openProfilePhotoPicker() }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        GradientButton(
            horizontalPadding = 2.dp,
            text = stringResource(R.string.save_and_continue),
            onClick = {

                if (validateFields()) {

                    viewModel.updatePersonalInfo(
                        fullName = profile.fullName,
                        contactNumber = profile.phoneCopy,
                        email = profile.emailCopy,
                        dateOfBirth = profile.dateOfBirth,
                        gender = profile.gender,
                        height = profile.height,
                        weight = profile.weight,
                        profilePhotoUri = selectedProfilePhotoUri,
                        profileMultipart =  selectedProfilePhotoUri?.let { uri ->
                            UriToRequestBody.uriToMultipart(context,uri, "profile_image")
                        },
                        {
                            onNext()
                        },{
                            Toast.makeText(context, "Failed to upload profile", Toast.LENGTH_SHORT).show()
                        }
                    )


                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))






    }


}