package com.bussiness.curemegptapp.ui.screen.main.editProfile

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bussiness.curemegptapp.R
import com.bussiness.curemegptapp.data.model.ProfileData
import com.bussiness.curemegptapp.ui.component.GradientButton
import com.bussiness.curemegptapp.ui.component.ProfileInputMultipleLineField
import com.bussiness.curemegptapp.ui.component.ProfileInputWithoutLabelField
import com.bussiness.curemegptapp.ui.component.buildLabelWithOptional
import com.bussiness.curemegptapp.ui.viewModel.auth.ProfileCompletionViewModel
import com.bussiness.curemegptapp.ui.viewModel.main.AddFamilyMemberViewModel
import com.bussiness.curemegptapp.ui.viewModel.main.EditProfileViewModel

@Composable
fun HistoryStep(
    viewModel: EditProfileViewModel,
    profileData: ProfileData,
    onNext: () -> Unit
) {


    val context = LocalContext.current

    // Observe ViewModel state
    val profileState = viewModel.profileFormState

    // Form state
    var selectedConditions by remember { mutableStateOf(setOf<String>()) }
    var customCondition by remember { mutableStateOf("") }
    var surgicalHistory by remember { mutableStateOf("") }
    var currentMedications by remember { mutableStateOf(listOf("")) }
    var currentSupplements by remember { mutableStateOf(listOf("")) }

    val conditions = listOf(
        "Diabetes", "Asthma", "Hypertension", "Thyroid",
        "Arthritis", "Heart Disease", "Anxiety", "Depression", "Others"
    )


    LaunchedEffect(profileState) {
        val initialCustomCondition = profileState.chronicConditions.filter { it !in conditions && it != "Others" }.joinToString(", ")
        customCondition = initialCustomCondition
        selectedConditions = profileState.chronicConditions.filter { it in conditions }.toSet() +
            if (initialCustomCondition.isNotEmpty()) setOf("Others") else emptySet()
        surgicalHistory = profileState.surgicalHistory
        currentMedications =
            if (profileState.currentMedications.isNotEmpty()) profileState.currentMedications
            else listOf("")
        currentSupplements =
            if (profileState.currentSupplements.isNotEmpty()) profileState.currentSupplements
            else listOf("")
    }
    LaunchedEffect(Unit) {
        viewModel.getMedicalHistory {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }

    fun validateFields(): Boolean {
        if (selectedConditions.isEmpty()) {
            Toast.makeText(context, "Please select at least one chronic condition", Toast.LENGTH_SHORT).show()
            return false
        }

        if ("Others" in selectedConditions && customCondition.isBlank()) {
            Toast.makeText(context, "Please specify your condition", Toast.LENGTH_SHORT).show()
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
        Column {

            Row(modifier = Modifier.padding(start = 12.dp, bottom = 6.dp)) {
                Text(
                    text = stringResource(R.string.chronic_conditions_label),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.Black,
                    fontFamily = FontFamily(Font(R.font.urbanist_regular))
                )
                Text(
                    text = " *",
                    color = Color.Red,
                    fontSize = 15.sp,
                    fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                    fontWeight = FontWeight.Normal
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                conditions.forEach { condition ->
                    val isSelected = condition in selectedConditions
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(40.dp))
                            .border(
                                1.dp,
                                if (isSelected) Color(0xFF5B4FFF) else Color(0xFFC3C6CB),
                                RoundedCornerShape(40.dp)
                            )
                            .background(
                                if (isSelected) Color(0x205B4FFF) else Color.Transparent
                            )
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                selectedConditions =
                                    if (isSelected) selectedConditions - condition
                                    else selectedConditions + condition
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = condition,
                            fontSize = 14.sp,
                            fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                            color = if (isSelected) Color(0xFF5B4FFF) else Color(0xFF697383)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if ("Others" in selectedConditions) {
                ProfileInputWithoutLabelField(
                    placeholder = stringResource(R.string.write_condition_placeholder),
                    value = customCondition,
                    onValueChange = { customCondition = it }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        ProfileInputMultipleLineField(
            label = stringResource(R.string.surgical_history_label),
            isImportant = false,
            placeholder = stringResource(R.string.surgical_history_placeholder),
            value = surgicalHistory,
            onValueChange = { surgicalHistory = it },
            isBold = false
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Current Medications
        Column {
            Text(
                text = buildLabelWithOptional(stringResource(R.string.current_medications_label)),
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Black,
                fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                modifier = Modifier.padding(start = 12.dp, bottom = 6.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            currentMedications.forEachIndexed { index, medication ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = medication,
                        onValueChange = { newValue ->
                            if (index == 0) {
                                val updated = currentMedications.toMutableList()
                                updated[index] = newValue
                                currentMedications = updated
                            }
                        },
                        placeholder = {
                            Text(
                                text = if (index == 0)
                                    stringResource(R.string.medications_placeholder)
                                else stringResource(R.string.added_item_placeholder),
                                color = Color(0xFF697383),
                                fontSize = 13.sp,
                                fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                                fontWeight = FontWeight.Normal
                            )
                        },
                        textStyle = TextStyle(
                            color = Color.Black,
                            fontSize = 13.sp,
                            fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                            fontWeight = FontWeight.Normal
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(30.dp))
                            .border(1.dp, Color(0xFFC3C6CB), RoundedCornerShape(30.dp)),
                        enabled = index == 0,
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White,
                            disabledContainerColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )

                    if (index == 0) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_add_icon),
                            contentDescription = "Add",
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    if (currentMedications[0].isNotBlank()) {
                                        val updated = currentMedications.toMutableList()
                                        updated.add(currentMedications[0])
                                        updated[0] = ""
                                        currentMedications = updated
                                    }
                                }
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.ic_remove_icon),
                            contentDescription = "Remove",
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    val updated = currentMedications.toMutableList()
                                    updated.removeAt(index)
                                    currentMedications = updated
                                }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Current Supplements - same logic as medications
            Text(
                text = buildLabelWithOptional(stringResource(R.string.current_supplements_label)),
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Black,
                fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                modifier = Modifier.padding(start = 12.dp, bottom = 6.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

            currentSupplements.forEachIndexed { index, supplement ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = supplement,
                        onValueChange = { newValue ->
                            if (index == 0) {
                                val updated = currentSupplements.toMutableList()
                                updated[index] = newValue
                                currentSupplements = updated
                            }
                        },
                        placeholder = {
                            Text(
                                text = if (index == 0)
                                    stringResource(R.string.supplements_placeholder)
                                else stringResource(R.string.added_item_placeholder),
                                color = Color(0xFF697383),
                                fontSize = 13.sp,
                                fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                                fontWeight = FontWeight.Normal
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(30.dp))
                            .border(1.dp, Color(0xFFC3C6CB), RoundedCornerShape(30.dp)),
                        textStyle = TextStyle(
                            color = Color.Black,
                            fontSize = 13.sp,
                            fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                            fontWeight = FontWeight.Normal
                        ),
                        enabled = index == 0,
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White,
                            disabledContainerColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )

                    if (index == 0) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_add_icon),
                            contentDescription = "Add",
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    if (currentSupplements[0].isNotBlank()) {
                                        val updated = currentSupplements.toMutableList()
                                        updated.add(currentSupplements[0])
                                        updated[0] = ""
                                        currentSupplements = updated
                                    }
                                }
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.ic_remove_icon),
                            contentDescription = "Remove",
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    val updated = currentSupplements.toMutableList()
                                    updated.removeAt(index)
                                    currentSupplements = updated
                                }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        GradientButton(
            text = stringResource(R.string.save_and_continue),
            horizontalPadding = 8.dp,
            onClick = {
                if (validateFields()) {
                    val conditionsList = selectedConditions.toMutableList()
                    if ("Others" in selectedConditions && customCondition.isNotEmpty()) {
                        customCondition.split(Regex(",\\s*")).filter { it.isNotBlank() }.forEach {
                            if (it !in conditionsList) {
                                conditionsList.add(it)
                            }
                        }
                    }

                    viewModel.updateMedicalHistory(
                        chronicConditions = conditionsList,
                        surgicalHistory = surgicalHistory,
                        currentMedications = currentMedications.filter { it.isNotBlank() },
                        currentSupplements = currentSupplements.filter { it.isNotBlank() },{
                            onNext()
                        },{
                            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                        }
                    )

                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}