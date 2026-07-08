package com.bussiness.curemegptapp.ui.dialog

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bussiness.curemegptapp.R
import com.bussiness.curemegptapp.ui.component.CancelButton
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

import com.bussiness.curemegptapp.ui.component.LayerShadowButton

@Composable
fun DeleteChatDialog(
    title: String,
    message: String,
    warningText: String? = null,
    bottomText: String,
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
                            painter = painterResource(id = R.drawable.ic_account_setting_icon),
                            contentDescription = "Success",
                            modifier = Modifier.size(38.dp)
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        Text(
                            text = title,
                            fontSize = 16.sp,
                            fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                            color = Color.Black,
                            lineHeight = 26.sp,
                            modifier = Modifier
                                .width(190.dp)
                                .wrapContentHeight()
                        )

                    }
                    Image(
                        painter = painterResource(id = R.drawable.ic_close),
                        contentDescription = "Close",
                        modifier = Modifier
                            .size(41.dp)
                            .clickable( interactionSource = remember { MutableInteractionSource() },
                                indication = null) { onDismiss() }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 🔹 Message
//                val formattedMessage = buildAnnotatedString {
//                    append(message)
//                    highlightText?.let {
//                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
//                            append(" $it")
//                        }
//                    }
//                }

                Text(
                    text = message,
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                    color = Color.Black,
                    lineHeight = 20.sp,
                )

                // 🔹 Optional warning text (red)
                warningText?.let {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.Top) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_warning_red),
                            contentDescription = null,
                            modifier = Modifier.padding(top =5.dp).size(14.dp),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = it,
                            fontSize = 12.sp,
                            fontFamily = FontFamily(Font(R.font.urbanist_italic)),
                            color = Color.Red,
                                    lineHeight = 19.sp,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 🔹 Buttons Row
                Row(Modifier.fillMaxWidth()) {

                    CancelButton(
                        cancelText = cancelText,
                        fontSize = 13.sp,
                        paddingHorizontal = 2.dp,
                        modifier = Modifier
                            .weight(0.9f)
                            .height(51.dp),
                        onClick = { onDismiss() }
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    LayerShadowButton(
                        modifier = Modifier
                            .weight(1.1f)
                            .height(51.dp),
                        onClick = { onConfirm()  }
                    ) {
                        Text(
                            text = confirmText,
                            color = Color.White,
                            fontSize = 13.sp,
                            fontFamily = FontFamily(Font(R.font.urbanist_semibold))
                        )
                    }

                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = bottomText,
                    fontSize = 11.sp,
                    fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                    color = Color.Black,
                    lineHeight = 18.sp,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DeleteChatDialogPreview() {
    DeleteChatDialog(
        title = "Delete Chat?",
        message = "Are you sure you want to delete this chat?",
        warningText = "Chat will be permanently removed",
        bottomText = "You’re always in control — deleted chats cannot be restored.",
        cancelText = "Cancel",
        confirmText = "Delete",
        onDismiss = {},
        onConfirm = {}
    )
}

