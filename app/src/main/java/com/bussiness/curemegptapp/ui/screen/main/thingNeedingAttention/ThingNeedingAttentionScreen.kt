package com.bussiness.curemegptapp.ui.screen.main.thingNeedingAttention

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.bussiness.curemegptapp.R
import com.bussiness.curemegptapp.ui.component.AttentionItem
import com.bussiness.curemegptapp.ui.component.SettingHeader
import com.bussiness.curemegptapp.ui.viewModel.main.HomeViewModel
import com.bussiness.curemegptapp.apimodel.homemodel.FamilyX
import com.bussiness.curemegptapp.navigation.AppDestination

@Composable
fun ThingNeedingAttentionScreen(
    navController: NavHostController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiStateHome by viewModel.uiStateHome.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getHomeRequest()
    }

    ThingNeedingAttentionContent(
        navController = navController,
        attentionItems = uiStateHome?.needAttentionList ?: emptyList(),
        onScheduleClick = { item ->
            val symptom = item.symptoms?.getOrNull(0) ?: ""
            viewModel.removeLocalAlert(symptom)
            navController.navigate(AppDestination.ScheduleNewAppointment)
        }
    )
}

@Composable
fun ThingNeedingAttentionContent(
    navController: NavHostController,
    attentionItems: List<FamilyX>,
    onScheduleClick: (FamilyX) -> Unit = {}
) {
    var backPressedTime by remember { mutableStateOf(0L) }

    Column(
        Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
    ) {

        SettingHeader(
            title = stringResource(R.string.thing_needing_attention_title),
            onBackClick = {
                val currentTime = System.currentTimeMillis()
                if (currentTime - backPressedTime > 1000) { // 1 second threshold
                    backPressedTime = currentTime
                    navController.popBackStack()
                }
            }
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 15.dp, vertical = 30.dp)
        ) {
            items(attentionItems) { item ->
                AttentionItem(
                    title = item.symptoms?.getOrNull(0) ?: "",
                    subtitle = if (item.name?.startsWith("For:") == true) item.name else "For: ${item.name ?: ""}",
                    isUrgent = false,
                    onScheduleClick = { onScheduleClick(item) }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ThingNeedingAttentionScreenPreview() {
    val navController = rememberNavController()
    ThingNeedingAttentionContent(
        navController = navController,
        attentionItems = listOf(
            FamilyX(
                name = "James Logan",
                symptoms = mutableListOf("Tooth Pain Symptoms Detected")
            ),
            FamilyX(
                name = "Rosy Logan",
                symptoms = mutableListOf("Overdue Dental Cleaning")
            )
        )
    )
}
