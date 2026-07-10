package com.bussiness.curemegptapp.ui.screen.main.scheduleNewAppointment

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
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.bussiness.curemegptapp.R
import com.bussiness.curemegptapp.navigation.AppDestination
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
import com.bussiness.curemegptapp.viewmodel.appointmentViewModel.AppointmentViewModel
import java.time.format.DateTimeFormatter

//ScheduleNewAppointmentScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ScheduleNewAppointmentScreen(
    navController: NavHostController,
    viewModel : AppointmentViewModel = androidx.hilt.navigation.compose.hiltViewModel()
) {

    val handle = navController.previousBackStackEntry?.savedStateHandle
    val chatId = handle?.get<Int>("chatId")

    androidx.compose.runtime.LaunchedEffect(chatId) {
        val recommendedId = if (chatId != null && chatId != 0) chatId.toString() else null
        viewModel.updateRecommendedChatId(recommendedId)
    }

    var dateOfBirth by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var preferredDoctor by remember { mutableStateOf("") }
    var preferredClinic by remember { mutableStateOf("") }
    var selectedMember by remember { mutableStateOf("Select Member") }
    var selectedAppointmentReminder by remember { mutableStateOf("Select Member") }
    val memberOptions by viewModel.memberOption.collectAsState() // Added example options
      val selectedAppointmentReminderOptions =
        listOf("10 Min Before", "30 Min Before", "2 Hrs. Before", "24 Hrs. Before") // Added example options
    var selectedAppointment by remember { mutableStateOf("Select Appointment Type") }
    var description by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var showDialog1 by remember { mutableStateOf(false) }
    var showDialogSuccessFully by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val appointmentOptions by  viewModel.appointmentTypeOption.collectAsState()


    val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")

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

        // Validate Date format (basic check)
        val dateRegex = Regex("^\\d{2}-\\d{2}-\\d{4}\$")

        if (dateOfBirth.isNotBlank() && !dateRegex.matches(dateOfBirth)) {
            Toast.makeText(context, "Please enter date in MM-DD-YYYY format", Toast.LENGTH_SHORT).show()
            }

        // Validate Time
        if (time.isBlank()) {
            Toast.makeText(context, "Please select a time", Toast.LENGTH_SHORT).show()
            return false
        }

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
            .fillMaxSize().statusBarsPadding().imePadding().verticalScroll(rememberScrollState())
            .background(Color(0xFFFFFFFF))
    ) {

        TopBarHeader1(
            title = stringResource(R.string.schedule_new_appointment_title),
            onBackClick = {navController.navigateUp()}
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 19.dp, vertical = 30.dp)
        ) {


            Text(
                text = stringResource(R.string.for_whom_label)/*"For Whom"*/,
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
                    viewModel.updateForWhomId(reason)
                },
                horizontalPadding = 24.dp,
                reasons = memberOptions
            )

            Spacer(Modifier.height(24.dp))
            Text(
                text = stringResource(R.string.appointment_type_label)/*"Appointment Type"*/,
                fontSize = 15.sp,
                color = Color.Black,
                fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                fontWeight = FontWeight.Normal
            )
            Spacer(Modifier.height(8.dp))
            CustomPowerSpinner(
                selectedText = selectedAppointment,
                onSelectionChanged = { reason ->
                    viewModel.updateAppointmentTypeId(reason)
                    selectedAppointment = reason
                },
                horizontalPadding = 24.dp,
                reasons = appointmentOptions
            )

            Spacer(Modifier.height(24.dp))

            ProfileInputMultipleLineField2(
                label = stringResource(R.string.description_label)/*"Description"*/,
                isImportant = false,
                placeholder = stringResource(R.string.type_here_placeholder)/*"Type here...."*/,
                value = description,
                onValueChange = { description = it
                                viewModel.updateDescription(it)
                                },
                heightOfEditText = 135.dp,
                paddingHorizontal = 5.dp,
                borderColor = Color(0xFF697383),
                textColor = Color(0xFF697383),
                textStartPadding = 5.dp
            )

            Spacer(Modifier.height(20.dp))

            Row (modifier = Modifier.padding(horizontal = 0.dp)) {
                UniversalInputField(
                    title = stringResource(R.string.date_label)/*"Date"*/,
                    isImportant = false,
                    placeholder = stringResource(R.string.date_format_placeholder)/*"MM-DD-YYYY"*/,
                    value = dateOfBirth,
                    modifier = Modifier.weight(1f),
                    rightIcon = R.drawable.ic_calender_icon
                ) {
                    showDialog = true
                }
                Spacer(Modifier.height(20.dp))
                UniversalInputField(
                    title = stringResource(R.string.time_label)/*"Time"*/,
                    isImportant = false,
                    placeholder = stringResource(R.string.time_format_placeholder)/*"00:00:00"*/,
                    value = time,
                    modifier = Modifier.weight(1f),
                    rightIcon = R.drawable.ic_appointed_gray_icon
                ) {
                    showDialog1 = true
                }

            }
            Spacer(Modifier.height(20.dp))

            ProfileInputField2(
                label = stringResource(R.string.preferred_doctor_label)/*"Preferred Doctor"*/,
                isImportant = false,
                placeholder = stringResource(R.string.doctor_placeholder)/*"e.g., Dr. John Deo"*/,
                value = preferredDoctor,
                onValueChange = { preferredDoctor = it
                     viewModel.updatePreferredDoctor(it)
                }
            )

            Spacer(Modifier.height(20.dp))

            ProfileInputField2(
                label = stringResource(R.string.preferred_clinic_label)/*"Preferred Clinic"*/,
                isImportant = false,
                placeholder = stringResource(R.string.clinic_placeholder)/*"e.g., Bright Smile Dental"*/,
                value = preferredClinic,
                onValueChange = { preferredClinic = it
                    viewModel.updatePreferredClinic(it)
                }
            )

            Spacer(Modifier.height(24.dp))
            Row (modifier = Modifier.padding(horizontal = 5.dp)) {
                Column {


                Text(
                    text = stringResource(R.string.appointment_reminder_label)/*"Appointment Reminder"*/,
                    fontSize = 15.sp,
                    color = Color.Black,
                    fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                    fontWeight = FontWeight.Normal
                )

                Spacer(Modifier.height(8.dp))

                CustomPowerSpinner(
                    selectedText = selectedAppointmentReminder,
                    onSelectionChanged = { reason ->
                        selectedAppointmentReminder = reason
                        viewModel.updateAppointmentReminder(reason)
                    },
                    horizontalPadding = 24.dp,
                    reasons = selectedAppointmentReminderOptions // Pass the list of options here
                )
                }
            }
            Spacer(Modifier.height(30.dp))
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                CancelButton(title = stringResource(R.string.cancel_button)/*"Cancel"*/) {
                   navController.navigateUp()
                }

                ContinueButton(text = stringResource(R.string.schedule_button)/*"Schedule"*/) {
                    if (validateFields()) {
                        viewModel.addScheduleAppointment({
                            showDialogSuccessFully = true
                        },{ msg ->
                            Toast.makeText(context,msg,Toast.LENGTH_LONG).show()
                        })
                    }
                }
            }
            Spacer(Modifier.height(37.dp))
        }
    }
    if (showDialog) {
        CalendarDialog(
            onDismiss = { showDialog = false },
            allowFutureDates = true,
            allowPastDates = false,
            onDateApplied = {
                showDialog = false
                dateOfBirth = it.toString()
                viewModel.updateDate(it.toString())
            }
        )
    }
    if (showDialog1) {
        TimePickerDialog(
            onDismiss = {
                showDialog1 = false
            }, onTimeApplied = {
                time= it
                viewModel.updateTime(time)
                showDialog1 = false
            }
        )
    }

    if (showDialogSuccessFully) {
        SuccessfulDialog(
            title = "Appointment Scheduled\nSuccessfully",
            description = "Your appointment reminder are set.",
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
fun ScheduleNewAppointmentScreenPreview() {
    val navController = rememberNavController()
    ScheduleNewAppointmentScreen(navController = navController)
}