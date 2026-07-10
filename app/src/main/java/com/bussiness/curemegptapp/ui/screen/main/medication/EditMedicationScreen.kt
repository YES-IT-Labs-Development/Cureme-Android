package com.bussiness.curemegptapp.ui.screen.main.medication

//EditMedicationScreen


import android.net.Uri
import android.os.Build
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.bussiness.curemegptapp.R
import com.bussiness.curemegptapp.ui.component.CancelButton
import com.bussiness.curemegptapp.ui.component.ContinueButton
import com.bussiness.curemegptapp.ui.component.ProfileInputMultipleLineField2
import com.bussiness.curemegptapp.ui.component.ProfileInputSmallField
import com.bussiness.curemegptapp.ui.component.ProfilePhotoPicker
import com.bussiness.curemegptapp.ui.component.RoundedCustomCheckbox
import com.bussiness.curemegptapp.ui.component.TopBarHeader1
import com.bussiness.curemegptapp.ui.component.UniversalInputField
import com.bussiness.curemegptapp.ui.component.input.CustomMultiSelectPowerSpinner
import com.bussiness.curemegptapp.ui.component.input.CustomPowerSpinner
import com.bussiness.curemegptapp.ui.dialog.CalendarDialog
import com.bussiness.curemegptapp.ui.dialog.SuccessfulDialog
import com.bussiness.curemegptapp.ui.dialog.TimePickerDialog
import com.bussiness.curemegptapp.util.AppConstant
import com.bussiness.curemegptapp.util.UriToRequestBody
import com.bussiness.curemegptapp.viewmodel.medication.EditMedicationViewModel
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.text.SimpleDateFormat
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditMedicationScreen(
    navController: NavHostController,
    medicationId :Int,
    viewModel : EditMedicationViewModel = hiltViewModel()
) {

    val context = LocalContext.current

    val uiState by viewModel.uiState.collectAsState()
    uiState.error?.let {
        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
    }

    LaunchedEffect(medicationId) {
        viewModel.getMedicationDetail(medicationId)
        viewModel.getFamilyMembers()
    }

    var dateOfBirth by remember { mutableStateOf("") }


    var currentReminder by remember { mutableStateOf("") }

    var medicationName by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var selectedMyself by remember { mutableStateOf("") }
    var selectedMedicationType by remember { mutableStateOf("") }
    var selectFrequency by remember { mutableStateOf("") }
    var selectedDays by remember { mutableStateOf(setOf<String>()) }
    var selectDayName by remember { mutableStateOf("") }
    LaunchedEffect(selectedDays) {
        selectDayName = if (selectedDays.isEmpty()) "" else selectedDays.joinToString(", ")
    }
    var newPhotoUploaded by remember { mutableStateOf(false)}
    var description by remember { mutableStateOf("") }

    var currentReminderTime by remember { mutableStateOf(listOf("")) }
    var showTimePickerDialog by remember { mutableStateOf(false) }

    val myselfOptions by viewModel.memberOption.collectAsState()
    val selectedMedicationTypeOptions = listOf("Medicine", "Supplements")
    val selectDayNameOptions = listOf(
        "Sunday", "Monday", "Tuesday", "Wednesday",
        "Thursday", "Friday", "Saturday"
    )
    val selectFrequencyOptions = listOf("Daily", "Alternate", "Weekly")

    var showDialog by remember { mutableStateOf(false) }
    var showDialog1 by remember { mutableStateOf(false) }
    var showDialogSuccessFully by remember { mutableStateOf(false) }
    var uploadedFiles by remember { mutableStateOf<Uri?>(null) }
    var checked by remember { mutableStateOf(true) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            newPhotoUploaded = true
            uploadedFiles = uri
        }
    }


    LaunchedEffect(uiState.medication) {

        uiState.medication?.let { medication ->
            medicationName = medication.medication_name ?: ""
            dosage = medication.dosage ?: ""
            startDate = medication.start_date ?: ""
            endDate = medication.end_date ?: ""
            selectedMedicationType = medication.medication_type ?: ""
            selectedMyself = medication.medication_for_whom ?: ""
            selectFrequency = medication.frequency ?: ""
            val daysFromApi = medication.days ?: ""
            selectedDays = if (daysFromApi.isNotBlank()) {
                daysFromApi.split(",").map { it.trim() }.toSet()
            } else {
                emptySet()
            }
            description = medication.notes ?: ""
            val uri: Uri = Uri.parse(  AppConstant.IMAGE_BASE_URL+medication.prescription_docs)
            uploadedFiles = uri
            currentReminderTime =
                if (medication.reminder_time.isNullOrEmpty()) listOf("")
                else medication.reminder_time.map { convert24To12Hour(it) }
        }

    }




    fun formatTimeInput(input: String): String {
        
        val digits = input.filter { it.isDigit() }
        
        val limitedDigits = digits.take(6)
        
        return when (limitedDigits.length) {
            0 -> ""
            1 -> "0$limitedDigits:"
            2 -> "$limitedDigits:"
            3 -> "${limitedDigits.substring(0, 2)}:${limitedDigits[2]}"
            4 -> "${limitedDigits.substring(0, 2)}:${limitedDigits.substring(2, 4)}:"
            5 -> "${limitedDigits.substring(0, 2)}:${limitedDigits.substring(2, 4)}:${limitedDigits[4]}"
            6 -> {
                // Format as HH:MM:SS
                "${limitedDigits.substring(0, 2)}:${limitedDigits.substring(2, 4)}:${limitedDigits.substring(4, 6)}"
            }
            else -> input
        }
    }

    fun getFileName(uri: Uri): String {
        return uri.lastPathSegment ?: "file"
    }

    fun validateFields(): Boolean {
        // Debug: Print current reminder times
        println("DEBUG: Current reminder times: $currentReminderTime")
        println("DEBUG: Valid reminder times: ${currentReminderTime.filter { it.trim().isNotBlank() }}")



        // Validate Medication Type Selection
        if (selectedMedicationType == "Select Appointment Type" || selectedMedicationType.isBlank()) {
            Toast.makeText(context, "Please select a medication type", Toast.LENGTH_SHORT).show()
            return false
        }

        // Validate Medication Name
        if (medicationName.isBlank()) {
            Toast.makeText(context, "Medication name is required", Toast.LENGTH_SHORT).show()
            return false
        }

        val trimmedMedicationName = medicationName.trim()
        if (trimmedMedicationName.length < 2) {
            Toast.makeText(context, "Medication name should be at least 2 characters", Toast.LENGTH_SHORT).show()
            return false
        }

        // Validate Dosage
        if (dosage.isBlank()) {
            Toast.makeText(context, "Dosage is required", Toast.LENGTH_SHORT).show()
            return false
        }

        val trimmedDosage = dosage.trim()
        if (trimmedDosage.length < 2) {
            Toast.makeText(context, "Please enter a valid dosage (e.g., 500mg)", Toast.LENGTH_SHORT).show()
            return false
        }

        // Validate Frequency Selection
        if (selectFrequency == "Select Frequency" || selectFrequency.isBlank()) {
            Toast.makeText(context, "Please select frequency", Toast.LENGTH_SHORT).show()
            return false
        }

        // Validate Day Selection if Weekly
        if (selectFrequency == "Weekly" && (selectDayName == "Select Frequency" || selectDayName.isBlank())) {
            Toast.makeText(context, "Please select a day for weekly schedule", Toast.LENGTH_SHORT).show()
            return false
        }

        // Validate Reminder Times
        val validReminderTimes = currentReminderTime.filter { it.trim().isNotBlank() }
        if (validReminderTimes.isEmpty()) {
            Toast.makeText(context, "Please add at least one reminder time", Toast.LENGTH_SHORT).show()
            return false
        }

        // Validate each reminder time format - HH:MM AM/PM format
        for (reminderTime in validReminderTimes) {
            val trimmedTime = reminderTime.trim()
            println("DEBUG: Validating time: '$trimmedTime'")

            // 12-hour AM/PM format regex
            val timeRegex = Regex("^(0[1-9]|1[0-2]):[0-5][0-9]\\s?(AM|PM)\$", RegexOption.IGNORE_CASE)

            if (!timeRegex.matches(trimmedTime)) {
                println("DEBUG: Time '$trimmedTime' does not match regex")
                Toast.makeText(context, "Please select time in HH:MM AM/PM format (e.g., 08:30 AM)", Toast.LENGTH_SHORT).show()
                return false
            }

            // Additional validation for proper time ranges
            val parts = trimmedTime.substring(0, 5).split(":")
            if (parts.size == 2) {
                val hour = parts[0].toIntOrNull() ?: 0
                val minute = parts[1].toIntOrNull() ?: 0

                if (hour !in 1..12) {
                    Toast.makeText(context, "Hour must be between 01 and 12", Toast.LENGTH_SHORT).show()
                    return false
                }

                if (minute !in 0..59) {
                    Toast.makeText(context, "Minute must be between 00 and 59", Toast.LENGTH_SHORT).show()
                    return false
                }
            }
        }

        // Validate Start Date
        if (startDate.isBlank()) {
            Toast.makeText(context, "Start date is required", Toast.LENGTH_SHORT).show()
            return false
        }

        // Validate Start Date format
        val dateRegex = Regex("^\\d{2}-\\d{2}-\\d{4}\$")
        if (!dateRegex.matches(startDate)) {
            Toast.makeText(context, "Please enter start date in MM-DD-YYYY format", Toast.LENGTH_SHORT).show()
            return false
        }

        // Validate End Date (Optional but if provided, validate format and that it's after start date)
        if (endDate.isNotBlank()) {
            if (!dateRegex.matches(endDate)) {
                Toast.makeText(context, "Please enter end date in MM-DD-YYYY format", Toast.LENGTH_SHORT).show()
                return false
            }

            // Validate that end date is after start date
            try {
                val dateFormat = SimpleDateFormat("MM-dd-yyyy", Locale.getDefault())
                val startDateObj = dateFormat.parse(startDate)
                val endDateObj = dateFormat.parse(endDate)

                if (startDateObj != null && endDateObj != null && endDateObj.before(startDateObj)) {
                    Toast.makeText(context, "End date must be after start date", Toast.LENGTH_SHORT).show()
                    return false
                }
            } catch (e: Exception) {
                // Date parsing failed
            }
        }

        // Validate Notes/Description (Optional)
        if (description.isNotBlank()) {
            val trimmedDescription = description.trim()
            if (trimmedDescription.length < 5) {
                Toast.makeText(context, "Notes should be at least 5 characters if provided", Toast.LENGTH_SHORT).show()
                return false
            }
        }


        if (validReminderTimes.isNotEmpty() && !checked) {
            Toast.makeText(context, "Please enable reminders for the set times", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding().imePadding()
            .verticalScroll(rememberScrollState())
            .background(Color(0xFFFFFFFF))
    ) {

        TopBarHeader1(
            title = stringResource(R.string.edit_medication_title),
            onBackClick = { navController.popBackStack() }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 19.dp, vertical = 30.dp)
        ) {

            Text(
                text = stringResource(R.string.for_family_member_label),
                fontSize = 15.sp,
                color = Color.Black,
                fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                fontWeight = FontWeight.Normal
            )

            Spacer(Modifier.height(8.dp))

            CustomPowerSpinner(
                selectedText = selectedMyself,
                onSelectionChanged = { reason ->
                    viewModel.updateForWhomId(reason)
                    selectedMyself = reason
                },
                horizontalPadding = 24.dp,
                reasons = myselfOptions
            )

            Spacer(Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.medication_type_label),
                fontSize = 15.sp,
                color = Color.Black,
                fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                fontWeight = FontWeight.Normal
            )

            Spacer(Modifier.height(8.dp))

            CustomPowerSpinner(
                selectedText = selectedMedicationType,
                onSelectionChanged = { reason ->
                    selectedMedicationType = reason
                },
                horizontalPadding = 24.dp,
                reasons = selectedMedicationTypeOptions
            )

            Row(Modifier.padding(vertical = 24.dp)) {
                ProfileInputSmallField(
                    label = stringResource(R.string.medication_name_label),
                    isImportant = false,
                    placeholder = stringResource(R.string.medication_name_placeholder),
                    value = medicationName,
                    onValueChange = { medicationName = it },
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(5.dp))

                ProfileInputSmallField(
                    label = stringResource(R.string.dosage_label),
                    isImportant = false,
                    placeholder = stringResource(R.string.dosage_placeholder),
                    value = dosage,
                    onValueChange = { dosage = it },
                    modifier = Modifier.weight(1f)
                )
            }

            Text(
                text = stringResource(R.string.frequency_label),
                fontSize = 15.sp,
                color = Color.Black,
                fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                fontWeight = FontWeight.Normal
            )

            Spacer(Modifier.height(8.dp))

            CustomPowerSpinner(
                selectedText = selectFrequency,
                onSelectionChanged = { reason ->
                    selectFrequency = reason
                    if (reason != "Weekly") {
                        selectedDays = emptySet()
                    }
                },
                horizontalPadding = 24.dp,
                reasons = selectFrequencyOptions
            )

            if (selectFrequency == "Weekly") {
                Text(
                    text = stringResource(R.string.day_label),
                    fontSize = 15.sp,
                    color = Color.Black,
                    fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                    fontWeight = FontWeight.Normal
                )

                Spacer(Modifier.height(8.dp))

                CustomPowerSpinner(
                    selectedText = if (selectDayName.isBlank() || selectDayName == "Select Frequency") "Select Day" else selectDayName,
                    onSelectionChanged = { reason ->
                        selectDayName = reason
                        selectedDays = setOf(reason)
                    },
                    horizontalPadding = 24.dp,
                    reasons = selectDayNameOptions
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.reminder_time_label),
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                fontFamily = FontFamily(Font(R.font.urbanist_regular))
            )

            Spacer(modifier = Modifier.height(8.dp))

            currentReminderTime.forEachIndexed { index, reminderValue ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clickable(
                                enabled = index == 0,
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                showTimePickerDialog = true
                            }
                    ) {
                        OutlinedTextField(
                            value = reminderValue,
                            onValueChange = {},
                            readOnly = true,
                            enabled = false,
                            textStyle = TextStyle(
                                color = Color.Black,
                                fontSize = 15.sp,
                                fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                                fontWeight = FontWeight.Normal
                            ),
                            placeholder = {
                                Text("00:00 AM", color = Color(0xFF697383), fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                                    fontWeight = FontWeight.Medium, fontSize = 13.sp)
                            },
                            trailingIcon = {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_appointed_gray_icon),
                                    contentDescription = "clock",
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(30.dp))
                                .border(1.dp, Color(0xFF697383), RoundedCornerShape(30.dp)),
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = Color.White,
                                focusedContainerColor = Color.White,
                                disabledContainerColor = Color.White,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent,
                                disabledTextColor = Color.Black,
                                disabledPlaceholderColor = Color(0xFF697383),
                                disabledTrailingIconColor = Color(0xFF697383)
                            )
                        )
                    }

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
                                    if (currentReminderTime[0].isNotBlank()) {
                                        val updated = currentReminderTime.toMutableList()
                                        updated.add(updated[0])
                                        updated[0] = ""
                                        currentReminderTime = updated
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
                                    val updatedList = currentReminderTime.toMutableList()
                                    updatedList.removeAt(index)
                                    currentReminderTime = updatedList
                                }
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
            }

            Row(modifier = Modifier.padding( vertical = 24.dp)) {
                UniversalInputField(
                    wholePadding = 0.dp,
                    textStartPadding = 0.dp,
                    title = stringResource(R.string.start_date_label),
                    isImportant = false,
                    placeholder = stringResource(R.string.date_format_placeholder),
                    value = startDate,
                    modifier = Modifier.weight(1f),
                    rightIcon = R.drawable.ic_calender_icon,
                ) {
                    showDialog = true
                }
                Spacer(Modifier.width(5.dp))
                UniversalInputField(
                    wholePadding = 0.dp,
                    textStartPadding = 0.dp,
                    title = stringResource(R.string.end_date_optional_label),
                    isImportant = false,
                    placeholder = stringResource(R.string.date_format_placeholder),
                    value = endDate,
                    modifier = Modifier.weight(1f),
                    rightIcon = R.drawable.ic_calender_icon
                ) {
                    showDialog1 = true
                }
            }

            ProfilePhotoPicker(
                label = stringResource(R.string.upload_prescription_optional_label),
                fileName = uploadedFiles?.let { getFileName(it) } ?: stringResource(R.string.no_file_chosen),
                onChooseClick = {
                    filePickerLauncher.launch(arrayOf("image/*", "application/pdf", "application/dicom"))
                }
            )

            Text(
                stringResource(R.string.file_formats_supported),
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        filePickerLauncher.launch(arrayOf("image/*", "application/pdf", "application/dicom"))
                    }
            )

            Spacer(Modifier.height(24.dp))

            ProfileInputMultipleLineField2(
                label = stringResource(R.string.notes_label),
                isImportant = false,
                placeholder = stringResource(R.string.notes_placeholder),
                value = description,
                onValueChange = { description = it },
                heightOfEditText = 135.dp,
                paddingHorizontal = 0.dp,
                borderColor = Color(0xFF697383),
                textColor = Color(0xFF697383),
                textStartPadding = 0.dp
            )

            Spacer(Modifier.height(30.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { checked = !checked },
                verticalAlignment = Alignment.Top
            ) {
                RoundedCustomCheckbox(
                    checked = checked,
                    onCheckedChange = { checked = it }
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = stringResource(R.string.enable_reminder_label),
                    fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                    fontWeight = FontWeight.Normal,
                    fontSize = 15.sp,
                    color = Color.Black,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Spacer(Modifier.height(30.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CancelButton(title = stringResource(R.string.cancel_button)) {
                    navController.popBackStack()
                }

                ContinueButton(text = stringResource(R.string.update_medication_button)) {
                    // In real app, this would save to database
                    if (validateFields()) {
                        val validReminderTimes = currentReminderTime.filter { it.trim().isNotBlank() }

                        val reminderParts = validReminderTimes.map {
                            val time24 = convert12To24Hour(it)
                            MultipartBody.Part.createFormData("reminder_time[]", time24)
                        }
                        var prescriptionPart: MultipartBody.Part? = null

                        if(newPhotoUploaded) {
                           prescriptionPart  = uploadedFiles?.let { uri ->
                                UriToRequestBody.uriToMultipart(context, uri, "prescription_docs")
                            }
                        }

                        viewModel.updateMedication(
                            medicationId = medicationId.toString().toRequestBody(),
                            forWhomId = viewModel.selectedId?.toRequestBody(),
                            medicationType = selectedMedicationType.toRequestBody(),
                            medicationName = medicationName.toRequestBody(),
                            dosage = dosage.toRequestBody(),
                            frequency = selectFrequency.toRequestBody(),
                            days = if (selectFrequency == "Weekly") selectDayName.toRequestBody() else null,
                            startDate = startDate.toRequestBody(),
                            endDate = endDate.toRequestBody(),
                            notes = description.toRequestBody(),
                            reminderStatus = if (checked) "1".toRequestBody() else "0".toRequestBody(),
                            reminderTimes = reminderParts,
                            prescriptionDocs = prescriptionPart,{
                                showDialogSuccessFully = true
                            },{
                                msg -> Toast.makeText(context,msg,Toast.LENGTH_LONG).show()
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(37.dp))
        }
    }

    if (showDialog) {
        CalendarDialog(
            onDismiss = { showDialog = false },
            allowPastDates = false,
            onDateApplied = {
                showDialog = false
                startDate = it.toString()
            }
        )
    }

    if (showDialog1) {
        CalendarDialog(
            onDismiss = { showDialog1 = false },
            onDateApplied = {
                showDialog1 = false
                endDate = it.toString()
            }
        )
    }

    if (showDialogSuccessFully) {
        SuccessfulDialog(
            title = "Medication Updated\nSuccessfully",
            description = "Your changes have been saved.",
            onDismiss = {
                showDialogSuccessFully = false
                navController.popBackStack()
            },
            onOkClick = {
                showDialogSuccessFully = false
                navController.popBackStack()
            }
        )
    }

    if (showTimePickerDialog) {
        TimePickerDialog(
            onDismiss = { showTimePickerDialog = false },
            onTimeApplied = { timeString ->
                showTimePickerDialog = false
                val list = currentReminderTime.toMutableList()
                list[0] = timeString
                currentReminderTime = list
            }
        )
    }
}


@RequiresApi(Build.VERSION_CODES.O)
private fun convert12To24Hour(time12: String): String {
    return try {
        val inputFormatter = java.time.format.DateTimeFormatter.ofPattern("hh:mm a", java.util.Locale.US)
        val outputFormatter = java.time.format.DateTimeFormatter.ofPattern("HH:mm", java.util.Locale.US)
        val localTime = java.time.LocalTime.parse(time12.trim().uppercase(), inputFormatter)
        localTime.format(outputFormatter)
    } catch (e: Exception) {
        time12
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun convert24To12Hour(time24: String): String {
    return try {
        val localTime = try {
            val formatter = java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss", java.util.Locale.US)
            java.time.LocalTime.parse(time24.trim(), formatter)
        } catch (e: Exception) {
            val formatter = java.time.format.DateTimeFormatter.ofPattern("HH:mm", java.util.Locale.US)
            java.time.LocalTime.parse(time24.trim(), formatter)
        }
        val outputFormatter = java.time.format.DateTimeFormatter.ofPattern("hh:mm a", java.util.Locale.US)
        localTime.format(outputFormatter)
    } catch (e: Exception) {
        time24
    }
}



@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun EditMedicationScreenPreview() {
    val navController = rememberNavController()
    EditMedicationScreen(navController = navController,0)
}
