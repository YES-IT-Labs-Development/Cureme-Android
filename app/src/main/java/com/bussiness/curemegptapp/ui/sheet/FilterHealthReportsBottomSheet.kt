package com.bussiness.curemegptapp.ui.sheet

//FilterHealthReportsBottomSheet

import android.content.res.Resources
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bussiness.curemegptapp.R
import com.bussiness.curemegptapp.ui.component.CancelButton
import com.bussiness.curemegptapp.ui.component.ContinueButton
import com.bussiness.curemegptapp.ui.component.input.CustomPowerSpinner

@Composable
fun FilterHealthReportsBottomSheet(
    memberOptions : List<String> = listOf("John Doe", "Jane Smith", "Alice Johnson", "Bob Williams"),
    initialSelectedFilter: String = "All",
    initialSelectedMember: String? = null,
    onDismiss: () -> Unit,
    onApply: (selectedFilter: String, selectedMember: String?) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf(initialSelectedFilter) }

    val combinedMemberOptions = remember(memberOptions) {
        if (memberOptions.contains("All")) {
            memberOptions
        } else {
            listOf("All") + memberOptions
        }
    }

    var selectedMember by remember {
        mutableStateOf(
            if (initialSelectedMember != null && combinedMemberOptions.contains(initialSelectedMember)) {
                initialSelectedMember
            } else {
                "All"
            }
        )
    }
    var showDropdown by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(RoundedCornerShape(topEnd = 30.dp, topStart = 30.dp))
            .background(Color.White)
            .padding(24.dp)
    ) {
        // Top indicator line
        Box(
            modifier = Modifier
                .width(82.dp)
                .height(4.dp)
                .background(Color(0xFFD0D0D0), RoundedCornerShape(2.dp))
                .align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(19.dp))

        Text(
            text = "Filter Health Reports",
            fontSize = 18.sp,
            fontFamily = FontFamily(Font(R.font.urbanist_medium)),
            fontWeight = FontWeight.Medium,
            color = Color(0xFF374151),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Divider(color = Color(0xFFEBE1FF), thickness = 1.dp)

        Spacer(modifier = Modifier.height(19.dp))

        // 🔵 All Reports Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    selectedFilter = "All"
                },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "All Reports",
                fontSize = 16.sp,
                fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                fontWeight = FontWeight.Medium,
                color = if (selectedFilter == "All") Color(0xFF4338CA) else Color(0xFF374151)
            )

            if (selectedFilter == "All") {
                Image(
                    painter = painterResource(id = R.drawable.ic_tick_icon),
                    contentDescription = "Selected",
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // 🔵 Attention Required Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    selectedFilter = "Attention"
                },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Attention Required",
                fontSize = 16.sp,
                fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                fontWeight = FontWeight.Medium,
                color = if (selectedFilter == "Attention") Color(0xFF4338CA) else Color(0xFF374151)
            )

            if (selectedFilter == "Attention") {
                Image(
                    painter = painterResource(id = R.drawable.ic_tick_icon),
                    contentDescription = "Selected",
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // 🔵 Normal Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    selectedFilter = "Normal"
                },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Normal",
                fontSize = 16.sp,
                fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                fontWeight = FontWeight.Medium,
                color = if (selectedFilter == "Normal") Color(0xFF4338CA) else Color(0xFF374151)
            )

            if (selectedFilter == "Normal") {
                Image(
                    painter = painterResource(id = R.drawable.ic_tick_icon),
                    contentDescription = "Selected",
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(19.dp))

        // Member label
        Text(
            text = "Member",
            fontSize = 15.sp,
            fontFamily = FontFamily(Font(R.font.urbanist_medium)),
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(8.dp))

        CustomPowerSpinner(
            selectedText = selectedMember,
            onSelectionChanged = { reason ->
                selectedMember = reason
            },
            horizontalPadding = 24.dp,
            reasons = combinedMemberOptions,
            fontSize = 16.sp,
            dropdownFontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(19.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CancelButton(title = "Cancel") {
                onDismiss()
            }

            ContinueButton(text = "Apply") {
                onApply(selectedFilter, if (selectedMember == "All" || selectedMember == "Select") null else selectedMember)
            }
        }
    }
}

@Preview(
    name = "Filter Bottom Sheet - Default",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
fun FilterHealthReportsBottomSheetPreview() {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(700.dp)
            .background(Color.White)
    ) {
        FilterHealthReportsBottomSheet(
            onDismiss = { println("Dismiss clicked") },
            onApply = { filter, member ->
                println("Filter: $filter, Member: $member")
                Log.d("09122025","Filter: $filter, Member: $member")
            }
        )
    }
}



