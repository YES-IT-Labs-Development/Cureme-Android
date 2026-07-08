package com.bussiness.curemegptapp.ui.dialog

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.bussiness.curemegptapp.R
import com.bussiness.curemegptapp.ui.component.CancelButton
import com.bussiness.curemegptapp.ui.component.GradientButton2
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarDialog(
    onDismiss: () -> Unit,
    allowFutureDates: Boolean = true,
    allowPastDates: Boolean = true,
    onDateApplied: (String) -> Unit
) {
    val currentMonth = remember { mutableStateOf(YearMonth.now()) }
    val selectedDate = remember { mutableStateOf<LocalDate?>(null) }
    val showMonthPicker = remember { mutableStateOf(false) }
    if (showMonthPicker.value) {
        IOSMonthYearPicker(
            current = currentMonth.value,
            allowFutureDates = allowFutureDates,
            allowPastDates = allowPastDates,
            onSelect = { newValue -> currentMonth.value = newValue },
            onDismiss = { showMonthPicker.value = false }
        )
    }


    Dialog(onDismissRequest = { onDismiss() },   properties = DialogProperties(
        dismissOnClickOutside = false, // 🔴 IMPORTANT
        dismissOnBackPress = false       // back press se band chahiye to true
    )) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(26.dp))
                .background(Color.White)
                .padding(15.dp)
        ) {

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {


                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_calendar_dialog_view),
                            contentDescription = "",

                            modifier = Modifier.size(35.dp)
                        )
                        Text(
                            text = "Select Date",
                            fontSize = 15.sp,
                            color = Color.Black,
                            fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(start = 10.dp),
                        )
                    }

                    Image(
                        painter = painterResource(id = R.drawable.ic_close),
                        contentDescription = "",
                        modifier = Modifier
                            .size(43.dp)
                            .clickable( interactionSource = remember { MutableInteractionSource() },
                                indication = null) { onDismiss() }
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // ---------- MONTH HEADER ----------
                MonthHeaderView(
                    month = currentMonth.value,
                    allowPastDates = allowPastDates,
                    onPrevious = {
                        val prevMonth = currentMonth.value.minusMonths(1)
                        if (allowPastDates || !prevMonth.isBefore(YearMonth.now())) {
                            currentMonth.value = prevMonth
                        }
                    },
                    onNext = { currentMonth.value = currentMonth.value.plusMonths(1) },
                    onClickMonthPicker = { showMonthPicker.value = true }
                )

                Spacer(modifier = Modifier.height(10.dp))

                // ---------- WEEK DAY ROW ----------
                DaysHeaderRow()

                // ---------- DATES GRID ----------
                DatesGrid(
                    currentMonth = currentMonth.value,
                    selectedDate = selectedDate.value,
                    allowFutureDates = allowFutureDates,
                    allowPastDates = allowPastDates,
                    onSelectDate = { selectedDate.value = it }
                )

                Spacer(modifier = Modifier.height(24.dp))



                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 2.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.weight(0.6f))
                    Row(Modifier.weight(1.4f)) {


                        CancelButton(
                            modifier = Modifier
                                .width(96.dp)
                                .height(45.dp),
                            paddingHorizontal = 5.dp,
                            onClick = { onDismiss() }
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        GradientButton2(
                            text = "Apply",
                            fontSize = 14.sp,
                            paddingHorizontal = 2.dp,
                            onClick = {
                                selectedDate.value?.let {
                                    val formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy")
                                    onDateApplied(it.format(formatter))
                                }
                            },
                            modifier = Modifier
                                .width(98.dp)
                                .height(45.dp)
                        )
                    }
                }
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MonthHeaderView(
    month: YearMonth,
    allowPastDates: Boolean = true,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onClickMonthPicker: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable( interactionSource = remember { MutableInteractionSource() },
                indication = null) { onClickMonthPicker() }
        ) {


            Text(
                text = "${
                    month.month.getDisplayName(
                        TextStyle.FULL,
                        Locale.getDefault()
                    )
                } ${month.year}",
                color = Color.Black,
                fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
            )
            Image(
                painter = painterResource(id = R.drawable.ic_next_arrow_calender),
                contentDescription = "",
                modifier = Modifier
                    .width(9.dp)
                    .height(16.dp)
            )

        }
        Row {
            val showPrevious = allowPastDates || month.isAfter(YearMonth.now())
            if (showPrevious) {
                Image(
                    painter = painterResource(id = R.drawable.ic_previous_icon_arrow),
                    contentDescription = "",

                    modifier = Modifier
                        .size(32.dp)
                        .clickable( interactionSource = remember { MutableInteractionSource() },
                            indication = null) { onPrevious() }
                )
            } else {
                Spacer(modifier = Modifier.size(32.dp))
            }



            Image(
                painter = painterResource(id = R.drawable.ic_next_icon_arrow),
                contentDescription = "",
                modifier = Modifier
                    .size(32.dp)
                    .clickable( interactionSource = remember { MutableInteractionSource() },
                        indication = null) { onNext() }
            )
        }
    }
}

