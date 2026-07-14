package com.bussiness.curemegptapp.ui.screen.main.addFamilyMemberScreen

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
import androidx.compose.runtime.collectAsState
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bussiness.curemegptapp.R
import com.bussiness.curemegptapp.data.model.ProfileData
import com.bussiness.curemegptapp.ui.component.Dropdown1
import com.bussiness.curemegptapp.ui.component.GradientButton
import com.bussiness.curemegptapp.ui.component.ProfileInputField
import com.bussiness.curemegptapp.ui.component.ProfilePhotoPicker
import com.bussiness.curemegptapp.ui.component.UniversalInputField
import com.bussiness.curemegptapp.ui.component.input.CustomPowerSpinner
import com.bussiness.curemegptapp.ui.dialog.CalendarDialog
import com.bussiness.curemegptapp.ui.viewModel.auth.ProfileCompletionViewModel
import com.bussiness.curemegptapp.ui.viewModel.main.AddFamilyMemberViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PersonalInfoStep(
    id:Int =0,
    viewModel: AddFamilyMemberViewModel,
    profileData: ProfileData,
    onNext: () -> Unit
) {

    val profileData1 by viewModel.profileData.collectAsStateWithLifecycle()

    LaunchedEffect(id) {
        Log.d("TEST_ID","Id inside the PersonalInfo is"+id)
        if (id > 0) {
            viewModel.getFamilyMemberProfileData(id)
        }
    }

    val context = LocalContext.current

    val familyRelations = listOf(
        "Father", "Mother", "Grandfather", "Grandmother", "Great-Grandfather",
        "Great-Grandmother", "Brother", "Sister", "Son", "Daughter",
        "Grandson", "Granddaughter", "Husband", "Wife", "Uncle", "Aunt",
        "Nephew", "Niece", "Cousin", "Father-in-law", "Mother-in-law",
        "Brother-in-law", "Sister-in-law", "Son-in-law", "Daughter-in-law"
    )

    var fullName by remember { mutableStateOf("") }
    var contactNumber by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Select") }
    var relation by remember { mutableStateOf("Father") }
    var update by remember { mutableStateOf(false) }
    var heightValue by remember { mutableStateOf("") }
    var heightUnit by remember { mutableStateOf("Cm") }

    var weightValue by remember { mutableStateOf("") }
    var weightUnit by remember { mutableStateOf("Kg") }

    var selectedProfilePhotoUri by remember { mutableStateOf<Uri?>(null) }
    var selectedProfilePhotoName by remember { mutableStateOf("No file chosen") }

    var showEmailOTPDialog by remember { mutableStateOf(false) }
    var showPhoneOTPDialog by remember { mutableStateOf(false) }


    var showDialog by remember { mutableStateOf(false) }



    LaunchedEffect(profileData1) {

        Log.d("TESTING_FAMILY","PROFILE CHANGED: ${profileData1.fullName}")

        if (profileData1.fullName.isNotEmpty()) {
            Log.d("TESTING_FAMILY","HERE INSIDE THE PROFILE DATA"+profileData1.fullName)
            update = true
            fullName = profileData1.fullName
            contactNumber = profileData1.contactNumber
            email = profileData1.email
            dateOfBirth = profileData1.dateOfBirth
            gender = profileData1.gender

            // FIX: Also sync the relation from the API and format to Title Case
            val apiRelation = profileData1.relation ?: "Father"
            relation = (familyRelations.find {
                it.lowercase().replace(" ", "").replace("-", "") == 
                apiRelation.lowercase().replace(" ", "").replace("-", "")
            } ?: apiRelation).lowercase().replaceFirstChar { it.uppercase() }

            val parsedHeight = com.bussiness.curemegptapp.util.CommonUtils.splitValueUnit(profileData1.height, "Cm")
            heightValue = parsedHeight.first
            heightUnit = parsedHeight.second

            val parsedWeight = com.bussiness.curemegptapp.util.CommonUtils.splitValueUnit(profileData1.weight, "Kg")
            weightValue = parsedWeight.first
            weightUnit = parsedWeight.second

            selectedProfilePhotoName = if (profileData1.profileImage.isNotEmpty())
                profileData1.profileImage.substringAfterLast("/")
            else
                "No file chosen"
        }
    }


    val genderOptions = listOf(
        stringResource(R.string.gender_male),
        stringResource(R.string.gender_female),
        stringResource(R.string.gender_other)
    )

    if (showDialog) {
        CalendarDialog(
            onDismiss = {
                showDialog = false
                        },
            allowFutureDates = false,
            onDateApplied = {
                showDialog = false
                dateOfBirth = it.toString()
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
        if (fullName.isBlank()) {
            Toast.makeText(context, "Full name is required", Toast.LENGTH_SHORT).show()
            return false
        }

        if (fullName.length < 2) {
            Toast.makeText(context, "Name must be at least 2 characters", Toast.LENGTH_SHORT).show()
            return false
        }
        if(relation.isEmpty()){
            Toast.makeText(context, "Please Select Relation", Toast.LENGTH_SHORT).show()
            return false
        }

        val phonePattern = Regex("^[0-9]+\$")

        if (contactNumber.isBlank() == false && !phonePattern.matches(contactNumber)) {
            Toast.makeText(context, "Phone number must contain only digits", Toast.LENGTH_SHORT).show()
            return false
        }

        if ( contactNumber.length >0 && contactNumber.length < 10) {
            Toast.makeText(context, "Phone number must be at least 10 digits", Toast.LENGTH_SHORT).show()
            return false
        }

        if(contactNumber.length > 10){
                Toast.makeText(context, "Phone number must be greater than 10 digits", Toast.LENGTH_SHORT).show()
                return false
        }




        val emailPattern = Patterns.EMAIL_ADDRESS
        if (email.isBlank() == false &&!emailPattern.matcher(email).matches()) {
            Toast.makeText(context, "Enter a valid email address", Toast.LENGTH_SHORT).show()
            return false
        }

        if (dateOfBirth.isBlank()) {
            Toast.makeText(context, "Date of birth is required", Toast.LENGTH_SHORT).show()
            return false
        }

        // Gender Validation - Detailed
        if (gender.isBlank()) {
            Toast.makeText(context, "Gender is required", Toast.LENGTH_SHORT).show()
            return false
        }

        // Check if selected gender is valid (from the options)
        if (gender !in genderOptions) {
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
            value = fullName,
            onValueChange = { fullName = it }
        )

        Spacer(modifier = Modifier.height(24.dp))

        ProfileInputField(
            label = ""+stringResource(R.string.contact_number_label)+" (Optional)",
            placeholder = stringResource(R.string.contact_number_placeholder),
            value = contactNumber,
            onValueChange = { contactNumber = it },
            keyboardType = KeyboardType.Phone
        )

        Spacer(modifier = Modifier.height(24.dp))

        ProfileInputField(
            label = ""+ stringResource(R.string.email_label) +" (Optional)",
            placeholder = stringResource(R.string.email_placeholder),
            value = email,
            onValueChange = { email = it },
            keyboardType = KeyboardType.Email
        )

        Spacer(modifier = Modifier.height(24.dp))

        UniversalInputField(
            title = stringResource(R.string.date_of_birth_label),
            isImportant = true,
            placeholder = stringResource(R.string.date_of_birth_placeholder),
            value = dateOfBirth,
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
                selectedText = gender,
                onSelectionChanged = { reason ->
                    gender = reason
                },
                horizontalPadding = 14.dp,
                reasons = genderOptions // Pass the list of options here
            )
        }

        Spacer(modifier = Modifier.height(16.dp))


        Row(modifier = Modifier.padding(horizontal = 9.dp)) {
            Text(
                text = stringResource(R.string.relation),
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
        Spacer(modifier = Modifier.height(9.dp))
        Row(modifier = Modifier.padding(horizontal = 7.dp)) {
            CustomPowerSpinner(
                selectedText = relation,
                onSelectionChanged = { reason ->
                    relation = reason.lowercase().replaceFirstChar { it.uppercase() }
                },
                horizontalPadding = 14.dp,
                reasons = familyRelations // Pass the list of options here
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.padding(horizontal = 7.dp)) {
            Dropdown1(
                label = stringResource(R.string.height_label),
                isImportant = true,
                placeholder = stringResource(R.string.height_placeholder),
                value = heightValue,
                onValueChange = { heightValue = it },
                dropdownItems = listOf(
                    stringResource(R.string.height_unit_cm),
                    stringResource(R.string.height_unit_ft)
                ),
                selectedUnit = heightUnit,
                onUnitSelected = { newUnit ->
                    heightUnit = newUnit
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.padding(horizontal = 7.dp)) {
            Dropdown1(
                label = stringResource(R.string.weight_label),
                isImportant = true,
                placeholder = stringResource(R.string.weight_placeholder),
                value = weightValue,
                onValueChange = { weightValue = it },
                dropdownItems = listOf(
                    stringResource(R.string.weight_unit_kg),
                    stringResource(R.string.weight_unit_lb)
                ),
                selectedUnit = weightUnit,
                onUnitSelected = { newUnit ->
                    weightUnit = newUnit
                }
            )
        }

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
                val height = if (heightValue.isNotBlank()) "$heightValue $heightUnit" else ""
                val weight = if (weightValue.isNotBlank()) "$weightValue $weightUnit" else ""

                    if(update) {
                        viewModel.updatePersonalInfo(
                            context, fullName = fullName,
                            contactNumber = contactNumber,
                            email = email,
                            dateOfBirth = dateOfBirth,
                            gender = gender,
                            height = height,
                            weight = weight,
                            profilePhotoUri = selectedProfilePhotoUri,
                            relationShip = relation, success = {
                                onNext()
                            }
                        )
                    }else{
                        viewModel.addPersonalInfo(
                            context, fullName = fullName,
                            contactNumber = contactNumber,
                            email = email,
                            dateOfBirth = dateOfBirth,
                            gender = gender,
                            height = height,
                            weight = weight,
                            profilePhotoUri = selectedProfilePhotoUri,
                            relationShip = relation, success = {
                                onNext()
                            }
                        )
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}