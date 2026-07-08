package com.bussiness.curemegptapp.ui.screen.main.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.bussiness.curemegptapp.util.AppConstant
import com.bussiness.curemegptapp.R
import com.bussiness.curemegptapp.apimodel.homemodel.Family
import com.bussiness.curemegptapp.data.model.HealthProfile
import com.bussiness.curemegptapp.ui.component.AppointmentBox
import com.bussiness.curemegptapp.ui.component.GradientRedButton
import com.bussiness.curemegptapp.ui.viewModel.main.HomeViewModel


val profiles = listOf(
    HealthProfile(
        id = 1,
        name = "James Logan",
        age = "40 yrs",
        relation = "Self",
        lastCheckup = "45 days ago",
        alerts = listOf(
            "Blood pressure medication reminder",
            "Annual checkup due"
        )
    ),
    HealthProfile(
        id = 2,
        name = "Rose Logan",
        age = "38 yrs",
        relation = "Spouse",
        lastCheckup = "20 days ago",
        alerts = listOf(
            "Vitamin D reminder"
        )
    ),
    HealthProfile(
        id = 3,
        name = "Peter Logan",
        age = "12 yrs",
        relation = "Son",
        lastCheckup = "10 days ago",
        alerts = listOf(
            "Vaccination due"
        )
    )
)


@Composable
fun HealthOverviewSection(
    viewModel: HomeViewModel,
    alerts: MutableList<Family>?,
    onAddClick: () -> Unit,
    onEditClick: (Family) -> Unit,
    onSchedule: () -> Unit,
    onAskAi: () -> Unit,
    onAppointmentClick: () -> Unit
) {


    /*val profiles = remember { profiles }

    var selectedProfile by remember {
        mutableStateOf(profiles.first())
    }*/

    var selectedProfile by remember(alerts) {
        mutableStateOf(
            alerts?.firstOrNull { it.isSelected == true }
                ?: alerts?.firstOrNull()
        )
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.health_overview_title),
            fontSize = 20.sp,
            fontFamily = FontFamily(Font(R.font.urbanist_medium)),
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )

        GradientRedButton(
            text = stringResource(R.string.add_button_text)/*"Add"*/,
            icon = R.drawable.ic_plus_normal_icon,
            width = 88.dp,
            height = 42.dp,
            fontSize = 14.sp,
            imageSize = 16.dp,
            gradientColors = listOf(
                Color(0xFF4338CA),
                Color(0xFF211C64)
            ),
            onClick = { onAddClick() }
        )
    }
    Spacer(modifier = Modifier.height(20.dp))

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(alerts ?: emptyList()) { profile ->
            ProfileTab(
                name = profile.name.orEmpty(),
                isSelected = profile.isSelected == true,
                onClick = {
                    viewModel.onProfileSelected(profile)
                }
            )
        }
    }


    Spacer(modifier = Modifier.height(42.dp))

    UserHealthCard(
        profile = selectedProfile,
        allProfiles = alerts,
        onEditClick = {
            selectedProfile?.let { onEditClick(it) }
        },
        onSchedule = { onSchedule() },
        onAskAi = { onAskAi() },
        onAppointmentClick = { onAppointmentClick() }
    )

    Spacer(modifier = Modifier.height(71.dp))
}

@Composable
fun ProfileTab(
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() },
        shape = RoundedCornerShape(50.dp),
        color = if (isSelected) Color(0xFF3C3C3C) else Color.White,
        border = if (!isSelected) BorderStroke(1.dp, Color(0xFF697383)) else null
    ) {
        Box(
            modifier = Modifier.padding( horizontal = 16.dp,
                vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = name,
                fontSize = 14.sp,
                color = if (isSelected) Color.White else Color(0xFF697383)
            )
        }
    }
}


