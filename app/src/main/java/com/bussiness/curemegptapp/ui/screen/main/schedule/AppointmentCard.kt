package com.bussiness.curemegptapp.ui.screen.main.schedule

import android.util.Log
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bussiness.curemegptapp.R
import com.bussiness.curemegptapp.ui.component.GradientRedButton
import com.bussiness.curemegptapp.ui.component.GradientViewSummaryButton
import com.bussiness.curemegptapp.ui.dialog.AlertCardDialog
import com.bussiness.curemegptapp.ui.viewModel.main.AppointmentUIModel

@Composable
fun AppointmentCard(appointment: AppointmentUIModel, onEditClick: () -> Unit,
                    onDeleteClick: () -> Unit, onViewClick: () -> Unit,onCheckClick :() ->Unit) {
    var showMenu by remember { mutableStateOf(false) }

    var checkedState by remember { mutableStateOf(if(appointment.completeStatus==0) false else true ) }
    Log.d("TESTING_CHECK_STATE","Checked State is "+checkedState)
    Surface(
        modifier = Modifier.fillMaxWidth().wrapContentHeight(),
        shape = RoundedCornerShape(40.dp),
        color = Color(0xFFF9F9FD),
        border =  BorderStroke(1.dp, Color(0xFFE7E6F8))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min) .padding(20.dp)
        ) {
            // Icon Column
            Box(
                modifier = Modifier
                    .width(54.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(30.dp))
                    .border(color = Color(0xFF181818), width = 1.dp, shape = RoundedCornerShape(40.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = appointment.icon),
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
                    Column(   modifier = Modifier.weight(1f)) {
                        Text(
                            text = appointment.title,
                            fontSize = 15.sp,
                            fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                            fontWeight = FontWeight.Medium,

                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = appointment.doctor,
                            fontSize = 13.sp,
                            color = Color(0xFF374151),
                            fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                            fontWeight = FontWeight.Medium
                        )
                    }



                    PostContentMenu(
                        modifier = Modifier,
                        checked = checkedState,
                        onCheckedChange = {
                            checkedState = it
                            onCheckClick()
                                          },
                        onEditClick = { onEditClick()},
                        onDeleteClick = { onDeleteClick()  })
                }






                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "For: ${appointment.patientName}",
                    fontSize = 13.sp,
                    color = Color(0xFF4338CA),
                    fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                    fontWeight = FontWeight.Normal
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Date and Time
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_calender_health_icon),
                        contentDescription = null,
                        modifier = Modifier.size(29.dp),

                        )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = appointment.date,
                        fontSize = 14.sp,
                        color = Color.Black,
                        fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                        fontWeight = FontWeight.Normal
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Image(
                        painter = painterResource(id = R.drawable.ic_date_health_icon),
                        contentDescription = null,
                        modifier = Modifier.size(29.dp),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = appointment.time,
                        fontSize = 14.sp,
                        color = Color.Black,
                        fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                        fontWeight = FontWeight.Normal
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Location
                Row(
                    verticalAlignment = Alignment.Top
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_location_health_icon),
                        contentDescription = null,
                        modifier = Modifier.size(29.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = appointment.location,
                        fontSize = 14.sp,
                        color = Color.Black,
                        fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                        fontWeight = FontWeight.Normal,
                        lineHeight = 20.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Description
                Row(
                    verticalAlignment = Alignment.Top
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_note_health_icon),
                        contentDescription = null,
                        modifier = Modifier.size(29.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = appointment.description,
                        fontSize = 14.sp,
                        color = Color.Black,
                        fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                        fontWeight = FontWeight.Normal
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if(appointment.isVisibleItem){
                    // View Summary Button
                    GradientViewSummaryButton(
                        text = "View Summary",
                        icon = R.drawable.ic_summary_view_icon1,
                        width = 155.dp,
                        height = 35.dp,
                        fontSize = 14.sp,
                        imageSize = 18.dp,
                        verticalPadding = 5.dp,
                        modifier = Modifier.align(Alignment.End),
                        gradientColors = listOf(
                            Color(0xFF4338CA),
                            Color(0xFF211C64)
                        ),
                        onClick = { onViewClick() }
                    )
                }


            }
        }
    }
}