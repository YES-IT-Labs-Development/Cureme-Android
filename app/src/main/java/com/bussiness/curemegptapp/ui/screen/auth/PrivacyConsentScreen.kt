package com.bussiness.curemegptapp.ui.screen.auth

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.ClickableText
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.bussiness.curemegptapp.R
import com.bussiness.curemegptapp.navigation.AppDestination
import com.bussiness.curemegptapp.ui.component.DisclaimerBox
import com.bussiness.curemegptapp.ui.component.GradientButton
import com.bussiness.curemegptapp.ui.component.GradientHeader
import com.bussiness.curemegptapp.ui.component.RoundedCustomCheckbox

@Composable
fun PrivacyConsentScreen(navController: NavHostController) {

    // Consent Checkboxes
    var checkbox1 by remember { mutableStateOf(false) }
    var checkbox2 by remember { mutableStateOf(false) }
    var checkbox3 by remember { mutableStateOf(false) }
    var checkbox4 by remember { mutableStateOf(false) }
    val allChecked = checkbox1 && checkbox2 && checkbox3 && checkbox4
    var showError by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val privacyAnnotatedString = remember {
        buildAnnotatedString {
            append(context.getString(R.string.consent_checkbox_1_part1))
            append(" ")
            pushStringAnnotation(tag = "POLICY", annotation = "privacy")
            withStyle(
                style = SpanStyle(
                    color = Color(0xFF4338CA),
                    fontWeight = FontWeight.Normal
                )
            ) {
                append(context.getString(R.string.privacy_policy_link))
            }
            pop()
        }
    }

    val termsAnnotatedString = remember {
        buildAnnotatedString {
            append(context.getString(R.string.consent_checkbox_2_part1))
            append(" ")
            pushStringAnnotation(tag = "TERMS", annotation = "terms")
            withStyle(
                style = SpanStyle(
                    color = Color(0xFF4338CA),
                    fontWeight = FontWeight.Normal
                )
            ) {
                append(context.getString(R.string.terms_of_service_link))
            }
            pop()
        }
    }

    BackHandler() {
        (context as Activity).moveTaskToBack(true)
    }

    Column(modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())) {

        // Top Gradient Header
        GradientHeader(
            heading = stringResource(R.string.privacy_consent_title),
            description = stringResource(R.string.privacy_consent_description))

        Spacer(modifier = Modifier.height(26.dp))

        // Important Disclaimers Section
        Column(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)) {
            // Important Disclaimers Header with Icon
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 16.dp)) {
                Image(
                        painter = painterResource(id = R.drawable.ic_warning),
                        contentDescription = "Warning",
                        modifier = Modifier.size(42.dp)
                    )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = stringResource(R.string.important_disclaimers_title),
                    fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = Color.Black
                )
            }
            // Medical Disclaimer Box
            DisclaimerBox(
                title = stringResource(R.string.medical_disclaimer_title),
                description = stringResource(R.string.medical_disclaimer_description),
                titleColor = Color(0xFF4338CA),
                backColor = Color(0x084338CA),
                isRed = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            // Data Privacy Box
            DisclaimerBox(
                title = stringResource(R.string.data_privacy_title),
                description = stringResource(R.string.data_privacy_description),
                backColor = Color(0x08F31D1D),
                isRed = true
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Column(modifier = Modifier
                .fillMaxWidth().padding(22.dp)
                .border(width = 1.dp, color = Color(0xFF697383),
                    shape = RoundedCornerShape(30.dp))) {
            Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 26.dp).padding(start = 20.dp, end = 15.dp)) {
                ConsentCheckbox(
                    checked = checkbox1,
                    onCheckedChange = { checkbox1 = it },
                    text = privacyAnnotatedString,
                    onClickText = { offset ->
                        val annotations = privacyAnnotatedString.getStringAnnotations(tag = "POLICY", start = offset, end = offset)
                        if (annotations.isNotEmpty()) {
                            navController.navigate(AppDestination.PrivacyPolicyScreen)
                        } else {
                            checkbox1 = !checkbox1
                        }
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
                ConsentCheckbox(
                    checked = checkbox2,
                    onCheckedChange = { checkbox2 = it },
                    text = termsAnnotatedString,
                    onClickText = { offset ->
                        val annotations = termsAnnotatedString.getStringAnnotations(tag = "TERMS", start = offset, end = offset)
                        if (annotations.isNotEmpty()) {
                            navController.navigate(AppDestination.TermsAndConditionsScreen)
                        } else {
                            checkbox2 = !checkbox2
                        }
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
                ConsentCheckbox(
                    checked = checkbox3,
                    onCheckedChange = { checkbox3 = it },
                    text = buildAnnotatedString {
                        append(stringResource(R.string.consent_checkbox_3))
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
                ConsentCheckbox(
                    checked = checkbox4,
                    onCheckedChange = { checkbox4 = it },
                    text = buildAnnotatedString {
                        append(stringResource(R.string.consent_checkbox_4))
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // I Agree - Continue Button
        val allTerm = stringResource(R.string.accept_all_terms_error)
        GradientButton(
            text =  stringResource(R.string.i_agree_continue_button),
            onClick = {   if (allChecked) {
                navController.navigate(AppDestination.ProfileCompletion)
            } else {
                Toast.makeText(
                    context,
                    allTerm,
                    Toast.LENGTH_SHORT
                ).show()
            } }
        )

        // Exit Instead
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 26.dp,bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_lock_encripted_icon),
                contentDescription = "Encripted",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = stringResource(R.string.encrypted_text),
                fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = Color(0xFFCED4DA)
            )
        }

    }
}



@Composable
fun ConsentCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    text: androidx.compose.ui.text.AnnotatedString,
    onClickText: ((Int) -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
 

        RoundedCustomCheckbox(
            checkboxSize = 21.dp,
            checked = checked,
            onCheckedChange = { onCheckedChange(it) }
        )


        Spacer(modifier = Modifier.width(12.dp))

        if (onClickText != null) {
            ClickableText(
                text = text,
                style = androidx.compose.ui.text.TextStyle(
                    fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    color = Color(0xFF697383),
                    lineHeight = 20.sp,
                ),
                onClick = onClickText
            )
        } else {
            Text(
                text = text,
                fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = Color(0xFF697383),
                lineHeight = 20.sp,
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    onCheckedChange(!checked)
                }
            )
        }
    }
}



@Preview(showBackground = true)
@Composable
fun PrivacyConsentScreenPreview() {
    val navController = rememberNavController()
    PrivacyConsentScreen(navController = navController)
}