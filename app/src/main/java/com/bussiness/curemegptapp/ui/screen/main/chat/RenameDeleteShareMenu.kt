package com.bussiness.curemegptapp.ui.screen.main.chat

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

//RenameDeleteShareMenu

@Composable
fun RenameDeleteShareMenu(
    modifier: Modifier = Modifier,
    onEditClick: () -> Unit,
    onShareClick: () -> Unit,
    onDeleteClick: () -> Unit
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
            modifier = Modifier.size(26.dp)
        ) {
            Icon(
                painter = painterResource( R.drawable.ic_horizontal_menu_icon),
                contentDescription = stringResource(R.string.menu_icon_description),
                tint = Color.Unspecified,
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
                        text = "Rename"/*"Edit"*/,
                        fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                        color = Color(0xFF374151),
                        fontSize = 16.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_note_edit_icon),
                        contentDescription = "Reschedule",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(20.dp)
                    )
                },
                onClick = {
                    expanded = false
                    onEditClick()
                }
            )

            DropdownMenuItem(
                text = {
                    Text(
                        text = "Share Chat"/*"Edit"*/,
                        fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                        color = Color(0xFF374151),
                        fontSize = 16.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_share_icon_in_report_menu),
                        contentDescription = "Reschedule",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(24.dp)
                    )
                },
                onClick = {
                    expanded = false
                    onShareClick()
                }
            )

            DropdownMenuItem(
                text = {
                    Text(
                        text = stringResource(R.string.delete)/*"Delete"*/,
                        fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                        color = Color(0xFFFD3A3A),
                        fontSize = 16.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_delete_icon2),
                        contentDescription = "Delete",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(20.dp)
                    )
                },
                onClick = {
                    expanded = false
                    onDeleteClick()
                }
            )
        }
    }
}