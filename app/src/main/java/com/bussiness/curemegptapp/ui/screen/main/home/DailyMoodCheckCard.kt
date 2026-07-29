package com.bussiness.curemegptapp.ui.screen.main.home

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
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
    moodTitle: String,
    moodDescription: String,
    isLoading: Boolean,
    onMoodSelected: (String) -> Unit,
    onClose: () -> Unit,
    onSkip: () -> Unit,
) {

    val moodList = listOf(
        Pair(R.drawable.mood1, stringResource(R.string.mood_low)/*"Low"*/),
        Pair(R.drawable.mood2, stringResource(R.string.mood_down)/*"Down"*/),
        Pair(R.drawable.mood3, stringResource(R.string.mood_neutral)/*"Neutral"*/),
        Pair(R.drawable.mood4, stringResource(R.string.mood_good)/*"Good"*/),
        Pair(R.drawable.mood5, stringResource(R.string.mood_great)/*"Great"*/)
    )

    // Controls which content is shown inside the card:
    // true  -> mood picker (emoji row) is visible
    // false -> selected mood summary (with "Change") is visible
    var showMoodOptions by remember(selectedMood) { mutableStateOf(selectedMood.isEmpty()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(36.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF4338CA))
    ) {

        Column(
            modifier = Modifier
                .padding(horizontal = 13.dp)
                .padding(top = 14.dp, bottom = 1.dp)
                // card ki height smoothly grow/shrink ho jab andar ka content switch ho
                .animateContentSize()
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
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onClose() }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ---------- ANIMATED SWAP: mood picker <-> selected summary ----------
            AnimatedContent(
                targetState = showMoodOptions,
                transitionSpec = {
                    // Naya content halke fade+expand ke saath andar aaye,
                    // purana content fade+shrink ho ke bahar jaye.
                    (fadeIn(animationSpec = tween(300, delayMillis = 90)) +
                            expandVertically(
                                animationSpec = tween(300, delayMillis = 90),
                                expandFrom = Alignment.Top
                            ))
                        .togetherWith(
                            fadeOut(animationSpec = tween(90)) +
                                    shrinkVertically(
                                        animationSpec = tween(90),
                                        shrinkTowards = Alignment.Top
                                    )
                        )
                },
                label = "moodCardContentSwap"
            ) { showOptions ->

                if (showOptions) {

                    Column {
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
                                        showMoodOptions = false
                                        Log.d("MOOD_SELECTED", "Selected Mood: ${item.second}")
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // ---------- SKIP BUTTON ----------
                        TextButton(
                            onClick = { onSkip() },
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

                } else {

                    // ---------- SELECTED MOOD SUMMARY ----------
                    val moodIcon = moodList.firstOrNull { it.second == selectedMood }?.first
                        ?: R.drawable.mood4

                    Column {
                        SelectedMoodSummary(
                            title = moodTitle,
                            description = moodDescription,
                            isLoading = isLoading,
                            moodIcon = moodIcon,
                            moodLabel = selectedMood,
                            onChangeClick = { showMoodOptions = true }
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun SelectedMoodSummary(
    title: String,
    description: String,
    isLoading: Boolean,
    moodIcon: Int,
    moodLabel: String,
    onChangeClick: () -> Unit,
) {

    Column(modifier = Modifier.fillMaxWidth()) {

        // ---------- TOP ROW: icon + "Selected Mood" + Change pill ----------
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            AnimatedSelectedMoodIcon(moodIcon = moodIcon)

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Selected Mood",
                    color = Color.White.copy(alpha = 0.85f),
                    fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                    fontSize = 13.sp
                )
                Text(
                    text = moodLabel,
                    color = Color.White,
                    fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Color.White.copy(alpha = 0.15f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onChangeClick() }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Change",
                    color = Color.White,
                    fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        if (!isLoading){
            // ---------- TITLE ----------
            Text(
                text = title,
                color = Color.White,
                fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            // ---------- DESCRIPTION ----------
            Text(
                text = description,
                color = Color.White.copy(alpha = 0.85f),
                fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        }
    }
}

/**
 * iOS ke SelectedEmojiAnimatedView jaisa "breathing" loop:
 * scale 0.96 <-> 1.12, rotation -5deg <-> +5deg, y-offset +3dp <-> -3dp,
 * shadow 4dp <-> 8dp — sab ek 1.3s easeInOut cycle me, forever, autoreverse ke saath.
 */
@Composable
private fun AnimatedSelectedMoodIcon(
    @DrawableRes moodIcon: Int
) {
    val infiniteTransition = rememberInfiniteTransition(label = "selectedMoodBreathing")

    // 0f -> 1f animate karke usi progress se scale/rotation/offset/shadow sab derive karenge,
    // taaki sab exactly saath me (in-sync) move karein, jaise SwiftUI ke single `isAnimating` bool se hota hai.
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1300, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathingProgress"
    )

    val scale = lerp(0.96f, 1.12f, progress)
    val rotation = lerp(-5f, 5f, progress)
    val offsetY = lerp(3f, -3f, progress)
    val shadowElevation = lerp(4f, 8f, progress)

    // Outer box static hai - sirf background/shape/size container ka kaam karta hai
    Box(
        modifier = Modifier
            .size(55.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = moodIcon),
            contentDescription = null,
            modifier = Modifier
                .size(38.dp)
                .padding(2.dp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    rotationZ = rotation
                    translationY = offsetY.dp.toPx()
                }
                .shadow(
                    elevation = shadowElevation.dp,
                    shape = RoundedCornerShape(10.dp),
                    ambientColor = Color.Black.copy(alpha = 0.18f),
                    spotColor = Color.Black.copy(alpha = 0.18f)
                )
        )
    }
}


private fun lerp(start: Float, stop: Float, fraction: Float): Float =
    start + (stop - start) * fraction

/**
 * Returns (title, description) copy for each mood.
 * Move these into strings.xml (e.g. R.string.mood_good_title / R.string.mood_good_desc)
 * the same way other labels in this screen are handled.
 */
