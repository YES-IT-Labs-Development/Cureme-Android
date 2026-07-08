package com.bussiness.curemegptapp.ui.screen.main.alert

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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.bussiness.curemegptapp.R
import com.bussiness.curemegptapp.ui.component.RoundedCustomCheckbox
import com.bussiness.curemegptapp.ui.screen.main.scheduleNewAppointment.ScheduleNewAppointmentScreen
import com.bussiness.curemegptapp.util.TimeFormatter

@Composable
fun AlertCard(
    name: String,
    title: String,
    description: String,
    time: String,
    showTags: Boolean,
    priority: String = "",
    actionRequired: Boolean = false,
    readStatus: Boolean = false,
    checkBoxShow: Boolean = false,
    onCheckedChange: (Boolean) -> Unit
) {
    var checked by remember { mutableStateOf(readStatus) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(30.dp))
            .border(1.dp, Color(0xFFE7E6F8), RoundedCornerShape(30.dp))
            .background(Color(0xFFF9F9FD))
            .padding(20.dp)
    ) {

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = "For: $name",
                color = Color(0xFF4338CA),
                fontSize = 12.sp,
                fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                fontWeight = FontWeight.Medium
            )


        }

        Spacer(Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {

            Text(
                text = title,
                color = Color.Black,
                fontSize = 16.sp,
                fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = TimeFormatter.format(time),
                color = Color(0xFF4338CA),
                fontSize = 14.sp,
                fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                fontWeight = FontWeight.Medium
            )
        }
        Spacer(Modifier.height(8.dp))

        Text(
            text = description,
            color = Color(0xFF404657),
            fontSize = 14.sp,
            lineHeight = 20.sp,
            fontFamily = FontFamily(Font(R.font.urbanist_medium)),
            fontWeight = FontWeight.Medium
        )

        Spacer(Modifier.height(12.dp))

        if (showTags) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {

                PriorityTag(
                    label = priority,
                    color = if (priority == "High") Color(0xFFEB5757) else Color(0xFFFFA24C),
                    borderColor = if (priority == "High") Color(0xFFF31D1D) else Color(0xFFF36F1D),
                    backGroundColor = if (priority == "High") Color(0xFFF8E3E7) else Color(0xFFF8EBE7),
                    )

                if (actionRequired) {
                    ActionRequiredTag()
                }
            }

            Spacer(Modifier.height(12.dp))
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,

        ) {

            Image(
                painter = painterResource(id = R.drawable.ic_alert_clock_icon),
                contentDescription = "",

                modifier = Modifier
                    .size(48.dp)


            )

            Spacer(Modifier.weight(1f))

            if (checkBoxShow){
                Row(
                    modifier = Modifier.clickable( interactionSource = remember { MutableInteractionSource() },
                        indication = null){  onCheckedChange(!readStatus) },
                    verticalAlignment = Alignment.CenterVertically
                ) {


                    RoundedCustomCheckbox(
                        checked = readStatus,
                        onCheckedChange = { onCheckedChange(it) }
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Mark As Complete",
                        fontSize = 16.sp,
                        color = Color.Black,
                        fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                        fontWeight = FontWeight.Normal
                    )
                }
            }

        }
    }
}

@Composable
fun PriorityTag(label: String, color: Color,borderColor : Color,backGroundColor: Color) {
    Box(
        modifier = Modifier
            .border(1.dp, borderColor, RoundedCornerShape(50.dp))
            .background(backGroundColor,RoundedCornerShape(50.dp))
            .padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        Text(
            text = label,
            color = color,
            fontSize = 10.sp,
            fontFamily = FontFamily(Font(R.font.urbanist_medium)),
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun ActionRequiredTag() {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color(0xFFE74C3C), Color(0xFFAA0015))
                )
            )
            .padding(vertical = 5.dp, horizontal = 14.dp)
    ) {
        Text(
            text = "Action Required",
            color = Color.White,
            fontSize = 12.sp,
            fontFamily = FontFamily(Font(R.font.urbanist_medium)),
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF,
    showSystemUi = false
)
@Composable
fun AlertCardPreview() {
    AlertCard(
        name = "John Doe",
        title = "Medication Reminder",
        description = "Your blood pressure medication is due in 30 minutes. Please take it with water after a light meal.",
        time = "2026-07-01T10:30:00", // Replace with the format expected by TimeFormatter
        showTags = true,
        priority = "High",
        actionRequired = true,
        readStatus = false,
        checkBoxShow = true,
        onCheckedChange = {}
    )
}