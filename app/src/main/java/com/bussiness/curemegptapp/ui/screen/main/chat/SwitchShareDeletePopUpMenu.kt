package com.bussiness.curemegptapp.ui.screen.main.chat

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bussiness.curemegptapp.R
import com.bussiness.curemegptapp.ui.component.RoundedCustomCheckbox

//SwitchShareDeletePopUpMenu

@Composable
fun SwitchShareDeletePopUpMenu(
    modifier: Modifier = Modifier,
    switchText : String = "Switch to Case",
    showSwitch: Boolean = true,
    onSwitchClick: () -> Unit,
    onShareClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier.wrapContentSize(Alignment.TopEnd)
    ) {
      IconButton(
            onClick = {  expanded = true},
            modifier = Modifier.size(45.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_menu_icon3),
                contentDescription = "More options",
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(15.dp) //  outer curve applied
        ) {
            if (showSwitch) {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = switchText,
                            fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                            color = Color(0xFF374151),
                            fontSize = 16.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.switch_to_icon),
                            contentDescription = "Switch",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    onClick = {
                        expanded = false
                        onSwitchClick()
                    }
                )
            }
            DropdownMenuItem(
                text = {
                    Text(
                        text = "Share Chat",
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
                        modifier = Modifier.size(20.dp)
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
                    expanded = false
                    onDeleteClick()
                }
            )
        }
    }
}