package com.bussiness.curemegptapp.ui.component

import android.app.TimePickerDialog
import android.os.Build
import android.widget.Toast
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bussiness.curemegptapp.R
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale


private val LightBackground = Color(0xFFF5F5F5)
private val TextDark = Color(0xFF333333)
private val TextGray = Color(0xFF999999)
private val SelectedBlue = Color(0xFF258694)
private val BorderGray = Color(0xFF949494)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DateTimePickerView(
    modifier: Modifier = Modifier,
    onDateTimeSelected: (LocalDateTime) -> Unit = {}
) {
    val isCalendarVisible = remember { mutableStateOf(false) }
    val currentMonth = remember { mutableStateOf(YearMonth.now()) }
    val selectedDate = remember { mutableStateOf<LocalDate?>(null) }
    val selectedHour = remember { mutableStateOf(11) }
    val selectedMinute = remember { mutableStateOf(38) }
    val isAM = remember { mutableStateOf(true) }
    val context = LocalContext.current
    val timePickerDialog = remember {
        TimePickerDialog(
            context,
            { _, hourOfDay: Int, minute: Int ->
                val isAMSelected = hourOfDay < 12
                selectedHour.value = if (hourOfDay % 12 == 0) 12 else hourOfDay % 12
                selectedMinute.value = minute
                isAM.value = isAMSelected
            },
            if (isAM.value) selectedHour.value % 12 else (selectedHour.value % 12) + 12,
            selectedMinute.value,
            false // 12-hour format
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth().wrapContentHeight()
            .background(Color.White)
        //.padding(vertical = 10.dp, horizontal = (12.5).dp)
    ) {
        Row {


            // Header
            Text(
                text = "Date of Birth",
                fontSize = 14.sp,
                color = Color.Black,
                fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Text(
                text = " *",
                fontSize = 14.sp,
                color = Color.Red,
                fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // Selected Date and Time Display
        OutlinedTextField(
            value = if (selectedDate.value != null) {
                val formatter = DateTimeFormatter.ofPattern("MM-DD-YYYY")
                "${selectedDate.value!!.format(formatter)} ${String.format("%02d:%02d", selectedHour.value, selectedMinute.value)} ${if (isAM.value) "AM" else "PM"}"
            } else {
                "MM-DD-YYYY"
            },
            onValueChange = { },
            readOnly = true,
            placeholder = {
                Text(
                    "MM-DD-YYYY",
                    color = Color(0xFFAEAEAE),
                    fontSize = 13.sp,
                    fontFamily = FontFamily(Font(R.font.urbanist_regular))
                )
            },
            trailingIcon = {
                Image(
                    painter = painterResource(id = R.drawable.ic_calender_icon), // Use calendar icon if available
                    contentDescription = "Calendar",
                    modifier = Modifier.size(24.dp).clickable( interactionSource = remember { MutableInteractionSource() },
                        indication = null,onClick = {
                        isCalendarVisible.value = true
                    })
                )
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color(0xFFAEAEAE),
                unfocusedIndicatorColor = Color(0xFFAEAEAE)
            ),
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp).clickable( interactionSource = remember { MutableInteractionSource() },
                    indication = null){
                    isCalendarVisible.value = true
                }
        )

        // Calendar Section
        if (isCalendarVisible.value) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .padding(13.dp)
            ) {
                CalendarView(
                    currentMonth = currentMonth.value,
                    selectedDate = selectedDate.value,
                    onDateSelected = { date ->
                        selectedDate.value = date
                        isCalendarVisible.value = false // date select karte hi calendar hide ho jaye
                        Toast.makeText(context, "Selected: $date", Toast.LENGTH_SHORT).show()
                    },
                    onMonthChanged = { newMonth ->
                        currentMonth.value = newMonth
                    }
                )
            }

            // Time Selection Section
            Row(
                modifier = Modifier
                    .fillMaxWidth().padding(horizontal = 20.dp)
                    .padding(top = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Time",
                    fontSize = 16.sp,
                    color = Color.Black,
                    fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                    modifier = Modifier.weight(1f)
                )

                Row(
                    modifier = Modifier.wrapContentWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Hour and Minute Display

                    Box(

                        modifier = Modifier.background(
                            color = Color(0xFFEFEFF0),
                            shape = RoundedCornerShape(6.dp)
                        )
                            .padding(
                                vertical = 4.dp,
                                horizontal = 12.dp
                            ) // Add padding around the Box content
                            .wrapContentSize(Alignment.Center)
                    )
                    {


                        Text(
                            text = "${
                                String.format(
                                    "%02d:%02d",
                                    selectedHour.value,
                                    selectedMinute.value
                                )
                            }",
                            fontSize = 16.sp,
                            fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                            color = TextDark,
                            modifier = Modifier
                                .clickable( interactionSource = remember { MutableInteractionSource() },
                                    indication = null) { timePickerDialog.show() } // 🟢 Attach TimePickerDialog here
                        )
                    }

                    Spacer(Modifier.width(8.dp))


                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(9.dp))
                            .background(Color(0xFFEEEEEF)) // Light gray capsule background
                            .padding(2.dp) // Padding between capsule border and buttons
                    ) {
                        Row {
                            Box(
                                modifier = Modifier
                                    .width(50.dp)
                                    .height(30.dp)
                                    .clip(RoundedCornerShape(7.dp))
                                    .background(if (isAM.value) SelectedBlue else Color.Transparent)
                                    .clickable( interactionSource = remember { MutableInteractionSource() },
                                        indication = null) {
                                        //  isAM.value = true
                                        if (!isAM.value) {
                                            isAM.value = true
                                            // 🔁 Convert to AM
                                            if (selectedHour.value == 12) {
                                                selectedHour.value = 12 // noon -> 12 AM
                                            } else if (selectedHour.value > 12) {
                                                selectedHour.value -= 12
                                            }
                                        }  },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "AM",
                                    color = if (isAM.value) Color.White else Color.Black,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .width(50.dp)
                                    .height(30.dp)
                                    .clip(RoundedCornerShape(7.dp))
                                    .background(if (!isAM.value) SelectedBlue else Color.Transparent)
                                    .clickable( interactionSource = remember { MutableInteractionSource() },
                        indication = null){
                                        //    isAM.value = false
                                        if (isAM.value) {
                                            isAM.value = false
                                            // 🔁 Convert to PM
                                            if (selectedHour.value < 12) {
                                                selectedHour.value = (selectedHour.value % 12) + 12
                                            }
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "PM",
                                    color = if (!isAM.value) Color.White else Color.Black,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                                )
                            }
                        }
                    }

                }


            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarView(
    currentMonth: YearMonth,
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit,
    onMonthChanged: (YearMonth) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        MonthHeader(currentMonth, onMonthChanged)
        DayNamesRow()
        WeeksGrid(currentMonth, selectedDate, onDateSelected)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun MonthHeader(
    month: YearMonth,
    onMonthChanged: (YearMonth) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp).padding(horizontal = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Text(
            text = "${month.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${month.year}",
            color = Color.Black,
            fontSize = 15.sp,
            fontFamily = FontFamily(Font(R.font.urbanist_regular))
        )

        Row {
            Image(
                painter = painterResource(id = R.drawable.ic_previous_icon_arrow),
                contentDescription = "Previous Month",
                modifier = Modifier
                    .size(16.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        onMonthChanged(month.minusMonths(1))
                    }
            )
            Spacer(Modifier.width(28.dp))

            Image(
                painter = painterResource(id = R.drawable.ic_next_icon_arrow),
                contentDescription = "Next Month",
                modifier = Modifier
                    .size(16.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        onMonthChanged(month.plusMonths(1))
                    }
            )
        }

    }
}

@Composable
private fun DayNamesRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        listOf("SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT").forEach { day ->
            Text(
                text = day,
                color = Color(0x4D3C3C43),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun WeeksGrid(
    month: YearMonth,
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit
) {
    val weeks = remember(month) { generateCalendarWeeks(month) }

    Column(modifier = Modifier.fillMaxWidth()) {
        weeks.forEach { week ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                week.forEach { date ->
                    DayCell(
                        date = date,
                        isCurrentMonth = date?.month == month.month,
                        isSelected = date == selectedDate,
                        isToday = date == LocalDate.now(),
                        onClick = { if (date != null) onDateSelected(date) }
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun DayCell(
    date: LocalDate?,
    isCurrentMonth: Boolean,
    isSelected: Boolean,
    isToday: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .padding(2.dp)
            .clip(CircleShape)
            .clickable(enabled = date != null, onClick = onClick)
            .then(
                when {
                    isSelected -> Modifier.background(Color(0xFFE5EFF2))
                    isToday && isCurrentMonth -> Modifier.border(1.dp, SelectedBlue, CircleShape)
                    else -> Modifier.background(Color.Transparent)
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        if (date != null) {
            Text(
                text = date.dayOfMonth.toString(),
                color = when {
                    isSelected -> Color(0xFF258694)
                    isToday && isCurrentMonth -> SelectedBlue
                    isCurrentMonth -> TextDark
                    else -> TextGray
                },
                fontSize = 15.sp,
                fontWeight = if (isToday && isCurrentMonth) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun generateCalendarWeeks(yearMonth: YearMonth): List<List<LocalDate?>> {
    val weeks = mutableListOf<List<LocalDate?>>()
    var week = mutableListOf<LocalDate?>()

    val firstDayOfMonth = yearMonth.atDay(1)
    val dayOfWeekValue = firstDayOfMonth.dayOfWeek.value % 7 // Convert to Sunday = 0

    val previousMonth = yearMonth.minusMonths(1)
    val daysInPreviousMonth = previousMonth.lengthOfMonth()

    // Add days from the previous month
    for (i in (daysInPreviousMonth - dayOfWeekValue + 1)..daysInPreviousMonth) {
        week.add(previousMonth.atDay(i))
    }

    // Add current month's days
    for (day in 1..yearMonth.lengthOfMonth()) {
        week.add(yearMonth.atDay(day))
        if (week.size == 7) {
            weeks.add(week)
            week = mutableListOf()
        }
    }

    // Add next month's days to fill the last week
    val nextMonth = yearMonth.plusMonths(1)
    var nextMonthDay = 1
    while (week.size < 7 && week.isNotEmpty()) {
        week.add(nextMonth.atDay(nextMonthDay))
        nextMonthDay++
    }
    if (week.isNotEmpty()) {
        weeks.add(week)
    }

    return weeks
}



@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun DateTimePickerPreview() {
    MaterialTheme {
        DateTimePickerView()
    }
}