package com.bussiness.curemegptapp.ui.screen.main.healthReports

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.bussiness.curemegptapp.ui.component.PriorityImageTag


@Composable
fun ReportCard(
    icon: Int,
    title: String,
    patientName: String,
    priority: String,
    date: String,
    note: String,
    filesCount: Int,
    onViewClick: () -> Unit,
    onShareClick: () -> Unit
) {


    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(40.dp),
        color = Color(0xFFF6F5FC),
        border = BorderStroke(1.dp, Color(0xFFE0DFE5))
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth().height(IntrinsicSize.Min)
                .padding(20.dp)
        ) {

            // ---------- ICON ----------
            Box(
                modifier = Modifier
                    .width(60.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(30.dp))
                    .border(
                        color = Color(0xFF181818),
                        width = 2.dp,
                        shape = RoundedCornerShape(40.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // ---------- CONTENT ----------
            Column(modifier = Modifier.weight(1f)) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {

                        Text(
                            text = title,
                            fontSize = 16.sp,
                            fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                            fontWeight = FontWeight.Medium,
                            maxLines = 1
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "For: $patientName",
                            fontSize = 14.sp,
                            color = Color(0xFF4338CA),
                            fontFamily = FontFamily(Font(R.font.urbanist_regular))
                        )
                    }

                    ViewShareMenu(
                        onViewClick = onViewClick,
                        onShareClick = onShareClick
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // ---------- PRIORITY TAG ----------
                PriorityImageTag(
                    label = priority,
                    color = if (priority == "Attention") Color(0xFFF31D1D) else Color(0xFF19BB9B),
                    backgroundColor = if (priority == "Attention") Color(0xFFF6DFE6) else Color(0xFFD3ECEC),
                    borderColor = if (priority == "Attention") Color(0xFFF31D1D) else Color(0xFF19BB9B),
                    icon = if (priority == "Attention") R.drawable.ic_attention_icon_red else R.drawable.ic_normal_icon_green
                )

                Spacer(modifier = Modifier.height(10.dp))

                // ---------- DATE ----------
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_date_icon_rotate),
                        contentDescription = null,
                        modifier = Modifier.size(29.dp),
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = date,
                        fontSize = 14.sp,
                        color = Color.Black,
                        fontFamily = FontFamily(Font(R.font.urbanist_regular))
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // ---------- NOTE / DESCRIPTION ----------
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_note_health_icon),
                        contentDescription = null,
                        modifier = Modifier.size(29.dp),
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = note,
                        fontSize = 12.sp,
                        lineHeight = 14.sp,
                        color = Color.Black,
                        fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                        fontWeight = FontWeight.Normal,
                        maxLines = 3
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                }

                // ---------- FILE COUNT ----------
                Surface(
                    shape = RoundedCornerShape(30.dp),
                    color = Color(0xFFE4E2F7),
                    modifier = Modifier.padding(top = 10.dp),
                    border = BorderStroke(1.dp, Color(0xFFD4D1F2))
                ) {
                    Text(
                        text = "$filesCount Files",
                        fontSize = 10.sp,
                        color = Color(0xFF4338CA),
                        fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }
        }
    }
}





@Preview(showBackground = true)
@Composable
fun ReportCardPreview() {
    val navController = rememberNavController()
   // ReportCard()
}