@Composable
fun DaysHeaderRow() {

    val days = listOf("SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT")

    Row(modifier = Modifier.fillMaxWidth()) {
        days.forEach {
            Text(
                text = it,
                fontSize = 10.sp,
                color = Color(0xFF697383),
                fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                modifier = Modifier
                    .weight(1f),     // ⭐ SAME WEIGHT AS DATES
                textAlign = TextAlign.Center
            )
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DatesGrid(
    currentMonth: YearMonth,
    selectedDate: LocalDate?,
    allowFutureDates: Boolean,
    allowPastDates: Boolean = true,
    onSelectDate: (LocalDate) -> Unit
) {

//    val weeks = remember(currentMonth) { buildMonthMatrix(currentMonth) }
//    val today = LocalDate.now()
//
//    Column(modifier = Modifier.fillMaxWidth()) {
//
//        weeks.forEach { week ->
//
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(vertical = 6.dp)
//            ) {
//
//                week.forEach { date ->
//
//                    Box(
//                        modifier = Modifier
//                            .weight(1f)
//                            .height(44.dp),
//                        contentAlignment = Alignment.Center
//                    ) {
//
//                        if (date != null) {
//
//                            val isSelected = selectedDate == date
//                            val isFutureDate = date.isAfter(today)
//
//                            Box(
//                                modifier = Modifier
//                                    .size(42.dp)
//                                    .clip(CircleShape)
//                                    .clickable(
//                                        enabled = !isFutureDate,
//                                        interactionSource = remember { MutableInteractionSource() },
//                                        indication = null
//                                    ) {
//                                        onSelectDate(date)
//                                    }
//                                    .background(
//                                        if (isSelected) Color(0xFF4A35C9)
//                                        else Color.Transparent
//                                    ),
//                                contentAlignment = Alignment.Center
//                            ) {
//
//                                Text(
//                                    text = date.dayOfMonth.toString(),
//                                    fontSize = 15.sp,
//                                    color = when {
//                                        isSelected -> Color.White
//                                        isFutureDate -> Color.LightGray
//                                        else -> Color.Black
//                                    }
//                                )
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }

    val weeks = remember(currentMonth) { buildMonthMatrix(currentMonth) }
    val today = LocalDate.now()

    Column(modifier = Modifier.fillMaxWidth()) {

        weeks.forEach { week ->

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {

                week.forEach { date ->

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        contentAlignment = Alignment.Center
                    ) {

                        if (date != null) {

                            val isSelected = selectedDate == date
                            val isFutureDate = date.isAfter(today)
                            val isPastDate = date.isBefore(today)

                            val isDisabled =
                                (!allowFutureDates && isFutureDate) || (!allowPastDates && isPastDate)

                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .clickable(
                                        enabled = !isDisabled,
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        onSelectDate(date)
                                    }
                                    .background(
                                        if (isSelected && !isDisabled) Color(0xFF4A35C9)
                                        else Color.Transparent
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = date.dayOfMonth.toString(),
                                    fontSize = 15.sp,
                                    color = when {
                                        isSelected && !isDisabled -> Color.White
                                        isDisabled -> Color.LightGray
                                        else -> Color.Black
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }


}


//@RequiresApi(Build.VERSION_CODES.O)
//@Composable
//fun DatesGrid(
//    currentMonth: YearMonth,
//    selectedDate: LocalDate?,
//    onSelectDate: (LocalDate) -> Unit
//) {
//    val weeks = remember(currentMonth) { buildMonthMatrix(currentMonth) }
//
//    Column(modifier = Modifier.fillMaxWidth()) {
//
//        weeks.forEach { week ->
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(vertical = 6.dp)
//            ) {
//
//                week.forEach { date ->
//
//                    Box(
//                        modifier = Modifier
//                            .weight(1f)        // ⭐ SAME AS HEADER → PERFECT ALIGNMENT
//                            .height(44.dp),
//                        contentAlignment = Alignment.Center
//                    ) {
//
//                        if (date != null) {
//
//                            val isSelected = selectedDate == date
//
//                            Box(
//                                modifier = Modifier
//                                    .size(42.dp)
//                                    .clip(CircleShape)
//                                    .clickable( interactionSource = remember { MutableInteractionSource() },
//                                        indication = null) { onSelectDate(date) }
//                                    .background(
//                                        if (isSelected) Color(0xFF4A35C9) else Color.Transparent
//                                    ),
//                                contentAlignment = Alignment.Center
//                            ) {
//                                Text(
//                                    text = date.dayOfMonth.toString(),
//                                    fontSize = 15.sp,
//                                    color = if (isSelected) Color.White else Color.Black
//                                )
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//}


@RequiresApi(Build.VERSION_CODES.O)
fun buildMonthMatrix(yearMonth: YearMonth): List<List<LocalDate?>> {

    val firstDay = yearMonth.atDay(1)
    val totalDays = yearMonth.lengthOfMonth()

    // SUN START → MON=1 ... SUN=7 → convert to 0 index
    val leadingBlanks = firstDay.dayOfWeek.value % 7

    val slots = mutableListOf<LocalDate?>()

    repeat(leadingBlanks) { slots.add(null) }

    for (d in 1..totalDays) {
        slots.add(yearMonth.atDay(d))
    }

    while (slots.size % 7 != 0) slots.add(null)

    return slots.chunked(7)
}




@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun IOSMonthYearPicker(
    current: YearMonth,
    allowFutureDates: Boolean = true,
    allowPastDates: Boolean = true,
    onSelect: (YearMonth) -> Unit,
    onDismiss: () -> Unit
) {
    val today = LocalDate.now()
    val currentYear = today.year
    val months = Month.values().toList()
    val years = if (allowPastDates) {
        if (allowFutureDates) (1900..2100).toList() else (1900..currentYear).toList()
    } else {
        if (allowFutureDates) (currentYear..2100).toList() else listOf(currentYear)
    }

    val selectedMonth = remember { mutableStateOf(current.month) }
    val selectedYear = remember { mutableStateOf(current.year) }

    val visibleMonths = remember(selectedYear.value) {
        if (!allowPastDates && selectedYear.value == currentYear) {
            months.filter { it.value >= today.monthValue }
        } else {
            months
        }
    }

    // Coerce selected month if it's not in the visible list
    if (!visibleMonths.contains(selectedMonth.value)) {
        selectedMonth.value = visibleMonths.firstOrNull() ?: today.month
    }

    Dialog(onDismissRequest = { onDismiss() }) {

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(26.dp))
                .background(Color.White)
                .padding(20.dp)
        ) {

            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                Text(
                    text = "Select Month & Year",
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {

                    // ⭐ MONTH PICKER
                    androidx.compose.foundation.lazy.LazyColumn {
                        items(visibleMonths.size) { index ->
                            val m = visibleMonths[index]
                            Text(
                                text = m.getDisplayName(TextStyle.FULL, Locale.getDefault()),
                                fontSize = 15.sp,
                                color = if (m == selectedMonth.value) Color(0xFF4A35C9) else Color.Black,
                                modifier = Modifier
                                    .padding(6.dp)
                                    .clickable( interactionSource = remember { MutableInteractionSource() },
                                        indication = null) {
                                        selectedMonth.value = m
                                    },
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    // ⭐ YEAR PICKER
                    androidx.compose.foundation.lazy.LazyColumn {
                        items(years.size) { index ->
                            val y = years[index]
                            Text(
                                text = y.toString(),
                                fontSize = 15.sp,
                                color = if (y == selectedYear.value) Color(0xFF4A35C9) else Color.Black,
                                modifier = Modifier
                                    .padding(6.dp)
                                    .clickable( interactionSource = remember { MutableInteractionSource() },
                                        indication = null) {
                                        selectedYear.value = y
                                    },
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // CONFIRM BUTTON
                Button(
                    onClick = {
                        onSelect(YearMonth.of(selectedYear.value, selectedMonth.value))
                        onDismiss()
                    },
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A35C9)),
                    modifier = Modifier.width(140.dp).height(45.dp)
                ) {
                    Text("Done", color = Color.White, fontSize = 14.sp)
                }
            }
        }
    }
}
