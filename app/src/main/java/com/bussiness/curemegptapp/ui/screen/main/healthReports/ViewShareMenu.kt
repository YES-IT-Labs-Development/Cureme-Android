package com.bussiness.curemegptapp.ui.screen.main.healthReports



import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bussiness.curemegptapp.R


@Composable
fun ViewShareMenu(
    modifier: Modifier = Modifier,
    onViewClick: () -> Unit,
    onShareClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 90f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "menu_rotation"
    )
    Box(
        modifier = modifier.wrapContentSize(Alignment.TopEnd)
    ) {


        IconButton(
            onClick = {  expanded = true},
            modifier = Modifier.size(45.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_menu_icon),
                contentDescription = "More options",
                modifier = Modifier.rotate(rotation)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(15.dp) //  outer curve applied
        ) {

            DropdownMenuItem(
                text = {
                    Text(
                        text = stringResource(R.string.view)/*"View"*/,
                        fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                        color = Color(0xFF374151),
                        fontSize = 16.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_view_eye_icon),
                        contentDescription = "View",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(20.dp)
                    )
                },
                onClick = {
                    expanded = false
                    onViewClick()
                }
            )
            DropdownMenuItem(
                text = {
                    Text(
                        text = stringResource(R.string.share)/*"Share"*/,
                        fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                        color = Color(0xFF374151),
                        fontSize = 16.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_share_icon_in_report_menu),
                        contentDescription = "Share",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(20.dp)
                    )
                },
                onClick = {
                    expanded = false
                    onShareClick()
                }
            )
        }
    }
}