package com.bussiness.curemegptapp.ui.dialog

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.bussiness.curemegptapp.R
import com.bussiness.curemegptapp.ui.component.GradientButton


import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.Icon
import androidx.compose.foundation.layout.width

@Composable
fun OTPVerificationDialog(
    onDismiss: () -> Unit,
    onVerify: (otp: String) -> Unit,
    title: String = "Verify OTP",
    message: String = "Enter the OTP sent to your email/phone",
    otpLength: Int = 4,
    onResend: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val otpValues = remember { mutableStateListOf("", "", "", "") }
    val focusRequesters = List(otpLength) { FocusRequester() }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White)
        ) {
            // Close / Cross icon to dismiss pop-up
//            Icon(
//                painter = painterResource(id = R.drawable.ic_close),
//                contentDescription = "Close",
//                tint = Color.Gray,
//                modifier = Modifier
//                    .align(Alignment.TopEnd)
//                    .padding(16.dp)
//                    .clickable(
//                        interactionSource = remember { MutableInteractionSource() },
//                        indication = null
//                    ) { onDismiss() }
//                    .size(20.dp)
//            )


            Image(
                painter = painterResource(id = R.drawable.ic_close),
                contentDescription = "Close",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        onDismiss()
                    }
                    .size(24.dp)
            )


            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = title,
                    fontSize = 17.sp,
                    fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = message,
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {

                    for (i in 0 until otpLength) {

                        OTPInputField(
                            value = otpValues[i],
                            onValueChange = { value ->

                                if (value.length <= 1 && value.all { it.isDigit() }) {

                                    otpValues[i] = value

                                    // Move to next field
                                    if (value.isNotEmpty() && i < otpLength - 1) {
                                        focusRequesters[i + 1].requestFocus()
                                    }

                                    // Move to previous on delete
                                    if (value.isEmpty() && i > 0) {
                                        focusRequesters[i - 1].requestFocus()
                                    }
                                }
                            },
                            focusRequester = focusRequesters[i]
                        )
                    }
                }

                if (onResend != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Didn't receive the OTP? ",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            fontFamily = FontFamily(Font(R.font.urbanist_regular))
                        )
                        Text(
                            text = "Resend",
                            fontSize = 14.sp,
                            color = Color(0xFF4338CA),
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily(Font(R.font.urbanist_bold)),
                            modifier = Modifier.clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onResend() }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                GradientButton(
                    text = "Verify OTP",
                    onClick = {

                        val otp = otpValues.joinToString("")

                        if (otp.length == otpLength) {
                            onVerify(otp)
                        } else {
                            Toast.makeText(context, "Please Enter OTP", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                )
            }
        }
    }
}

@Composable
fun OTPInputField(
    value: String,
    onValueChange: (String) -> Unit,
    focusRequester: FocusRequester
) {

    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF5F5F5))
            .border(2.dp, Color(0xFFE5E5E5), RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            textStyle = TextStyle(
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily(Font(R.font.urbanist_semibold))
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OTPVerificationDialogPreview() {
    OTPVerificationDialog(
        onDismiss = {},
        onVerify = { otp ->
            println("OTP Entered: $otp")
        }
    )
}

