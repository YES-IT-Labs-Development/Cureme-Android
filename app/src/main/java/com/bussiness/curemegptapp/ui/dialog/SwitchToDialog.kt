package com.bussiness.curemegptapp.ui.dialog


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.bussiness.curemegptapp.R
import com.bussiness.curemegptapp.ui.component.CancelButton
import com.bussiness.curemegptapp.ui.component.GradientButton1
import com.bussiness.curemegptapp.ui.component.GradientShadowButton
import com.bussiness.curemegptapp.ui.component.LayerShadowBlueButton
import com.bussiness.curemegptapp.ui.component.LayerShadowButton


@Composable
fun SwitchToDialog(onDismiss: () -> Unit, onConfirm: () -> Unit,title : String,description : String, buttonText : String) {
    Dialog(onDismissRequest = onDismiss,   properties = DialogProperties(
        dismissOnClickOutside = false, // 🔴 IMPORTANT
        dismissOnBackPress = false       // back press se band chahiye to true
    )) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Icon(
                            painter = painterResource( R.drawable.ic_new_case_icon),
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier
                                .size(40.dp)
                        )

                        Column {
                            Text(
                                title,
                                fontSize = 16.sp,
                                color = Color(0xFF050505),
                                fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            painter = painterResource(R.drawable.ic_close),
                            contentDescription = "Close",
                            tint = Color.Unspecified,
                            modifier = Modifier
                                .size(40.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    description,
                    fontSize = 14.sp,
                    color = Color(0xFF050505),
                    fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                    fontWeight = FontWeight.Normal,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(19.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    CancelButton(
                        cancelText = buttonText,
                        fontSize = 13.sp,
                        paddingHorizontal = 2.dp,
                        modifier = Modifier
                            .weight(1.15f)
                            .height(52.dp),
                        onClick = { onDismiss() }
                    )


//                    GradientShadowButton(
//                        text = "Yes, Switch",
//                        fontSize = 13.sp,
//                        onClick = { onConfirm() },
//                        modifier = Modifier
//                            .weight(0.85f)
//                            .height(52.dp)
//                    )

                    LayerShadowBlueButton(
                        modifier = Modifier
                            .weight(0.85f)
                            .height(52.dp),
                        onClick = { onConfirm()  }
                    ) {
                        Text(
                            text = "Yes, Switch",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontFamily = FontFamily(Font(R.font.urbanist_semibold))
                        )
                    }
                }
            }
        }
    }
}
