package com.bussiness.curemegptapp.ui.screen.main.addFamilyMemberScreen

import android.widget.Toast
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bussiness.curemegptapp.R
import com.bussiness.curemegptapp.data.model.ProfileData
import com.bussiness.curemegptapp.ui.component.GradientButton
import com.bussiness.curemegptapp.ui.component.ProfileInputField
import com.bussiness.curemegptapp.ui.component.ProfileInputWithoutLabelField
import com.bussiness.curemegptapp.ui.component.input.CustomPowerSpinner
import com.bussiness.curemegptapp.ui.viewModel.auth.ProfileCompletionViewModel
import com.bussiness.curemegptapp.ui.viewModel.main.AddFamilyMemberViewModel
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun GeneralInfoStep(
    viewModel: AddFamilyMemberViewModel,
    profileData: ProfileData,
    onNext: () -> Unit
) {
    var update by remember { mutableStateOf(profileData.bloodGroup != "Select") }
    val allergyOptions = listOf(
        "Drug", "Food", "Environmental", "Aspirin",
        "Latex", "Ibuprofen", "Shellfish", "Nuts",
        "Penicillin", "Others"
    )
    val bloodOptions = listOf(
        "A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-"
    )

    val initialCustomAllergy = profileData.allergies.filter { it !in allergyOptions && it != "Others" }.joinToString(", ")
    var bloodGroup by remember { mutableStateOf(profileData.bloodGroup) }
    var customAllergy by remember { mutableStateOf(initialCustomAllergy) }
    var selectedAllergies by remember {
        mutableStateOf(
            profileData.allergies.filter { it in allergyOptions }.toSet() +
            if (initialCustomAllergy.isNotEmpty()) setOf("Others") else emptySet()
        )
    }
    var emergencyName by remember { mutableStateOf(profileData.emergencyContactName) }
    var emergencyPhone by remember { mutableStateOf(profileData.emergencyContactPhone) }
    val context = LocalContext.current

    fun validateFields(): Boolean {
        if (bloodGroup.isBlank()) {
            Toast.makeText(context, "Blood group is required", Toast.LENGTH_SHORT).show()
            return false
        }
        // Check if selected blood group is valid (from the options)
        if (bloodGroup !in bloodOptions) {
            Toast.makeText(context, "Blood group is required", Toast.LENGTH_SHORT).show()
            return false
        }

        if (selectedAllergies.isEmpty()) {
            Toast.makeText(context, "Please select at least one allergy", Toast.LENGTH_SHORT).show()
            return false
        }

        if (emergencyPhone.isNotBlank() && !emergencyPhone.matches(Regex("^[0-9]{10}$"))) {
            Toast.makeText(context, "Enter Correct Phone", Toast.LENGTH_SHORT).show()
            return false
        }


        if ("Others" in selectedAllergies && customAllergy.isBlank()) {
            Toast.makeText(context, "Please specify your allergy", Toast.LENGTH_SHORT).show()
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

        Row(modifier = Modifier.padding(start = 12.dp, bottom = 6.dp)) {
            Text(
                text = stringResource(R.string.blood_group_label),
                fontSize = 14.sp,
                color = Color.Black,
                fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                fontWeight = FontWeight.Normal
            )
            Text(
                text = " *",
                color = Color.Red,
                fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                fontWeight = FontWeight.Normal
            )
        }

        CustomPowerSpinner(
            modifier = Modifier.padding(horizontal = 8.dp),
            selectedText = bloodGroup,
            onSelectionChanged = { reason ->
                bloodGroup = reason
            },
            reasons = bloodOptions
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.padding(start = 12.dp, bottom = 6.dp)) {
            Text(
                text = stringResource(R.string.known_allergies_label),
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Black,
                fontFamily = FontFamily(Font(R.font.urbanist_regular))
            )
            Text(
                text = "*",
                color = Color.Red,
                fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                fontWeight = FontWeight.Normal
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 600.dp)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            userScrollEnabled = false
        ) {
            items(allergyOptions) { item ->
                val isSelected = item in selectedAllergies
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(40.dp))
                        .border(
                            1.dp,
                            if (isSelected) Color(0xFF5B4FFF) else Color(0xFFE0E0E0),
                            RoundedCornerShape(40.dp)
                        )
                        .background(
                            if (isSelected) Color(0x205B4FFF) else Color.Transparent
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            selectedAllergies = if (isSelected)
                                selectedAllergies - item
                            else
                                selectedAllergies + item
                        }
                        .padding(vertical = 5.dp)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item,
                        fontSize = 14.sp,
                        fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                        color = if (isSelected) Color(0xFF5B4FFF) else Color(0xFF697383)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if ("Others" in selectedAllergies) {
            ProfileInputWithoutLabelField(
                placeholder = stringResource(R.string.write_allergy_placeholder),
                value = customAllergy,
                onValueChange = { customAllergy = it }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        ProfileInputField(
            label = stringResource(R.string.emergency_name_label),
            isImportant = false,
            placeholder = stringResource(R.string.emergency_name_placeholder),
            value = emergencyName,
            onValueChange = { emergencyName = it },
            isBold = false
        )

        Spacer(modifier = Modifier.height(16.dp))

        ProfileInputField(
            label = stringResource(R.string.emergency_phone_label),
            isImportant = false,
            placeholder = stringResource(R.string.emergency_phone_placeholder),
            value = emergencyPhone,
            onValueChange = { emergencyPhone = it },
            keyboardType = KeyboardType.Number,
            isBold = false
        )

        Spacer(modifier = Modifier.height(24.dp))

        GradientButton(
            horizontalPadding = 8.dp,
            text = stringResource(R.string.save_and_continue),
            onClick = {
                if (validateFields()) {
                    val allergiesList = selectedAllergies.toMutableList()
                    if ("Others" in selectedAllergies && customAllergy.isNotEmpty()) {
                        customAllergy.split(Regex(",\\s*")).filter { it.isNotBlank() }.forEach {
                            if (it !in allergiesList) {
                                allergiesList.add(it)
                            }
                        }
                    }
                    if (update) {
                        viewModel.updateGeneralInfo(
                            bloodGroup = bloodGroup,
                            allergies = allergiesList,
                            emergencyName = emergencyName,
                            emergencyPhone = emergencyPhone,
                            onError = { msg ->
                                Toast.makeText(context, msg.toString(), Toast.LENGTH_SHORT).show()
                            }, onSuccess = {
                                onNext()
                            }
                        )
                    } else {
                        viewModel.addGeneralInfo(
                            bloodGroup = bloodGroup,
                            allergies = allergiesList,
                            emergencyName = emergencyName,
                            emergencyPhone = emergencyPhone,
                            onError = {
                                msg ->
                                Toast.makeText(context, msg.toString(), Toast.LENGTH_SHORT).show()
                            }, onSuccess = {
                                onNext()
                            }
                        )
                    }
                }
            }
        )
    }
}