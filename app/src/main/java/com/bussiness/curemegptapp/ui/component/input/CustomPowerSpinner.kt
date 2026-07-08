package com.bussiness.curemegptapp.ui.component.input

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bussiness.curemegptapp.R

//@Composable
//fun CustomPowerSpinner(
//    modifier: Modifier = Modifier,
//    modifierDropDown: Modifier = Modifier,
//    selectedText: String = "Select",
//    onSelectionChanged: (String) -> Unit = {},
//    horizontalPadding: Dp = 24.dp,
//    reasons: List<String> = listOf()
//) {
//    var expanded by remember { mutableStateOf(false) }
//    var selectedReason by remember { mutableStateOf(selectedText) }
//
//
//    LaunchedEffect(selectedText) {
//        selectedReason = selectedText
//    }
//
//    BoxWithConstraints(modifier = modifier.background(Color.White)) {
//        val dropdownWidth = maxWidth - 0.dp
//        // Main button
//        Card(
//            modifier = Modifier
//                .fillMaxWidth()
//                .clickable(  interactionSource = remember { MutableInteractionSource() },
//                    indication = null){ expanded = !expanded },
//            colors = CardDefaults.cardColors(containerColor = Color.White),
//            border = BorderStroke(1.dp, Color(0xFF697383)),
//            shape = RoundedCornerShape(50.dp),
//            elevation = CardDefaults.cardElevation(0.dp)
//        ) {
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(16.dp),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text(
//                    text = selectedReason,
////                    color = if (selectedReason == "Select") Color(0xFF697383) else  Color.Black,
//                    color = if (selectedReason.trim().startsWith("Select", ignoreCase = true))
//                        Color(0xFF697383)
//                    else
//                        Color.Black,
//                    fontSize = 13.sp,
//                    fontFamily = FontFamily(Font(R.font.urbanist_regular)),
//                    )
//
//                Image(
//                    painter = painterResource(if (expanded) R.drawable.ic_dropdown_show else R.drawable.ic_dropdown_icon),
//                    contentDescription = "",
//                )
//            }
//        }
//
//
//        DropdownMenu(
//            expanded = expanded,
//            onDismissRequest = { expanded = false },
//            modifier = modifierDropDown
//                .width(dropdownWidth)
//                .heightIn(max = 300.dp)
//                .padding(horizontal = 15.dp, vertical = 5.dp)
//                .background(Color.White, RoundedCornerShape(20.dp)),
//            containerColor = Color.White,
//            shape = RoundedCornerShape(12.dp),
//        ) {
//            reasons.forEachIndexed { index, reason ->
//                val isSelected = reason == selectedReason
//
//                Box(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(horizontal = 10.dp, vertical = 6.dp)
//                        .clip(RoundedCornerShape(50.dp))
//                        .background(
//                            brush = if (isSelected)
//                                Brush.horizontalGradient(
//                                    colors = listOf(
//                                        Color(0xFF4338CA),
//                                        Color(0xFF211C64)
//                                    )
//                                )
//                            else Brush.linearGradient(listOf(Color.Unspecified, Color.Unspecified)),
//                            shape = RoundedCornerShape(50.dp)
//                        )
//
//                        .clickable( interactionSource = remember { MutableInteractionSource() },
//                        indication = null){
//                            selectedReason = reason
//                            expanded = false
//                            onSelectionChanged(reason)
//                        }
//                        .padding(horizontal = 14.dp, vertical = 6.dp)
//                ) {
//
//                    Text(
//                        text = reason,
//                        color = if (isSelected) Color.White else Color.Black,
//                        fontSize = 16.sp
//                    )
//                }
//            }
//        }
//
//    }
//}

@Composable
fun CustomPowerSpinner(
    modifier: Modifier = Modifier,
    modifierDropDown: Modifier = Modifier,
    selectedText: String = "Select",
    onSelectionChanged: (String) -> Unit = {},
    horizontalPadding: Dp = 24.dp,
    reasons: List<String> = listOf(),
    enabled: Boolean = true   // ✅ NEW PARAMETER
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedReason by remember { mutableStateOf(selectedText) }

    LaunchedEffect(selectedText) {
        selectedReason = selectedText
    }

    BoxWithConstraints(
        modifier = modifier
            .background(Color.White)
            .alpha(if (enabled) 1f else 0.6f)   // visually disabled
    ) {
        val dropdownWidth = maxWidth

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    enabled = enabled,   // ✅ disable click
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    expanded = !expanded
                },
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFF697383)),
            shape = RoundedCornerShape(50.dp),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = selectedReason,
                    color = if (selectedReason.trim().startsWith("Select", ignoreCase = true))
                        Color(0xFF697383)
                    else
                        Color.Black,
                    fontSize = 13.sp,
                    fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                )

                Image(
                    painter = painterResource(
                        if (expanded) R.drawable.ic_dropdown_show
                        else R.drawable.ic_dropdown_icon
                    ),
                    contentDescription = "",
                )
            }
        }

        DropdownMenu(
            expanded = expanded && enabled,   // ✅ prevent opening if disabled
            onDismissRequest = { expanded = false },
            modifier = modifierDropDown
                .width(dropdownWidth)
                .heightIn(max = 300.dp)
                .padding(horizontal = 4.dp, vertical = 1.dp)
                .background(Color.White, RoundedCornerShape(12.dp)),
            containerColor = Color.White,
            shape = RoundedCornerShape(12.dp),
        ) {

            reasons.forEach { reason ->

                val isSelected = reason == selectedReason

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                        .clip(RoundedCornerShape(50.dp))
                        .background(
                            brush = if (isSelected)
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFF4338CA),
                                        Color(0xFF211C64)
                                    )
                                )
                            else Brush.linearGradient(
                                listOf(Color.Unspecified, Color.Unspecified)
                            ),
                            shape = RoundedCornerShape(50.dp)
                        )
                        .clickable(
                            enabled = enabled,  // ✅ disable option click
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            selectedReason = reason
                            expanded = false
                            onSelectionChanged(reason)
                        }
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {

                    Text(
                        text = reason,
                        color = if (isSelected) Color.White else Color.Black,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun CustomMultiSelectPowerSpinner(
    modifier: Modifier = Modifier,
    modifierDropDown: Modifier = Modifier,
    selectedText: String = "Select",
    selectedValues: Set<String> = emptySet(),
    onSelectionChanged: (Set<String>) -> Unit = {},
    horizontalPadding: androidx.compose.ui.unit.Dp = 24.dp,
    reasons: List<String> = listOf(),
    enabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }

    BoxWithConstraints(
        modifier = modifier
            .background(Color.White)
            .alpha(if (enabled) 1f else 0.6f)
    ) {
        val dropdownWidth = maxWidth

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    enabled = enabled,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    expanded = !expanded
                },
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFF697383)),
            shape = RoundedCornerShape(50.dp),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedText,
                    color = if (selectedText.trim().startsWith("Select", ignoreCase = true) || selectedText.isBlank())
                        Color(0xFF697383)
                    else
                        Color.Black,
                    fontSize = 13.sp,
                    fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )

                Image(
                    painter = painterResource(
                        if (expanded) R.drawable.ic_dropdown_show
                        else R.drawable.ic_dropdown_icon
                    ),
                    contentDescription = "",
                )
            }
        }

        DropdownMenu(
            expanded = expanded && enabled,
            onDismissRequest = { expanded = false },
            modifier = modifierDropDown
                .width(dropdownWidth)
                .heightIn(max = 300.dp)
                .padding(horizontal = 4.dp, vertical = 1.dp)
                .background(Color.White, RoundedCornerShape(12.dp)),
            containerColor = Color.White,
            shape = RoundedCornerShape(12.dp),
        ) {
            reasons.forEach { reason ->
                val isSelected = selectedValues.contains(reason)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                        .clip(RoundedCornerShape(50.dp))
                        .background(
                            brush = if (isSelected)
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFF4338CA),
                                        Color(0xFF211C64)
                                    )
                                )
                            else Brush.linearGradient(
                                listOf(Color.Unspecified, Color.Unspecified)
                            ),
                            shape = RoundedCornerShape(50.dp)
                        )
                        .clickable(
                            enabled = enabled,
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            val newSelection = if (isSelected) {
                                selectedValues - reason
                            } else {
                                selectedValues + reason
                            }
                            onSelectionChanged(newSelection)
                        }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = reason,
                        color = if (isSelected) Color.White else Color.Black,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}