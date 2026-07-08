package com.bussiness.curemegptapp.ui.sheet



//FilterFamilyMembersTypeSheet


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.OutlinedButton

@Composable
fun FilterFamilyMembersTypeSheet(
    members: List<String> = listOf(
        "All Members",
        "Children",
        "Adults",
        "Seniors",
        "Friends",
        "Relatives"
    ),
    selectedMember: String = "All Members",
    onMemberSelected: (String) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(RoundedCornerShape(topEnd = 30.dp, topStart = 30.dp))
            .background(Color.White)
            .padding(20.dp)
    ) {
        // Top Indicator
        Box(
            modifier = Modifier
                .width(82.dp)
                .height(4.dp)
                .background(Color(0xFFD0D0D0), RoundedCornerShape(2.dp))
                .align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Title
        Text(
            text = "Filter Family Members",
            fontSize = 18.sp,
            fontFamily = FontFamily(Font(R.font.urbanist_medium)),
            fontWeight = FontWeight.Medium,
            color = Color(0xFF374151),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Divider(color = Color(0xFFEBE1FF), thickness = 1.dp)

        Spacer(modifier = Modifier.height(10.dp))

        // LazyColumn List
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            userScrollEnabled = false
        ) {
            items(members) { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .clickable( interactionSource = remember { MutableInteractionSource() },
                            indication = null) {
                            onMemberSelected(item)
                        },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item,
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                        fontWeight = FontWeight.Medium,
                        color = if (selectedMember == item)
                            Color(0xFF4338CA) else Color(0xFF374151)
                    )

                    if (selectedMember == item) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_tick_icon),
                            contentDescription = "Selected",
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))


    }
}

@Preview(
    name = "Filter Bottom Sheet - Default",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
fun FilterFamilyMembersTypeSheetPreview() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(700.dp)
            .background(Color.White)
    ) {
        FilterFamilyMembersTypeSheet()
    }
}
