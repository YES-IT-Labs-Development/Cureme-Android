package com.bussiness.curemegptapp.ui.screen.main.home

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bussiness.curemegptapp.R
import com.bussiness.curemegptapp.ui.component.MoodOptionSelectable

@Composable
fun DailyMoodCheckCard(
    selectedMood: String,
    onMoodSelected: (String) -> Unit,
    onClose: () -> Unit,
    onSkip: () -> Unit,
) {


    val moodList = listOf(
        Pair(R.drawable.mood1, stringResource(R.string.mood_low)/*"Low"*/),
        Pair(R.drawable.mood2, stringResource(R.string.mood_down)/*"Down"*/),
        Pair(R.drawable.mood3, stringResource(R.string.mood_neutral)/*"Neutral"*/),
        Pair(R.drawable.mood4, stringResource(R.string.mood_good)/*"Good"*/),
        Pair(R.drawable.mood5,  stringResource(R.string.mood_great)/*"Great"*/)
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(36.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF4338CA))
    ) {

        Column(
            modifier = Modifier.padding(horizontal = 13.dp).padding(top = 14.dp, bottom = 1.dp)
        ) {

            // ---------- TOP ROW ----------
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.daily_mood_chekcer_main_icon),
                        contentDescription = null,
                        modifier = Modifier.size(45.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = stringResource(R.string.daily_mood_check_title)/*"Daily Mood Check"*/,
                        fontSize = 18.sp,
                        fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }

                Image(
                    painter = painterResource(id = R.drawable.ic_close_icon_mood),
                    contentDescription = "Close",
                    modifier = Modifier
                        .size(45.dp)
                        .clickable(  interactionSource = remember { MutableInteractionSource() },
                            indication = null){ onClose() }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ---------- EMOJI OPTIONS ----------
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                moodList.forEach { item ->
                    MoodOptionSelectable(
                        icon = item.first,
                        label = item.second,
                        isSelected = selectedMood == item.second,
                        onClick = {
                            onMoodSelected(item.second)
                            Log.d("MOOD_SELECTED", "Selected Mood: $selectedMood")
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ---------- SKIP BUTTON ----------
            TextButton(
                onClick = { onSkip()  },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = stringResource(R.string.skip_for_now)/*"Skip for Now"*/,
                    color = Color.White,
                    fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp
                )
            }
        }
    }
}