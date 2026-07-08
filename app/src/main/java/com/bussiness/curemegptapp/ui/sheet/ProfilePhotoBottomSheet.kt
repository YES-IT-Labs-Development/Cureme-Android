package com.bussiness.curemegptapp.ui.sheet

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bussiness.curemegptapp.R

@Composable
fun ProfilePhotoBottomSheet(
    onDismiss: () -> Unit,
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(RoundedCornerShape(topEnd = 30.dp, topStart = 30.dp))
            .background(Color.White)
            .padding(20.dp)
    ) {

        // 👉 Top Indicator
        Box(
            modifier = Modifier
                .width(82.dp)
                .height(4.dp)
                .background(Color(0xFFD0D0D0), RoundedCornerShape(2.dp))
                .align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 👉 Header Row (Close, Title, Delete)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Close Button
            IconButton(
                onClick = onDismiss,
                modifier = Modifier.size(26.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_cross_icon3), // Use your close icon
                    contentDescription = "Close",
                    tint = Color.Unspecified,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Title
            Text(
                text = "Profile Photo",
                fontSize = 18.sp,
                fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                fontWeight = FontWeight.Medium,
                color = Color(0xFF374151)
            )

            // Delete Button
            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier.size(26.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_delete_icon2), // Use your delete icon
                    contentDescription = "Delete",
                    tint = Color.Unspecified,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Spacer(modifier = Modifier.height(15.dp))
        Divider(color = Color(0xFFEBE1FF), thickness = 1.dp)
        Spacer(modifier = Modifier.height(19.dp))
        // 👉 Camera Option
        OptionRow(
            iconRes = R.drawable.ic_camera, // Use your camera icon
            text = "Camera",
            onClick = onCameraClick
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 👉 Gallery Option
        OptionRow(
            iconRes = R.drawable.ic_gallery, // Use your gallery icon
            text = "Gallery",
            onClick = onGalleryClick
        )

        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Composable
private fun OptionRow(
    iconRes: Int,
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onClick()
            }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon Circle
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = text,
                modifier = Modifier.size(54.dp)
            )


        Spacer(modifier = Modifier.width(10.dp))

        // Text
        Text(
            text = text,
            fontSize = 14.sp,
            fontFamily = FontFamily(Font(R.font.urbanist_regular)),
            fontWeight = FontWeight.Normal,
            color = Color.Black
        )
    }
}

@Preview(
    name = "Profile Photo Bottom Sheet",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
fun ProfilePhotoBottomSheetPreview() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
            .background(Color(0xFFF5F5F5)),
        contentAlignment = Alignment.BottomCenter
    ) {
        ProfilePhotoBottomSheet(
            onDismiss = { println("Dismiss clicked") },
            onCameraClick = { println("Camera clicked") },
            onGalleryClick = { println("Gallery clicked") },
            onDeleteClick = { println("Delete clicked") }
        )
    }
}