package com.bussiness.curemegptapp.ui.sheet

import android.content.res.Resources
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
fun FilterAppointmentsBottomSheet(
    onDismiss: () -> Unit,
    memberOptions: List<String> = listOf("John Doe", "Jane Smith", "Alice Johnson", "Bob Williams"),
    initialSelectedFilter: String = "Upcoming",
    initialSelectedMember: String? = null,
    onApply: (selectedFilter: String, selectedMember: String?) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf(initialSelectedFilter) }

    // Prepend "All" to the list of member options if not already present
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
        modifier = modifier
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
            text = "Filter Appointments",
            fontSize = 18.sp,
            fontFamily = FontFamily(Font(R.font.urbanist_medium)),
            fontWeight = FontWeight.Medium,
            color = Color(0xFF374151),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Divider(color = Color(0xFFEBE1FF), thickness = 1.dp)

        Spacer(modifier = Modifier.height(19.dp))

        // ------------------------------
        // 🔵 Upcoming Row
        // ------------------------------
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    selectedFilter = "Upcoming"
                },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Upcoming Appointments",
                fontSize = 16.sp,
                fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                fontWeight = FontWeight.Medium,
                color = if (selectedFilter == "Upcoming") Color(0xFF4338CA) else Color(0xFF374151)
            )

            if (selectedFilter == "Upcoming") {
                Image(
                    painter = painterResource(id = R.drawable.ic_tick_icon),
                    contentDescription = "Selected",
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // ------------------------------
        // 🔵 Past Row
        // ------------------------------
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    selectedFilter = "Past"
                },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Past Appointments",
                fontSize = 16.sp,
                fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                fontWeight = FontWeight.Medium,
                color = if (selectedFilter == "Past") Color(0xFF4338CA) else Color(0xFF374151)
            )

            if (selectedFilter == "Past") {
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
            reasons = combinedMemberOptions
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
                onApply(selectedFilter, if (selectedMember == "All") null else selectedMember)
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
fun FilterAppointmentsBottomSheetPreview() {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(700.dp)
            .background(Color.White)
    ) {
        FilterAppointmentsBottomSheet(
            onDismiss = { println("Dismiss clicked") },
            onApply = { filter, member ->
                println("Filter: $filter, Member: $member")
            }
        )
    }
}



