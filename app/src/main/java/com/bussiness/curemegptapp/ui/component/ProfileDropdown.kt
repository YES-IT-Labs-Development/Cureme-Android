// Create a new file: ProfileDropdown.kt in your ui.component package
package com.bussiness.curemegptapp.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.bussiness.curemegptapp.ui.viewModel.main.Profile
import com.bussiness.curemegptapp.R

@Composable
fun ProfileDropdown(
    selectedProfile: Profile?,
    profiles: List<Profile>,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    onSelectProfile: (Profile) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
    ) {
        // Clickable dropdown header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
                .clickable(interactionSource = remember { MutableInteractionSource() },
                    indication = null) { onToggle() }
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                selectedProfile?.let { profile ->
                    // Profile icon
                    Icon(
                        painter = painterResource(id = profile.iconResId),
                        contentDescription = profile.name,
                        modifier = Modifier.size(20.dp),
                        tint = Color.Unspecified
                    )

                    // Profile name
                    Text(
                        text = profile.name,
                        color = Color.Black,
                        fontSize = 14.sp
                    )
                } ?: run {
                    // Placeholder when no profile is selected
                    Text(
                        text = "Select Profile",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            }

            // Dropdown arrow
            Icon(
                painter = painterResource(if (isExpanded) R.drawable.ic_dropdown_show else R.drawable.ic_dropdown_icon),
                contentDescription = "Expand/Collapse",
                tint = Color.Gray
            )
        }

        // Dropdown menu
        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = onToggle,
            modifier = Modifier
                .widthIn(min = 200.dp)
                .background(Color.White)
                .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
        ) {
            profiles.forEach { profile ->
                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            // Profile icon
                            Icon(
                                painter = painterResource(id = profile.iconResId),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = Color.Unspecified
                            )

                            // Profile info
                            Column {
                                Text(
                                    text = profile.name,
                                    fontSize = 14.sp,
                                    color = Color.Black
                                )
                                Text(
                                    text = profile.description,
                                    fontSize = 12.sp,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }
                    },
                    onClick = {
                        onSelectProfile(profile)
                        onToggle()
                    }
                )
            }
        }
    }
}