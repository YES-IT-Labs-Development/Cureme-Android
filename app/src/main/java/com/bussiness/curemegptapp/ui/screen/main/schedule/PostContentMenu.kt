package com.bussiness.curemegptapp.ui.screen.main.schedule

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bussiness.curemegptapp.R
import com.bussiness.curemegptapp.ui.component.RoundedCustomCheckbox

@Composable
fun PostContentMenu(
    modifier: Modifier = Modifier,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var lastClickTime by remember { mutableStateOf(0L) }
    var lastClickTimeDelete by remember { mutableStateOf(0L) }
    var expanded by remember { mutableStateOf(false) }
    // 🔥 Rotation animation
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 90f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "menu_rotation"
    )
    Box(
        modifier = modifier.wrapContentSize(Alignment.TopEnd)
    ) {


        IconButton(
            onClick = {  expanded = !expanded},
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
                        text = "Mark As Complete",
                        fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                        color = Color(0xFF374151),
                        fontSize = 16.sp
                    )
                },
                leadingIcon = {
                    RoundedCustomCheckbox(
                        checked = checked,
                        onCheckedChange = { onCheckedChange(it) }
                    )
                },
                onClick = {
                    expanded = false
                  //  onEditClick()
                }
            )
            DropdownMenuItem(
                enabled = !checked,
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    expanded = false
                    onEditClick()
                },
                text = {
                    Text(
                        text = "Reschedule",
                        fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                        color = if (checked) Color(0x80374151) else Color(0xFF374151), // faded
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
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - lastClickTime > 1000) { // 1 second gap
                        lastClickTime = currentTime
                        expanded = false
                        onEditClick()
                    }
                }
            )


            DropdownMenuItem(
                text = {
                    Text(
                        text = "Delete",
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
                    val currentTime = System.currentTimeMillis()

                    if (currentTime - lastClickTimeDelete > 1000) { // 1 second gap
                        lastClickTimeDelete = currentTime
                        expanded = false
                        onDeleteClick()
                    }

                }
            )
        }
    }
}