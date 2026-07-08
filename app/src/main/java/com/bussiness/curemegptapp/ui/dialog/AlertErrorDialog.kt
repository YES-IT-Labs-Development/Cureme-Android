package com.bussiness.curemegptapp.ui.dialog

//LogOutDialog


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.bussiness.curemegptapp.R
import com.bussiness.curemegptapp.ui.component.GradientButton
import com.bussiness.curemegptapp.ui.component.GradientRedButton
import com.bussiness.curemegptapp.ui.component.GradientRedButton1

@Composable
fun AlertErrorDialog(
    message: String,
    confirmText: String = "Okay",
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = true
        )
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(40.dp))
                .background(Color.White)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // 🔹 Top Row (Icon + Close Button)
                // 🔹 Top Row (Close Button aligned to end)
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = message,
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                    color = Color.Black,
                    lineHeight = 17.sp,
                    modifier = Modifier.fillMaxWidth(), // makes Text take full width
                    textAlign = TextAlign.Center       // centers text horizontally
                )
                Spacer(modifier = Modifier.height(20.dp))
                // 🔹 Buttons Row
                GradientButton(
                    text = confirmText,
                    onClick = { onConfirm() },
                    modifier = Modifier.fillMaxWidth()
                        .height(52.dp)
                )
            }
        }
    }
}

// ✅ Preview with dummy callbacks
@Preview(showBackground = true)
@Composable
fun AlertErrorDialogPreview() {
    AlertErrorDialog(
        message = "",
        onDismiss = {},
        onConfirm = {}
    )
}