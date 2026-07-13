package com.bussiness.curemegptapp.ui.screen.main.reports


import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.bussiness.curemegptapp.R
import com.bussiness.curemegptapp.ui.component.DocumentItem2
import com.bussiness.curemegptapp.ui.component.PriorityImageTag
import com.bussiness.curemegptapp.ui.component.TopBarHeader2
import com.bussiness.curemegptapp.viewmodel.reportviewmodel.ReportViewModel

@Composable
fun ReportScreen(navController: NavHostController,id: String? = "",viewModel: ReportViewModel = hiltViewModel()) {


    Log.d("ReportScreen", "Displaying report with ID: $id")
    val state by viewModel.uiStateDetails.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getReportDetailsRequest(id?:"")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFFFF)).statusBarsPadding()
    ) {
        val shareChatMessage = stringResource(R.string.share_chat_message)
        val context = LocalContext.current
        val attachmentList = listOf(
            stringResource(R.string.attachment_xray),
            stringResource(R.string.attachment_analysis_report),
            stringResource(R.string.attachment_blood_test),
            stringResource(R.string.attachment_prescription)
          /*  "xray_001.jpg",
            "analysis_report.pdf",
            "blood_test_result.png",
            "prescription_2025.pdf"*/
        )


        val rawSeverity = state?.severity
        val priority = if (rawSeverity.isNullOrBlank()) "" else rawSeverity.replaceFirstChar { it.uppercase() }

        TopBarHeader2(title = stringResource(R.string.back_to_reports)/*"Back to Reports"*/, onBackClick = {navController.navigateUp()})

        Spacer(modifier = Modifier.height(8.dp))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(19.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.Top
            ) {
                // Icon Box
                Box(
                    modifier = Modifier
                        .size(48.dp,140.dp)
                        .background(Color(0xFF4C3FCC), RoundedCornerShape(30.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                      painter = painterResource(id = R.drawable.ic_report_mini_icon),
                        contentDescription = null,
                        modifier = Modifier.size(19.dp,23.dp)
                    )
                }

                // Title and Info
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = state?.title?:"",
                        fontSize = 23.sp,
                        fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF1A1A1A),
                        maxLines = 1
                    )

                    if (priority.isNotBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        val isHigh = priority.equals("Attention", ignoreCase = true) || priority.equals("High", ignoreCase = true)
                        PriorityImageTag(
                            label = priority,
                            color = if (isHigh) Color(0xFFF31D1D) else Color(0xFF19BB9B),
                            backgroundColor = if (isHigh) Color(0xFFF6DFE6) else Color(0xFFD3ECEC),
                            borderColor = if (isHigh) Color(0xFFF31D1D) else Color(0xFF19BB9B),
                            icon = if (isHigh) R.drawable.ic_attention_icon_red else R.drawable.ic_normal_icon_green
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    } else {
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    // Date
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(29.dp),
                            color = Color.White,
                            shadowElevation = 8.dp
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_calender_health_icon),
                                contentDescription = null,
                                modifier = Modifier.size(29.dp),
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = state?.chat_date?:"",
                            fontSize = 16.sp,
                            color = Color.Black,
                            fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Patient Name
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(29.dp),
                            color = Color.White,
                            shadowElevation = 8.dp
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_profile_icon4),
                                contentDescription = null,
                                modifier = Modifier.size(29.dp),
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = state?.user_name?:"",
                            fontSize = 16.sp,
                            color = Color(0xFF4338CA),
                            fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }


                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                            Image(
                                painter = painterResource(id = R.drawable.ic_share_icon),
                                contentDescription = null,
                                modifier = Modifier.size(45.dp).clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    val report = state
                                    if (report == null) {
                                        Toast.makeText(context, "Report details are not loaded yet", Toast.LENGTH_SHORT).show()
                                        return@clickable
                                    }

                                    viewModel.shareReportPdf(
                                        context = context,
                                        id = id?.toIntOrNull() ?: 0,
                                        error = { msg ->
                                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                        }
                                    )
                                },
                            )
                    Spacer(modifier = Modifier.height(20.dp))

                    Image(
                        painter = painterResource(id = R.drawable.ic_download_black_icon),
                        contentDescription = null,
                        modifier = Modifier.size(45.dp) .clickable {
                            viewModel.downloadReportPdf(
                                context = context,
                                id = id?.toInt() ?: 0,
                                error = { msg ->
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                }
                            )
                        },
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Summary Card
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFFF9F9FD),
                border = BorderStroke(1.dp, Color(0xFFE7E6F8))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = stringResource(R.string.summary),
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(R.font.urbanist_semibold)),
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF4338CA)
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text =  state?.summary?:"",
                        fontSize = 15.sp,
                        fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF181818),
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Detailed Analysis Card
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFFF9F9FD),
                border = BorderStroke(1.dp, Color(0xFFE7E6F8))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = stringResource(R.string.detailed_analysis)/*"Detailed Analysis"*/,
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(R.font.urbanist_semibold)),
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF4338CA)
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = state?.detailed_analysis?:"",
                        fontSize = 15.sp,
                        fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF181818),
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // AI Insights Card
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFFF9F9FD),
                border = BorderStroke(1.dp, Color(0xFFE7E6F8))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = stringResource(R.string.ai_insights)/*"AI Insights"*/,
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(R.font.urbanist_semibold)),
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF4338CA)
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    InsightRow(
                        label = stringResource(R.string.severity),
                        value = state?.ai_insights?.severity?:""
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    InsightRow(
                        label = stringResource(R.string.Reason),
                        value = state?.ai_insights?.reason?:""
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    InsightRow(
                        label = stringResource(R.string.Category),
                        value = state?.ai_insights?.category?:""
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    InsightRow(
                        label = stringResource(R.string.Recommended),
                        value = state?.ai_insights?.action_type?:""
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    InsightRow(
                        label = stringResource(R.string.Symptoms),
                        value = state?.ai_insights?.symptom_analysis
                            ?.joinToString(", ")
                            .orEmpty()
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Attachments Card
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFFF9F9FD),
                border = BorderStroke(1.dp, Color(0xFFE7E6F8))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = stringResource(R.string.attachments),
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(R.font.urbanist_semibold)),
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF4338CA)
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        state?.attachments?.forEach { fileName ->
                            DocumentItem2(
                                label = fileName,
                                onDownloadClick = {
                                    val absoluteUrl = if (fileName.startsWith("http://") || fileName.startsWith("https://")) {
                                        fileName
                                    } else {
                                        com.bussiness.curemegptapp.util.AppConstant.IMAGE_BASE_URL + fileName
                                    }
                                    com.bussiness.curemegptapp.util.DownloadUtils.downloadFile(
                                        context = context,
                                        url = absoluteUrl,
                                        fileName = fileName.substringAfterLast("/")
                                    )
                                }
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

/*@Composable
fun InsightRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 15.sp,
            color = Color(0xFF211C64),
            fontFamily = FontFamily(Font(R.font.urbanist_medium)),
            fontWeight = FontWeight.Medium,
        )
        Text(
            text = value,
            fontSize = 15.sp,
            color = Color(0xFF181818),
            fontFamily = FontFamily(Font(R.font.urbanist_regular)),
            fontWeight = FontWeight.Normal,
        )
    }
}*/

@Composable
fun InsightRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {

        Text(
            text = label,
            fontSize = 15.sp,
            color = Color(0xFF4338CA),
            fontFamily = FontFamily(Font(R.font.urbanist_medium)),
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Start
        )

        Text(
            text = value,
            fontSize = 15.sp,
            color = Color(0xFF181818),
            fontFamily = FontFamily(Font(R.font.urbanist_regular)),
            fontWeight = FontWeight.Normal,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Start
        )
    }
}



@SuppressLint("SuspiciousIndentation")
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ReportScreenPreview() {
    val navController = rememberNavController()
        ReportScreen(navController)
}