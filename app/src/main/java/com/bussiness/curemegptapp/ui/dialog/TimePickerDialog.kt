package com.bussiness.curemegptapp.ui.dialog


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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.compose.rememberNavController
import com.bussiness.curemegptapp.R
import com.bussiness.curemegptapp.ui.component.CancelButton
import com.bussiness.curemegptapp.ui.component.GradientButton2
import java.time.format.DateTimeFormatter


@Composable
fun TimePickerDialog(
    onDismiss: () -> Unit,
    onTimeApplied: (String) -> Unit
) {

    val hourList = (1..12).map { String.format("%02d", it) }
    val minuteList = (0..59).map { String.format("%02d", it) }

    val selectedHour = remember { mutableStateOf(hourList[0]) }
    val selectedMinute = remember { mutableStateOf(minuteList[0]) }
    val isAM = remember { mutableStateOf(true) }

    Dialog(onDismissRequest = { onDismiss() },   properties = DialogProperties(
        dismissOnClickOutside = false, // 🔴 IMPORTANT
        dismissOnBackPress = false       // back press se band chahiye to true
    )) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(40.dp))
                .background(Color.White)
                .padding(horizontal = 15.dp, vertical = 18.dp)
        ) {

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // ---------- Header ----------
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_time_icon),
                            contentDescription = "",
                            modifier = Modifier.size(35.dp)
                        )

                        Spacer(Modifier.width(8.dp))

                        Text(
                            text = "Select Time",
                            fontSize = 15.sp,
                            color = Color.Black,
                            fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Image(
                        painter = painterResource(id = R.drawable.ic_close),
                        contentDescription = "Close",
                        modifier = Modifier
                            .size(43.dp)
                            .clickable( interactionSource = remember { MutableInteractionSource() },
                                indication = null) { onDismiss() }
                    )
                }

                Spacer(Modifier.height(20.dp))

                // ---------- Time Picker Row ----------
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    // Hour Dropdown
                    DropdownField(
                        items = hourList,
                        selected = selectedHour.value,
                        onSelect = { selectedHour.value = it },
                        modifier = Modifier.width(80.dp).height(36.dp)
                    )

                    Text(
                        ":",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.padding(horizontal = 6.dp)
                    )

                    // Minute Dropdown
                    DropdownField(
                        items = minuteList,
                        selected = selectedMinute.value,
                        onSelect = { selectedMinute.value = it },
                        modifier = Modifier.width(80.dp).height(36.dp)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    // AM/PM Toggle
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(36.dp)
                            .clip(RoundedCornerShape(40.dp))
                            .background( Color(0xFFEEEEEF) ),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            modifier = Modifier.width(105.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            TimePeriodButton("AM", isSelected = isAM.value) {
                                isAM.value = true
                            }
                            Spacer(Modifier.height(8.dp))
                            TimePeriodButton("PM", isSelected = !isAM.value) {
                                isAM.value = false
                            }
                        }
                    }
                }

                Spacer(Modifier.height(25.dp))



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
                            val time = "${selectedHour.value}:${selectedMinute.value} ${if (isAM.value) "AM" else "PM"}"
                            onTimeApplied(time)
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




@Composable
fun DropdownField(
    items: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val expanded = remember { mutableStateOf(false) }

    Box(modifier = modifier) {

        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(25.dp))
                .background(Color(0xFFF5F5F5))
                .clickable( interactionSource = remember { MutableInteractionSource() },
                    indication = null) { expanded.value = true }
                .padding(horizontal = 12.dp, vertical = 4.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selected,
                fontSize = 17.sp,
                color = Color.Black
            )
            Image(
                painter = painterResource(R.drawable.ic_dropdown_icon),
                contentDescription = "",
                modifier = Modifier.size(8.dp)
            )
        }

        androidx.compose.material3.DropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false },
            modifier = Modifier.height(110.dp),
            containerColor = Color.White,
        ) {
            items.forEach { item ->
                androidx.compose.material3.DropdownMenuItem(
                    text = {
                        Text(
                            text = item,
                            fontSize = 18.sp,
                            color = Color.Black
                        )
                    },
                    onClick = {
                        onSelect(item)
                        expanded.value = false
                    }
                )
            }
        }
    }
}

@Composable
fun TimePeriodButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(50.dp)
            .height(32.dp)
            .clip(RoundedCornerShape(47.dp))
            .background(if (isSelected) Color(0xFF4A35C9) else Color(0xFFF5F5F5))
            .clickable( interactionSource = remember { MutableInteractionSource() },
                indication = null) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 13.sp,
            fontFamily = FontFamily(Font(R.font.urbanist_regular)),
            fontWeight = FontWeight.Normal,
            color = if (isSelected) Color.White else Color(0xFF181818)
        )
    }
}


@Preview(showBackground = true)
@Composable
fun TimePickerDialogPreview() {
    val navController = rememberNavController()
    TimePickerDialog(onDismiss = {}, onTimeApplied = {}

    )
}

