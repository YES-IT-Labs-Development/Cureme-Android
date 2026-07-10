package com.bussiness.curemegptapp.ui.screen.main.schedule

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bussiness.curemegptapp.R
import com.bussiness.curemegptapp.apimodel.medication.Medication
import com.bussiness.curemegptapp.apimodel.medication.MedicationTime
import com.bussiness.curemegptapp.ui.component.GradientRedButton

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MedicationsCard( medication: Medication,
                     onEditClick: () -> Unit,
                     onDeleteClick: () -> Unit) {

    var timeStates by remember { mutableStateOf(medication.times.map { true }) }
    var checkedState by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxWidth().wrapContentHeight(),
        shape = RoundedCornerShape(40.dp),
        color = Color(0xFFF9F9FD),
        border =  BorderStroke(1.dp, Color(0xFFE7E6F8))
    ) {

        Row(
            modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min).padding(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(54.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(30.dp))
                    .border(
                        color = Color(0xFF181818),
                        width = 1.dp,
                        shape = RoundedCornerShape(40.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = medication.icon),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Content Column
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {

                        Text(
                            text = medication.title,
                            fontSize = 16.sp,
                            fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                            fontWeight = FontWeight.Medium,
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "For: ${medication.patientName}",
                            fontSize = 14.sp,
                            color = Color(0xFF4338CA),
                            fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                            fontWeight = FontWeight.Normal
                        )

                    }

                    EditDeleteMenu(
                        modifier = Modifier,
                       onEditClick = { onEditClick() },
                        onDeleteClick = {  onDeleteClick() })
                }


                Spacer(modifier = Modifier.height(4.dp))


                // Medication Type
                Text(
                    text = "Medication Type: ${medication.medicationType}",
                    fontSize = 14.sp,
                    color = Color.Black,
                    fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                    fontWeight = FontWeight.Normal
                )

                Spacer(modifier = Modifier.height(13.dp))


                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFFE8E4FF)
                ) {
                    Text(
                        text = medication.frequency,
                        fontSize = 10.sp,
                        color = Color(0xFF4338CA),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 1.dp),
                        fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Days
                if (!medication.days.isNullOrEmpty()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_sun_mon),
                            contentDescription = null,
                            modifier = Modifier.size(29.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        medication.days.let {
                            Text(
                                text = it,
                                fontSize = 14.sp,
                                color = Color.Black,
                                fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                                fontWeight = FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Times with Checkboxes - Using Manual Grid (2 columns)

                val times = medication.times

                Row{
                    Image(
                        painter = painterResource(id = R.drawable.ic_clck_icon),
                        contentDescription = null,
                        modifier = Modifier.size(29.dp)
                    )
                    Spacer(modifier = Modifier.width(9.dp))


                    Column {
                    // First row (2 items)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // First column items
                        times.take(2).forEachIndexed { index, timeSlot ->
                            Box(
                                modifier = Modifier.weight(1f)
                            ) {
                                TimeSlotItem(
                                    time = timeSlot.time,

                                )
                            }
                        }
                    }

                    // Second row (2 items) - if we have more than 2 times
                    if (times.size > 2) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Second column items
                            times.drop(2).take(2).forEachIndexed { originalIndex, timeSlot ->
                                val index = originalIndex + 2
                                Box(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    TimeSlotItem(
                                        time = timeSlot.time,
                                    )
                                }
                            }
                        }
                    }

                        if (times.size > 4) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Second column items
                                times.drop(4).take(2).forEachIndexed { originalIndex, timeSlot ->
                                    val index = originalIndex + 2
                                    Box(
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        TimeSlotItem(
                                            time = timeSlot.time,
                                        )
                                    }
                                }
                            }
                        }
                        if (times.size > 6) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Second column items
                                times.drop(6).take(2).forEachIndexed { originalIndex, timeSlot ->
                                    val index = originalIndex + 2
                                    Box(
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        TimeSlotItem(
                                            time = timeSlot.time,
                                        )
                                    }
                                }
                            }
                        }

                        if (times.size > 8) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Second column items
                                times.drop(8).take(2).forEachIndexed { originalIndex, timeSlot ->
                                    val index = originalIndex + 2
                                    Box(
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        TimeSlotItem(
                                            time = timeSlot.time,
                                        )
                                    }
                                }
                            }
                        }

                        if (times.size > 10) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                times.drop(10).take(2).forEachIndexed { originalIndex, timeSlot ->
                                    val index = originalIndex + 2
                                    Box(
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        TimeSlotItem(
                                            time = timeSlot.time,
                                        )
                                    }
                                }
                            }
                        }
                   }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_strt_end),
                        contentDescription = null,
                        modifier = Modifier.size(29.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "${medication.startDate}",
                        fontSize = 14.sp,
                        color = Color.Black,
                        fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                        fontWeight = FontWeight.Normal
                    )
                    Spacer(modifier = Modifier.width(18.dp))

                    Image(
                        painter = painterResource(id = R.drawable.ic_strt_end),
                        contentDescription = null,
                        modifier = Modifier.size(29.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "${medication.endDate}",
                        fontSize = 14.sp,
                        color = Color.Black,
                        fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                        fontWeight = FontWeight.Normal
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_dc_note),
                        contentDescription = null,
                        modifier = Modifier.size(29.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = medication.instructions,
                        fontSize = 14.sp,
                        color = Color.Black,
                        fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                        fontWeight = FontWeight.Normal,
                        lineHeight = 20.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }



            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TimeSlotItem(
    time: String,
) {
    val formattedTime = remember(time) {
        try {
            val cleanTime = time.trim()
            val parsedTime = try {
                LocalTime.parse(cleanTime, DateTimeFormatter.ofPattern("HH:mm:ss"))
            } catch (e: Exception) {
                try {
                    LocalTime.parse(cleanTime, DateTimeFormatter.ofPattern("HH:mm"))
                } catch (e2: Exception) {
                    try {
                        LocalTime.parse(cleanTime, DateTimeFormatter.ofPattern("hh:mm a"))
                    } catch (e3: Exception) {
                        try {
                            LocalTime.parse(cleanTime, DateTimeFormatter.ofPattern("h:mm a"))
                        } catch (e4: Exception) {
                            null
                        }
                    }
                }
            }
            parsedTime?.format(DateTimeFormatter.ofPattern("hh:mm a")) ?: time
        } catch (e: Exception) {
            time
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(50.dp))
            .border(
                width = 1.dp,
                color = Color(0xFF4338CA),
                shape = RoundedCornerShape(50.dp)
            )
            .padding(horizontal = 4.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
    ) {
        Text(
            text = formattedTime,
            fontSize = 12.sp,
            color = Color.Black,
            fontFamily = FontFamily(Font(R.font.urbanist_regular)),
            fontWeight = FontWeight.Normal
        )
    }
}


@Preview(showBackground = true)
@Composable
fun MedicationsCardPreview() {
    val sampleMedication = Medication(
        icon = R.drawable.ic_medication_icon,
        title = "Albuterol Inhaler 2 puffs",
        patientName = "Peter Logan",
        medicationType = "Medication",
        frequency = "Weekly",
        days = "Monday, Tuesday",
        times = listOf(
            MedicationTime("09:00 AM", false),
            MedicationTime("09:00 PM", false),
            MedicationTime("10:00 AM", false),
            MedicationTime("04:00 PM", false)
        ),
        startDate = "08/28/2025",
        endDate = "10/28/2025",
        instructions = "For asthma symptoms",
        isVisibleItem = true,
        id=0
    )

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        MedicationsCard(medication = sampleMedication, onEditClick = {}, onDeleteClick = {})
    }
}