@Composable
fun UserHealthCard(
    profile: Family?,
    allProfiles: List<Family>?,
    onEditClick: () -> Unit,
    onSchedule: () -> Unit,
    onAskAi: () -> Unit,
    onAppointmentClick: () -> Unit
) {


    val appointmentText = remember(profile, allProfiles) {
        var closestDaysDiff: Int? = null
        val profilesToCheck = if (!allProfiles.isNullOrEmpty()) allProfiles else listOfNotNull(profile)
        
        profilesToCheck.forEach { family ->
            family.active_alerts?.appointments?.filter { it.complete_status != 1 && !it.date.isNullOrBlank() }?.forEach { appt ->
                val apptDate = parseDate(appt.date!!)
                if (apptDate != null) {
                    val todayCal = java.util.Calendar.getInstance()
                    val apptCal = java.util.Calendar.getInstance().apply { time = apptDate }
                    
                    todayCal.set(java.util.Calendar.HOUR_OF_DAY, 0)
                    todayCal.set(java.util.Calendar.MINUTE, 0)
                    todayCal.set(java.util.Calendar.SECOND, 0)
                    todayCal.set(java.util.Calendar.MILLISECOND, 0)
                    
                    apptCal.set(java.util.Calendar.HOUR_OF_DAY, 0)
                    apptCal.set(java.util.Calendar.MINUTE, 0)
                    apptCal.set(java.util.Calendar.SECOND, 0)
                    apptCal.set(java.util.Calendar.MILLISECOND, 0)
                    
                    val daysDiff = ((apptCal.timeInMillis - todayCal.timeInMillis) / (1000 * 60 * 60 * 24)).toInt()
                    if (daysDiff >= 0) {
                        if (closestDaysDiff == null || daysDiff < closestDaysDiff!!) {
                            closestDaysDiff = daysDiff
                        }
                    }
                }
            }
        }
        
        when {
            closestDaysDiff == null -> "No Appointment"
            closestDaysDiff == 0 -> "Today"
            closestDaysDiff == 1 -> "1 Day Left"
            else -> "Appointment Scheduled in $closestDaysDiff Days"
        }
    }

    val alerts = listOf(
        "Blood pressure medication reminder",
        "Annual checkup due"
    )


    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0EFFB)),
        shape = RoundedCornerShape(30.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .width(70.dp)
                            .height(70.dp)
                            .clip(RoundedCornerShape(20.dp))

                    ) {
                        val imageUrl = profile?.profile_image?.let {
                            if (it.isEmpty()) null
                            else if (it.startsWith("http://") || it.startsWith("https://")) it
                            else AppConstant.IMAGE_BASE_URL + it
                        }
                        if (!imageUrl.isNullOrEmpty() && 
                            imageUrl != AppConstant.IMAGE_BASE_URL && 
                            imageUrl != "https://curemegpt.tgastaging.com") {
                            AsyncImage(
                                model = imageUrl,
                                contentDescription = null,
                                modifier = Modifier.matchParentSize(),
                                contentScale = ContentScale.Crop,
                                placeholder = painterResource(id = R.drawable.user_not_found),
                                error = painterResource(id = R.drawable.user_not_found)
                            )
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.user_not_found),
                                contentDescription = null,
                                modifier = Modifier.matchParentSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(7.dp))
                    Column {

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = profile?.name?:"",
                                fontSize = 12.sp,
                                fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.width(100.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Surface(
                                modifier = Modifier.wrapContentWidth(),
                                shape = RoundedCornerShape(20.dp),
                                color = Color(0xFFE8E4FF)
                            ) {
                                Text(
                                    text = profile?.dob?:"",
                                    fontSize = 11.sp,
                                    color = Color(0xFF4338CA),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                                    fontWeight = FontWeight.Medium,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                        Text(
                            text = if (profile?.name?.contains("Myself", ignoreCase = true) == true) {
                                "self"
                            } else {
                                "family"
                            },
                            fontSize = 12.sp,
                            color = Color(0xFF374151),
                            fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                            fontWeight = FontWeight.Medium
                        )
                        if (profile?.last_appointment_days_ago != null) {
                            val lastCheckupText = when (val days = profile.last_appointment_days_ago) {
                                0 -> "Today"
                                1 -> "1 day ago"
                                else -> "$days days ago"
                            }
                            Text(
                                text = stringResource(R.string.last_checkup_format, lastCheckupText),
                                fontSize = 12.sp,
                                color = Color(0xFF374151),
                                fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Image(
                    painter = painterResource(id = R.drawable.ic_edit_icon_cirlcular),
                    contentDescription = "Edit",
                    modifier = Modifier
                        .size(43.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            onEditClick()
                        }
                )

            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.active_alerts_title)/*"Active Alerts"*/,
                fontSize = 14.sp,
                fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                fontWeight = FontWeight.Medium,
                color = Color(0xFF697383)
            )

            val alertItems = remember(profile) {
                val list = mutableListOf<String>()
                profile?.active_alerts?.let { activeAlerts ->
                    activeAlerts.medications?.forEach { med ->
                        val name = med.medication_name
                        if (!name.isNullOrBlank()) {
                            list.add("$name medication reminder")
                        } else {
                            list.add("Medication reminder")
                        }
                    }
                    activeAlerts.appointments?.forEach { appt ->
                        val desc = appt.description
                        if (!desc.isNullOrBlank()) {
                            list.add(desc.removeSuffix("."))
                        } else {
                            list.add("Appointment reminder")
                        }
                    }
                }
                list
            }

            if (alertItems.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    alertItems.forEach { alert ->
                        AlertItem(alertText = alert)
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "No active alerts",
                    fontSize = 13.sp,
                    fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                GradientRedButton(
                    text = stringResource(R.string.schedule_button_text),
                    icon = R.drawable.ic_schedule_attention_icon,
                    width = 95.dp,
                    height = 40.dp,
                    imageSize = 13.dp,
                    fontSize = 11.sp,
                    horizontalPadding = 8.dp,
                    onClick = { onSchedule() }
                )
                GradientRedButton(
                    text = stringResource(R.string.ask_ai_button_text),
                    icon = R.drawable.ic_ask_ai_icon,
                    width = 85.dp,
                    height = 40.dp,
                    imageSize = 13.dp,
                    fontSize = 11.sp,
                    horizontalPadding = 8.dp,
                    gradientColors = listOf(
                        Color(0xFF4338CA),
                        Color(0xFF211C64)
                    ),
                    onClick = { onAskAi() }
                )
                AppointmentBox(
                    text = appointmentText,
                    modifier = Modifier
                        .heightIn(min = 40.dp)
                        .weight(1f),
                    iconRes = R.drawable.ic_appointed_icon,
                    onClick = { onAppointmentClick() }
                )
            }

        }
    }
}

@Composable
fun AlertItem(alertText: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFDFD5FC), RoundedCornerShape(50.dp))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_alert_notification),
            contentDescription = null,
            modifier = Modifier.size(29.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = alertText,
            fontSize = 13.sp,
            color = Color(0xFF181818),
            modifier = Modifier.weight(1f),
            fontFamily = FontFamily(Font(R.font.urbanist_medium)),
            fontWeight = FontWeight.Medium

        )
    }
}

fun parseDate(dateStr: String): java.util.Date? {
    if (dateStr.isBlank()) return null
    val cleanDate = dateStr.trim().split(" ")[0].split("T")[0]
    
    if (cleanDate.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))) {
        try {
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
            sdf.isLenient = false
            return sdf.parse(cleanDate)
        } catch (e: Exception) {}
    }
    
    val formats = listOf(
        "MM-dd-yyyy",
        "dd-MM-yyyy",
        "yyyy-MM-dd",
        "M-d-yyyy",
        "d-M-yyyy",
        "yyyy-M-d"
    )
    
    for (format in formats) {
        try {
            val sdf = java.text.SimpleDateFormat(format, java.util.Locale.US)
            sdf.isLenient = false
            val parsed = sdf.parse(cleanDate)
            if (parsed != null) return parsed
        } catch (e: Exception) {
            // Try next format
        }
    }
    return null
}
