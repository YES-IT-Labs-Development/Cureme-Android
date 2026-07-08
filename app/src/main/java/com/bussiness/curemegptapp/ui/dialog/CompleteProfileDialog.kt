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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.bussiness.curemegptapp.R
import com.bussiness.curemegptapp.ui.component.CancelButton
import com.bussiness.curemegptapp.ui.component.GradientButton1


@Composable
fun CompleteProfileDialog(
    icon: Int = R.drawable.ic_person_complete_icon,   // your blue circular icon
    title: String = "Complete Your Profile",
    checklist: List<String> = listOf(
        "Faster AI answers tailored to you",
        "Safer medication & allergy checks",
        "Quicker reminders & records Complete Now"
    ),
    cancelText: String = "Remind Me Later",
    confirmText: String = "Complete Now",
    skipText: String = "Skip for Now",
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    onSkip: () -> Unit
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
                .padding(5.dp)
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
                    Row(verticalAlignment = Alignment.CenterVertically) {

                        Image(
                            painter = painterResource(id = icon),
                            contentDescription = "",
                            modifier = Modifier.size(42.dp)
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = title,
                            fontSize = 17.sp,
                            color = Color.Black,
                            fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                            fontWeight = FontWeight.Medium,
                            maxLines = 2
                        )
                    }

                    Image(
                        painter = painterResource(id = R.drawable.ic_close),
                        contentDescription = "",
                        modifier = Modifier
                            .size(42.dp)
                            .clickable( interactionSource = remember { MutableInteractionSource() },
                                indication = null) { onDismiss() }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 🔹 Description text
                Text(
                    text = "Finish setting up your details for better, personalized care",
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                    color = Color.Black,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(18.dp))

                // 🔹 CHECKLIST ITEMS (LazyColumn)
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 160.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(checklist) { item ->
                        CheckItem(text = item)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 🔹 Buttons Row
                Row(Modifier.fillMaxWidth()) {

                    CancelButton(
                        cancelText = cancelText,
                        fontSize = 14.sp,
                        paddingHorizontal = 2.dp,
                        fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        onClick = { onDismiss() }
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    GradientButton1(
                        text = confirmText,
                        fontSize = 14.sp,
                        onClick = { onConfirm() },
                        fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 🔹 Skip Text Button
                Text(
                    text = skipText,
                    fontSize = 14.sp,
                    color = Color(0xFF697383),
                    fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .clickable( interactionSource = remember { MutableInteractionSource() },
                            indication = null) { onSkip() }
                        .padding(6.dp)
                )
            }
        }
    }
}

@Composable
fun CheckItem(text: String) {
    Row(verticalAlignment = Alignment.Top) {

        Image(
            painter = painterResource(id = R.drawable.ic_green_check), // green box check icon
            contentDescription = "",
            modifier = Modifier.size(22.dp)
        )

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = text,
            fontSize = 14.sp,
            color = Color(0xFF050505),
            fontFamily = FontFamily(Font(R.font.urbanist_regular))
        )
    }
}

