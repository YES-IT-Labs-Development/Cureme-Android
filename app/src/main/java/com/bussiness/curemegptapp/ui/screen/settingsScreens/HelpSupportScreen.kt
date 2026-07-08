package com.bussiness.curemegptapp.ui.screen.settingsScreens

//HelpSupportScreen


import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import com.bussiness.curemegptapp.ui.component.TopBarHeader2
import com.bussiness.curemegptapp.R
import com.bussiness.curemegptapp.navigation.AppDestination

@Composable
fun HelpSupportScreen(navController: NavHostController) {
    val context = LocalContext.current
    val titleFromNav = navController.previousBackStackEntry?.savedStateHandle?.get<String>("title") ?: ""
    val contentFromNav = navController.previousBackStackEntry?.savedStateHandle?.get<String>("content") ?: ""

    val screenTitle = titleFromNav.ifEmpty { stringResource(R.string.help_support) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White).statusBarsPadding()
    ) {
        // Top Bar
        TopBarHeader2(title = screenTitle, onBackClick = { navController.popBackStack() })


        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 28.dp),
            verticalArrangement = Arrangement.spacedBy(28.dp)
        ) {
            // Medical Help Section
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.need_medical_help)/*"Need Medical Help? We're Here for You!"*/,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                    color = Color(0xFF4338CA),
                    lineHeight = 25.sp
                )

                val displayContent = contentFromNav.ifEmpty {
                    "Whether you need help with your profile, AI chat, or technical issues, the CureMeGPT support team is ready to assist you anytime."
                }
                val keyword = "CureMeGPT"
                val keywordColor = Color(0xFF4338CA)
                val annotatedText = buildAnnotatedString {
                    var startIndex = 0
                    while (true) {
                        val index = displayContent.indexOf(keyword, startIndex, ignoreCase = false)
                        if (index == -1) {
                            append(displayContent.substring(startIndex))
                            break
                        }
                        append(displayContent.substring(startIndex, index))
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Medium, color = keywordColor)) {
                            append(displayContent.substring(index, index + keyword.length))
                        }
                        startIndex = index + keyword.length
                    }
                }

                Text(
                    text = annotatedText,
                    fontSize = 16.sp,
                    color = Color(0xFF181818),
                    fontWeight = FontWeight.Medium,
                    fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                    lineHeight = 30.sp
                )

                // Email Button
                Button(
                    onClick = {
                        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:support@curemegpt.com")
                        }
                        try {
                            context.startActivity(emailIntent)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(63.dp),
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 0.dp
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        Color(0xFFCED4DA)
                    )
                ) {
                    Text(
                        text = "Email Us: support@curemegpt.com",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                    )
                }
            }

            // Quick Answers Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFFF9F9FD),
                        shape = RoundedCornerShape(26.dp)
                    )
                    .padding(vertical = 20.dp, horizontal = 13.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.need_quick_answers)/*"Need Quick Answers?"*/,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                    color = Color.Black
                )

                Text(
                    text = buildAnnotatedString {
                        append("Visit our ")
                        withStyle(
                            style = SpanStyle(
                                color = Color(0xFF4338CA),
                                fontWeight = FontWeight.Medium,
                                textDecoration = TextDecoration.Underline
                            )
                        ) {
                            append("FAQ section")
                        }
                        append(" to explore answers about AI consultations, profile setup, medication reminders, and privacy settings.")
                    },
                    fontSize = 16.sp,
                    color = Color(0xFF000000),
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                    modifier = Modifier.clickable(  interactionSource = remember { MutableInteractionSource() },
                        indication = null){ navController.navigate(AppDestination.FrequentlyAskQuestionsScreen) }
                )
            }

            // Feedback Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFFF9FAFB),
                        shape = RoundedCornerShape(26.dp)
                    )
                    .padding(vertical = 20.dp, horizontal = 13.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.feedback_suggestions)/*"Feedback & Suggestions"*/,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                    color = Color.Black
                )

                Text(
                    text = buildAnnotatedString {
                        append("Help us improve ")
                        withStyle(style = SpanStyle(
                            color = Color(0xFF4338CA),
                            fontWeight = FontWeight.Medium
                        )) {
                            append("CureMeGPT")
                        }
                        append(" by sharing your feedback or new feature ideas. Your input helps us build a smarter, more caring health assistant.")
                    },
                    fontSize = 16.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Medium,
                    fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                    lineHeight = 22.sp
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Footer
            Text(
                text = stringResource(R.string.footer_text)/*"© 2026 CureMeGPT · Your privacy is our priority"*/,
                fontSize = 14.sp,
                color = Color.Black,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium,
                fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@SuppressLint("SuspiciousIndentation")
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HelpSupportScreenPreview() {
    val navController = rememberNavController()
        HelpSupportScreen(navController)

}