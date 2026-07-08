package com.bussiness.curemegptapp.ui.dialog

//MemberAddedSuccessfullyDialog


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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bussiness.curemegptapp.R
import com.bussiness.curemegptapp.ui.component.CancelButton
import com.bussiness.curemegptapp.ui.component.GradientButton2


import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.bussiness.curemegptapp.ui.component.GradientButton1
import com.bussiness.curemegptapp.ui.component.GradientRedButton
import com.bussiness.curemegptapp.ui.component.GradientRedButton1

@Composable
fun MemberAddedSuccessfullyDialog(
    title: String,
    message: String,
    cancelText: String = "Cancel",
    confirmText: String = "OK",
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(40.dp))
                .background(Color.White)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {

                // 🔹 Top Row (Icon + Close Button)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {


                        Image(
                            painter = painterResource(id = R.drawable.ic_check),
                            contentDescription = "Success",
                            modifier = Modifier.size(38.dp)
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        Text(
                            text = title,
                            fontSize = 17.sp,
                            fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                            color = Color.Black,
                            lineHeight = 23.sp,
                            modifier = Modifier
                                .width(190.dp)
                                .wrapContentHeight()
                        )

                    }
                    Image(
                        painter = painterResource(id = R.drawable.ic_close),
                        contentDescription = "Close",
                        modifier = Modifier
                            .size(40.dp)
                            .clickable( interactionSource = remember { MutableInteractionSource() },
                                indication = null){ onDismiss() }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))



                Text(
                    text = message,
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                    color = Color.Black,
                    lineHeight = 17.sp,
                )





                Spacer(modifier = Modifier.height(20.dp))

                // 🔹 Buttons Row
                Row(Modifier.fillMaxWidth()) {

                    Box(
                        modifier = Modifier
                            .weight(1.25f)
                            .height(52.dp)
                            .clip(RoundedCornerShape(28.dp))
                            .border(
                                1.dp,
                                Color(0xFF181B1A),
                                RoundedCornerShape(28.dp)
                            )
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = cancelText,
                            fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                            color = Color.Black,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    GradientButton1(
                        text = confirmText,
                        fontSize = 14.sp,
                        onClick = { onConfirm() },
                        modifier = Modifier
                            .weight(0.75f)
                            .height(52.dp)
                    )
                }
            }
        }
    }
}
