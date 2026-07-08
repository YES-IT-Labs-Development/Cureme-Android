package com.bussiness.curemegptapp.ui.dialog

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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.compose.rememberNavController
import com.bussiness.curemegptapp.R
import com.bussiness.curemegptapp.ui.component.GradientButton
import com.bussiness.curemegptapp.ui.component.GradientButton1

@Composable
fun AccountCreatedDialog(
    title: String = "Account Created\nSuccessfully!",
    description: String = "Your account is ready. Start exploring now!",
    icon : Int = R.drawable.ic_check,
    onDismiss: () -> Unit,
    onSetupProfile: () -> Unit,
    onGoToAskAI: () -> Unit
) {

    Dialog(onDismissRequest = { onDismiss() },
            properties = DialogProperties(
            dismissOnClickOutside = false,
        dismissOnBackPress = false
    )) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(Color.White)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalAlignment = Alignment.Start
            ) {

                // ---------- TOP ROW (Check Icon + Title + Close Icon) ----------
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Row(verticalAlignment = Alignment.CenterVertically) {

                        Image(
                            painter = painterResource(id = R.drawable.ic_check),
                            contentDescription = "Success",
                            modifier = Modifier.size(42.dp)
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = title,
                            fontSize = 17.sp,
                            fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                            color = Color.Black,
                            lineHeight = 20.sp
                        )
                    }

                    Image(
                        painter = painterResource(id = R.drawable.ic_close),
                        contentDescription = "Close",
                        modifier = Modifier
                            .size(43.dp)
                            .clickable( interactionSource = remember { MutableInteractionSource() },
                                indication = null) { onDismiss() }
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))


                Text(
                    text = description,
                    fontSize = 15.sp,
                    fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    // WHITE BUTTON
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clip(RoundedCornerShape(40.dp))
                            .background(Color.White)
                            .border(
                                width = 1.dp,
                                color = Color(0xFFBEBEBE),
                                shape = RoundedCornerShape(40.dp)
                            )
                            .clickable( interactionSource = remember { MutableInteractionSource() },
                                indication = null) { onSetupProfile() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Set Up Profile",
                            fontSize = 15.sp,
                            fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                            color = Color.Black
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    GradientButton1(
                        text = "Go to Ask AI",
                        fontSize = 15.sp,
                        modifier = Modifier.weight(1f),
                        onClick = { onGoToAskAI() }
                    )
                }

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AccountCreatedDialogPreview() {
    AccountCreatedDialog(title = "Account Created\nSuccessfully!", description = "Your account is ready. Start exploring now!", onDismiss ={},onSetupProfile={},onGoToAskAI={} )
}