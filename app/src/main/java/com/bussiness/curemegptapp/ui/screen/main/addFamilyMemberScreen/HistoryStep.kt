package com.bussiness.curemegptapp.ui.screen.main.addFamilyMemberScreen

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
import androidx.compose.runtime.derivedStateOf
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

@Composable
fun HistoryStep(
    viewModel: AddFamilyMemberViewModel,
    profileData: ProfileData,
    onNext: () -> Unit
) {

    val conditions = listOf(
        "Diabetes", "Asthma", "Hypertension", "Thyroid",
        "Arthritis", "Heart Disease", "Anxiety", "Depression", "Others"
    )

    var selectedConditions by remember(profileData) {
        mutableStateOf(profileData.chronicConditions.toSet())
    }

    var surgicalHistory by remember(profileData) {
        mutableStateOf(profileData.surgicalHistory)
    }

    var currentMedications by remember(profileData) {
        mutableStateOf(
            if (profileData.currentMedications.isNotEmpty())
                profileData.currentMedications
            else listOf("")
        )
    }

    var currentSupplements by remember(profileData) {
        mutableStateOf(
             if (profileData.currentSupplements.isNotEmpty()) profileData.currentSupplements
                    else listOf("")
        )
    }

    var customCondition by remember(profileData) {
        mutableStateOf(profileData.chronicConditions.find { it !in conditions } ?: "")
    }
    val update by remember {
        derivedStateOf { profileData.chronicConditions.isNotEmpty() }
    }

    val context = LocalContext.current
    fun validateFields(): Boolean {
        if (selectedConditions.isEmpty()) {
            Toast.makeText(context, "Please select at least one chronic condition", Toast.LENGTH_SHORT).show()
            return false
        }

        // Check if "Others" is selected but custom field is empty
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
                    text = stringResource(R.string.chronic_conditions_label),//"Chronic Conditions",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
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
            // ⭐ Custom allergy field — only if Others selected
            if ("Others" in selectedConditions) {

                var showValues = ""
                selectedConditions.forEach { item ->
                    if (!selectedConditions.contains(item)) {
                        showValues += "$item, "
                    }
                }

                if (showValues.isNotEmpty()) {
                    showValues = showValues.substring(0, showValues.length - 2)
                }

                LaunchedEffect(Unit) {
                    customCondition = showValues
                }


                ProfileInputWithoutLabelField(
                    placeholder = stringResource(R.string.write_condition_placeholder),//"Write Condition",
                    value = customCondition,
                    onValueChange = { customCondition = it }
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        ProfileInputMultipleLineField(
            label = stringResource(R.string.surgical_history_label),//"Surgical History (Optional)",
            isImportant = false,
            placeholder = stringResource(R.string.surgical_history_placeholder),//"Any previous surgeries or major medical procedures...",
            value = surgicalHistory,
            onValueChange = { surgicalHistory = it },
            isBold = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column {
            Text(
                text = buildLabelWithOptional(stringResource(R.string.current_medications_label)),//"Current Medications (Optional)",
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
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
                            // ⭐ Sirf first item editable hona chahiye
                            if (index == 0) {
                                val updated = currentMedications.toMutableList()
                                updated[index] = newValue
                                currentMedications = updated
                            }
                        },
                        placeholder = {
                       //     if (index == 0)
                          //      Text(stringResource(R.string.medications_placeholder),/*"Any medications you're currently taking..."*/)
                        //    else
                          //      Text(stringResource(R.string.added_item_placeholder),/*"Added item"*/)  // non editable item placeholder
                            if (index == 0)
                                Text(
                                    text = stringResource(R.string.medications_placeholder),
                                    color = Color(0xFF697383),   // ✅ placeholder black
                                    fontSize = 13.sp,
                                    fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                                    fontWeight = FontWeight.Normal
                                )
                            else
                                Text(
                                    text = stringResource(R.string.added_item_placeholder),
                                    color = Color(0xFF697383),   // ✅ non-editable placeholder bhi black
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
                        enabled = index == 0,    // ⭐ only first row editable
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White,
                            disabledContainerColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent
                        )
                    )

                    if (index == 0) {
                        // ⭐ ADD ICON only for the FIRST item
                        Image(
                            painter = painterResource(id = R.drawable.ic_add_icon),
                            contentDescription = "Add",
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {

                                    // ⭐ Add tabhi hoga jab first field empty na ho
                                    if (currentMedications[0].isNotBlank()) {
                                        //   currentMedications = currentMedications + medication
                                        val updated = currentMedications.toMutableList()

                                        // ⭐ 1. Add a new empty item at bottom
                                        updated.add(currentMedications[0])

                                        // ⭐ 2. Clear first input field
                                        updated[0] = ""

                                        currentMedications = updated
                                    }
                                }
                        )
                    } else {
                        // ⭐ Other items → REMOVE icon
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


            // ⭐ Current Supplements
            Text(
                text = buildLabelWithOptional(stringResource(R.string.current_supplements_label))/*"Current Supplements (Optional)"*/,
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
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
                            // ⭐ Sirf first item editable hona chahiye
                            if (index == 0) {
                                val updated = currentSupplements.toMutableList()
                                updated[index] = newValue
                                currentSupplements = updated
                            }
                        },
                        placeholder = {
                            if (index == 0)
                                Text(
                                    text = stringResource(R.string.supplements_placeholder),
                                    color = Color(0xFF697383),   // ✅ placeholder black
                                    fontSize = 13.sp,
                                    fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                                    fontWeight = FontWeight.Normal
                                )
                            else
                                Text(
                                    text = stringResource(R.string.added_item_placeholder),
                                    color = Color(0xFF697383),   // ✅ non-editable placeholder bhi black
                                    fontSize = 13.sp,
                                    fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                                    fontWeight = FontWeight.Normal
                                )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(30.dp))
                            .border(
                                1.dp,
                                Color(0xFFC3C6CB),
                                RoundedCornerShape(30.dp)
                            ),
                        textStyle = TextStyle(
                            color = Color.Black,
                            fontSize = 13.sp,
                            fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                            fontWeight = FontWeight.Normal
                        ),
                        enabled = index == 0,   // ⭐ only first item editable
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White,
                            disabledContainerColor = Color.White, // ⭐ FIX
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent
                        )
                    )

                    if (index == 0) {
                        // ⭐ ADD icon - first item only
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

                                        // ⭐ Add new item in list
                                        updated.add(currentSupplements[0])

                                        // ⭐ Clear first input
                                        updated[0] = ""

                                        currentSupplements = updated
                                    }
                                }
                        )
                    } else {
                        // ⭐ REMOVE icon - for added items
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


            Spacer(modifier = Modifier.height(24.dp))
        }
        GradientButton(
            text = stringResource(R.string.save_and_continue),//"Save & Continue",
            horizontalPadding = 8.dp,
            onClick = {
                if (validateFields()) {
                val conditionsList = selectedConditions.toMutableList()
                if ("Others" in selectedConditions && customCondition.isNotEmpty()) {
                    conditionsList.add(customCondition)
                }
                    if(update) {

                        viewModel.updateMedicalHistory(
                            chronicConditions = conditionsList,
                            surgicalHistory = surgicalHistory,
                            currentMedications = currentMedications.filter { it.isNotBlank() },
                            currentSupplements = currentSupplements.filter { it.isNotBlank() },
                            onSuccess = {
                                 onNext()
                            },
                            onError = {
                                msg ->
                                Toast.makeText(context,msg,Toast.LENGTH_SHORT).show()
                            }
                        )
                    }else{
                        viewModel.addMedicalHistory(
                            chronicConditions = conditionsList,
                            surgicalHistory = surgicalHistory,
                            currentMedications = currentMedications.filter { it.isNotBlank() },
                            currentSupplements = currentSupplements.filter { it.isNotBlank() },
                            onSuccess = {
                              onNext()
                            },
                            onError = {
                                msg ->
                                Toast.makeText(context,msg,Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}