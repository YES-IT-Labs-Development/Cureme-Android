package com.bussiness.curemegptapp.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.bussiness.curemegptapp.R
import coil.compose.AsyncImage

@Composable
fun HomeHeader(
    logoRes: Int,          // Logo image resource
    notificationRes: Int,  // Notification icon image resource
    profileRes: Int,       // Profile image resource
    profileImageUrl: String? = null,
    modifier: Modifier = Modifier,
    paddingHorizontal: Dp = 16.dp,
    paddingVertical: Dp = 10.dp,
    onClick: () -> Unit,
    onClickNotification: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(top = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Logo
        Image(
            painter = painterResource(id = logoRes),
            contentDescription = "Logo",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .wrapContentWidth()
                .height(30.dp) // Adjust size according to your logo
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            // Notification Icon
            Image(
                painter = painterResource(id = notificationRes),
                contentDescription = "Notification",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(45.dp)
                    .padding(end = 8.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        onClickNotification()
                    }
            )

            // Profile Image
            Row(
                modifier = modifier
                    .width(74.dp)
                    .height(52.dp)
                    .background(
                        color = Color(0xFFEBE1FF),
                        shape = RoundedCornerShape(50) // Capsule shape
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onClick() }
                    .padding(horizontal = 5.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                // ---- Profile Circular Image ----
                if (!profileImageUrl.isNullOrEmpty() && 
                    profileImageUrl != "https://curemegpt.tgastaging.com/" && 
                    profileImageUrl != "https://curemegpt.tgastaging.com") {
                    AsyncImage(
                        model = profileImageUrl,
                        contentDescription = "Profile",
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(id = profileRes),
                        contentDescription = "Profile",
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // ---- Arrow Icon ----
                Image(
                    painter = painterResource(id = R.drawable.ic_next_arrow_calender),
                    contentDescription = "Arrow",
                    modifier = Modifier.size(16.dp),
                    contentScale = ContentScale.Fit
                )
            }

        }
    }
}

@Composable
fun GradientRedButton(
    text: String,
    icon: Int? = null,
    width: Dp = 109.dp,
    height: Dp = 39.dp,
    gradientColors: List<Color> = listOf(
        Color(0xFFF31D1D),
        Color(0xFF8D1111)
    ),
    modifier: Modifier = Modifier,
    imageSize: Dp = 10.dp,
    horizontalPadding: Dp = 16.dp,
    verticalPadding: Dp = 8.dp,
    fontSize: TextUnit = 12.sp,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(31.dp),
        contentPadding = PaddingValues(horizontal = horizontalPadding, vertical = verticalPadding),
        modifier = modifier
            .width(width)
            .height(height)
            .background(
                brush = Brush.horizontalGradient(gradientColors),
                shape = RoundedCornerShape(31.dp)
            )
    ) {

        // Icon (optional)
        icon?.let {
            Image(
                painter = painterResource(id = it),
                contentDescription = null,
                modifier = Modifier.size(imageSize)
            )
            Spacer(modifier = Modifier.width(7.dp))
        }

        // Text
        Text(
            text = text,
            fontSize = fontSize,
            fontFamily = FontFamily(Font(R.font.urbanist_medium)),
            fontWeight = FontWeight.Medium,
            color = Color.White
        )
    }
}

//@Composable
//fun GradientViewSummaryButton(
//    text: String,
//    icon: Int? = null,
//    width: Dp = 109.dp,
//    height: Dp = 39.dp,
//    gradientColors: List<Color> = listOf(
//        Color(0xFFF31D1D),
//        Color(0xFF8D1111)
//    ),
//    modifier :Modifier  = Modifier,
//    imageSize : Dp = 10.dp,
//    horizontalPadding : Dp =16.dp,
//    verticalPadding : Dp =8.dp,
//    fontSize : TextUnit  = 12.sp,
//    onClick: () -> Unit
//) {
//    Button(
//        onClick = onClick,
//        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
//        shape = RoundedCornerShape(31.dp),
//        contentPadding = PaddingValues(horizontal = horizontalPadding, vertical =2.dp),
//        modifier = modifier
//            .width(width)
//            .height(30.dp)
//            .background(
//                brush = Brush.horizontalGradient(gradientColors),
//                shape = RoundedCornerShape(31.dp)
//            )
//    ) {
//
//        // Icon (optional)
//        icon?.let {
//            Image(
//                painter = painterResource(id = it),
//                contentDescription = null,
//                modifier = Modifier.size(imageSize)
//            )
//            Spacer(modifier = Modifier.width(7.dp))
//        }
//
//        // Text
//        Text(
//            text = text,
//            fontSize = fontSize,
//            fontFamily = FontFamily(Font(R.font.urbanist_medium)),
//            fontWeight = FontWeight.Medium,
//            color = Color.White
//        )
//    }
//}

@Composable
fun GradientViewSummaryButton(
    text: String,
    icon: Int? = null,
    width: Dp = 109.dp,
    height: Dp = 39.dp,
    gradientColors: List<Color> = listOf(
        Color(0xFFF31D1D),
        Color(0xFF8D1111)
    ),
    modifier: Modifier = Modifier,
    imageSize: Dp = 10.dp,
    horizontalPadding: Dp = 14.dp,
    verticalPadding: Dp = 8.dp,
    fontSize: TextUnit = 12.sp,
    onClick: () -> Unit
) {

    Box(
        modifier = modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(31.dp))
            .background(brush = Brush.horizontalGradient(gradientColors))
            .clickable(interactionSource = remember { MutableInteractionSource() },
                indication = null) { onClick() },
        contentAlignment = Alignment.Center
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(
                horizontal = horizontalPadding,
                vertical = verticalPadding
            )
        ) {

            // Icon
            icon?.let {
                Image(
                    painter = painterResource(id = it),
                    contentDescription = null,
                    modifier = Modifier.size(imageSize)
                )
                Spacer(modifier = Modifier.width(7.dp))
            }
            Column {
                // Text
                Text(
                    text = text,
                    fontSize = fontSize,
                    fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    modifier= Modifier.padding(bottom = 5.dp)
                )

            }
        }
    }
}


@Composable
fun AppointmentBox(
    text: String,
    iconRes: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .border(1.dp, Color(0xFF697383), RoundedCornerShape(30.dp))
            .clip(RoundedCornerShape(20.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.padding(top = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                fontSize = 11.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = Color(0xFF697383),
                fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                fontWeight = FontWeight.Medium
            )
        }
    }
}


@Composable
fun AttentionItem(
    title: String,
    subtitle: String,
    isUrgent: Boolean,
    onScheduleClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .border(color = Color(0xFFFDCBCB), width = 1.dp, shape = RoundedCornerShape(30.dp))
            .background(color = Color(0xFFFFF6F6), shape = RoundedCornerShape(30.dp)),

        ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFD32F2F)
                )
                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF4338CA)
                )
            }

            GradientRedButton(
                verticalPadding = 4.dp,
                imageSize = 15.dp,
                text = "Schedule",
                icon = R.drawable.ic_schedule_attention_icon,
                onClick = onScheduleClick
            )

        }
    }
}


@Composable
fun MoodOptionSelectable(
    icon: Int,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) Color.White else Color.Transparent)
            .padding(horizontal = 8.dp)
            .padding(top = 6.dp, bottom = 4.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(id = icon),
            contentDescription = label,
            modifier = Modifier.size(40.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            color = if (isSelected) Color(0xFF4338CA) else Color.White,
            fontSize = 12.sp,
            fontFamily = FontFamily(Font(if (isSelected) R.font.urbanist_semibold else R.font.urbanist_medium)),
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
        )
    }
}




