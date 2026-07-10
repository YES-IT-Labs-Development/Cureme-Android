package com.bussiness.curemegptapp.ui.screen.main.rescheduleAppointment

//RescheduleAppointmentScreen


import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.bussiness.curemegptapp.R
import com.bussiness.curemegptapp.ui.component.CancelButton
import com.bussiness.curemegptapp.ui.component.ContinueButton
import com.bussiness.curemegptapp.ui.component.ProfileInputField
import com.bussiness.curemegptapp.ui.component.ProfileInputField2
import com.bussiness.curemegptapp.ui.component.ProfileInputMultipleLineField2
import com.bussiness.curemegptapp.ui.component.TopBarHeader1
import com.bussiness.curemegptapp.ui.component.UniversalInputField
import com.bussiness.curemegptapp.ui.component.input.CustomPowerSpinner
import com.bussiness.curemegptapp.ui.dialog.CalendarDialog
import com.bussiness.curemegptapp.ui.dialog.SuccessfulDialog
import com.bussiness.curemegptapp.ui.dialog.TimePickerDialog
import com.bussiness.curemegptapp.ui.screen.main.schedule.Appointment
import com.bussiness.curemegptapp.ui.viewModel.main.AppointmentUIModel
import com.bussiness.curemegptapp.viewmodel.appointmentViewModel.RescheduleViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RescheduleAppointmentScreen(navController: NavHostController,
                                appointmentId: Int, viewModel : RescheduleViewModel = hiltViewModel()) {

    LaunchedEffect(Unit) {
        viewModel.loadInitialData(appointmentId)
    }


    val appointment by viewModel.appointmentData.collectAsState()


    var dateOfBirth by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var preferredDoctor by remember { mutableStateOf("") }
    var preferredClinic by remember { mutableStateOf("") }
    var selectedAppointment by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedMember by remember { mutableStateOf("Myself") } // DUMMY: For myself
    var selectedAppointmentReminder by remember { mutableStateOf("30 Min Before") } // DUMMY: Standard reminder
    val context = LocalContext.current
    val memberOptions by  viewModel.memberOption.collectAsState()
    val appointmentOptions by viewModel.appointmentTypeOption.collectAsState()


    var showDialog by remember { mutableStateOf(false) }
    var showDialog1 by remember { mutableStateOf(false) }
    var showDialogSuccessFully by remember { mutableStateOf(false) }

    val selectedAppointmentReminderOptions = listOf(
        stringResource(R.string.appointment_reminder_10_min),
        stringResource(R.string.appointment_reminder_30_min),
        stringResource(R.string.appointment_reminder_2_hrs),
        stringResource(R.string.appointment_reminder_24_hrs)
    )
    LaunchedEffect(appointment.id) {
        dateOfBirth = appointment.date ?: ""
        time = appointment.time ?: ""
        preferredDoctor = appointment.preferred_doctor ?: ""
        preferredClinic = appointment.preferred_clinic ?: ""
        description = appointment.description ?: ""
        selectedMember = appointment.family_member?.full_name ?: "My Self"
        selectedAppointment = appointment.appointment_type?.name ?: ""
        appointment.appointment_reminder?.let {
            if (it.isNotBlank()) {
                selectedAppointmentReminder = it
            }
        }
    }




    fun validateFields(): Boolean {
        // Validate Member Selection

        if (selectedMember == "Select Member" || selectedMember.isBlank()) {
            Toast.makeText(context, "Please select a member", Toast.LENGTH_SHORT).show()
            return false
        }

        // Validate Appointment Type Selection
        if (selectedAppointment == "Select Appointment Type" || selectedAppointment.isBlank()) {
            Toast.makeText(context, "Please select an appointment type", Toast.LENGTH_SHORT).show()
            return false
        }

        // Validate Description (Optional but with minimum length if provided)

        if (description.isBlank()) {
            Toast.makeText(context, "Description is required", Toast.LENGTH_SHORT).show()
            return false
        }
        if (description.isNotBlank()) {
            // Trim whitespace and check actual content length
            val trimmedDescription = description.trim()
            println("DEBUG - Trimmed description: '$trimmedDescription', Length: ${trimmedDescription.length}")

            if (trimmedDescription.length < 10) {
                Toast.makeText(context, "Description should be at least 10 characters", Toast.LENGTH_SHORT).show()
                return false
            }
        }

        // Validate Date
        if (dateOfBirth.isBlank()) {
            Toast.makeText(context, "Please select a date", Toast.LENGTH_SHORT).show()
            return false
        }

        val dateRegex = Regex("^\\d{2}-\\d{2}-\\d{4}\$")
        if (dateOfBirth.isNotBlank() && !dateRegex.matches(dateOfBirth)) {
            Toast.makeText(context, "Please enter date in MM-DD-YYYY format", Toast.LENGTH_SHORT)
                .show()
            return false
        }


            // Validate Time
        if (time.isBlank()) {
            Toast.makeText(context, "Please select a time", Toast.LENGTH_SHORT).show()
            return false
        }

        // Validate Time format - HH:MM AM/PM format
        val timeRegex = Regex("^(0?[1-9]|1[0-2]):[0-5][0-9]\\s?(AM|PM|am|pm)\$", RegexOption.IGNORE_CASE)
        if (time.isNotBlank() && !timeRegex.matches(time)) {
            Toast.makeText(context, "Please enter time in HH:MM AM/PM format (e.g., 03:01 AM)", Toast.LENGTH_SHORT).show()
            return false
        }

        if (preferredDoctor.isBlank()) {
            Toast.makeText(context, "Doctor name is required", Toast.LENGTH_SHORT).show()
            return false
        }

        // Validate Preferred Doctor (Optional but with validation if provided)
        if (preferredDoctor.isNotBlank()) {
            if (preferredDoctor.length < 3) {
                Toast.makeText(context, "Doctor name should be at least 3 characters", Toast.LENGTH_SHORT).show()
                return false
            }

            // Check if it starts with Dr. or similar prefix
            if (!preferredDoctor.contains("Dr.", ignoreCase = true) &&
                !preferredDoctor.contains("Doctor", ignoreCase = true)) {
                // Warning only, not blocking
                // Toast.makeText(context, "Consider adding 'Dr.' before the name", Toast.LENGTH_SHORT).show()
            }
        }
        if (preferredClinic.isBlank()) {
            Toast.makeText(context, "Clinic name is required", Toast.LENGTH_SHORT).show()
            return false
        }

        // Validate Preferred Clinic (Optional but with validation if provided)
        if (preferredClinic.isNotBlank()) {
            if (preferredClinic.length < 3) {
                Toast.makeText(context, "Clinic name should be at least 3 characters", Toast.LENGTH_SHORT).show()
                return false
            }
        }

        // Validate Appointment Reminder
        if (selectedAppointmentReminder == "Select Member" || selectedAppointmentReminder.isBlank()) {
            Toast.makeText(context, "Please select an appointment reminder", Toast.LENGTH_SHORT).show()
            return false
        }

        // Validate future date and time
        try {
            val currentDate = java.time.LocalDate.now()
            val selectedDateParts = dateOfBirth.split("-")
            if (selectedDateParts.size == 3) {
                val selectedDate = java.time.LocalDate.of(
                    selectedDateParts[2].toInt(),
                    selectedDateParts[0].toInt(),
                    selectedDateParts[1].toInt()
                )

                if (selectedDate.isBefore(currentDate)) {
                    Toast.makeText(context, "Please select a future date", Toast.LENGTH_SHORT).show()
                    return false
                }

                // If date is today, check time (convert 12-hour format to 24-hour for comparison)
                if (selectedDate.isEqual(currentDate) && time.isNotBlank()) {
                    val currentTime = java.time.LocalTime.now()

                    // Convert 12-hour format time to LocalTime for comparison
                    val formattedTime = time.uppercase().replace(" ", "")
                    val hourMinutePart = formattedTime.substring(0, 5)
                    val amPmPart = formattedTime.substring(5)

                    val timeParts = hourMinutePart.split(":")
                    val hour = timeParts[0].toInt()
                    val minute = timeParts[1].toInt()

                    var selectedHour = hour
                    if (amPmPart == "PM" && hour != 12) {
                        selectedHour += 12
                    } else if (amPmPart == "AM" && hour == 12) {
                        selectedHour = 0
                    }

                    val selectedTime = java.time.LocalTime.of(selectedHour, minute)

                    if (selectedTime.isBefore(currentTime)) {
                        Toast.makeText(context, "Please select a future time", Toast.LENGTH_SHORT).show()
                        return false
                    }
                }
            }
        } catch (e: Exception) {
            // Date parsing failed, but we already validated format
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
            title = stringResource(R.string.reschedule_appointment_title),
            onBackClick = { navController.popBackStack() }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 19.dp, vertical = 30.dp)
        ) {

            Text(
                text = stringResource(R.string.for_whom_label),
                fontSize = 15.sp,
                color = Color.Black,
                fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                fontWeight = FontWeight.Normal
            )

            Spacer(Modifier.height(8.dp))

            CustomPowerSpinner(
                selectedText = selectedMember,
                onSelectionChanged = { reason ->
                    selectedMember = reason
                },
                horizontalPadding = 24.dp,
                reasons = memberOptions,
                enabled = false
            )

            Spacer(Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.appointment_type_label),
                fontSize = 15.sp,
                color = Color.Black,
                fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                fontWeight = FontWeight.Normal
            )

            Spacer(Modifier.height(8.dp))

            CustomPowerSpinner(
                selectedText = selectedAppointment,
                onSelectionChanged = { reason ->
                    selectedAppointment = reason
                },
                horizontalPadding = 24.dp,
                reasons = appointmentOptions,
                enabled = false
            )

            Spacer(Modifier.height(24.dp))

            ProfileInputMultipleLineField2(
                label = stringResource(R.string.description_label),
                isImportant = false,
                placeholder = stringResource(R.string.type_here_placeholder),
                value = description,
                onValueChange = { description = it },
                heightOfEditText = 135.dp,
                paddingHorizontal = 0.dp,
                borderColor = Color(0xFF697383),
                textColor = Color(0xFF697383),

            )

            Spacer(Modifier.width(24.dp))

            Row(modifier = Modifier.padding(horizontal = 5.dp)) {
                UniversalInputField(
                    title = stringResource(R.string.date_label),
                    isImportant = false,
                    placeholder = stringResource(R.string.date_format_placeholder),
                    value = dateOfBirth,
                    modifier = Modifier.weight(1f),
                    rightIcon = R.drawable.ic_calender_icon
                ) {
                    showDialog = true
                }
                Spacer(Modifier.width(5.dp))
                UniversalInputField(
                    title = stringResource(R.string.time_label),
                    isImportant = false,
                    placeholder = stringResource(R.string.time_format_placeholder),
                    value = time,
                    modifier = Modifier.weight(1f),
                    rightIcon = R.drawable.ic_appointed_gray_icon
                ) {
                    showDialog1 = true
                }
            }

            Spacer(Modifier.width(24.dp))

            ProfileInputField2(
                label = stringResource(R.string.preferred_doctor_label),
                isImportant = false,
                placeholder = stringResource(R.string.doctor_placeholder),
                value = preferredDoctor,
                onValueChange = { preferredDoctor = it }
            )

            Spacer(Modifier.width(24.dp))

            ProfileInputField2(
                label = stringResource(R.string.preferred_clinic_label),
                isImportant = false,
                placeholder = stringResource(R.string.clinic_placeholder),
                value = preferredClinic,
                onValueChange = { preferredClinic = it }
            )

            Spacer(Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.appointment_reminder_label),
                fontSize = 15.sp,
                color = Color.Black,
                fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                fontWeight = FontWeight.Normal
            )

            Spacer(Modifier.height(8.dp))

            CustomPowerSpinner(
                selectedText = selectedAppointmentReminder,
                onSelectionChanged = {
                    reason -> selectedAppointmentReminder = reason
                },
                horizontalPadding = 24.dp,
                reasons = selectedAppointmentReminderOptions,
                enabled = true
            )

            Spacer(Modifier.height(30.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                CancelButton(
                    title = stringResource(R.string.cancel_button)
                ) {
                    navController.popBackStack()
                }

                ContinueButton(
                    text = stringResource(R.string.reschedule_button)
                ) {
                    if (validateFields()){
                    //

                        viewModel.rescheduleAppointment(appointmentId,
                            dateOfBirth,time,selectedAppointmentReminder,{
                                showDialogSuccessFully = true
                            },{ msg->
                                Toast.makeText(context,msg,Toast.LENGTH_SHORT).show()
                            } )

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
                dateOfBirth = it
                showDialog = false
            }
        )
    }

    if (showDialog1) {
        TimePickerDialog(
            onDismiss = {
                showDialog1 = false
            },
            onTimeApplied = {
                time = it
                showDialog1 = false
            }
        )
    }

    if (showDialogSuccessFully) {
        SuccessfulDialog(
            title = "Appointment Updated\nSuccessfully",
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
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun RescheduleAppointmentScreenPreview() {
    val navController = rememberNavController()
    RescheduleAppointmentScreen(navController = navController,
        appointmentId = 0
        )
}


