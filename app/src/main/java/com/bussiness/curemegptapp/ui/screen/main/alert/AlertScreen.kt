package com.bussiness.curemegptapp.ui.screen.main.alert

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.bussiness.curemegptapp.R
import com.bussiness.curemegptapp.data.model.AlertModel
import com.bussiness.curemegptapp.ui.component.TopBarHeader1
import com.bussiness.curemegptapp.ui.screen.main.scheduleNewAppointment.ScheduleNewAppointmentScreen
import com.bussiness.curemegptapp.viewmodel.alertviewmodel.AlertViewModel
import com.bussiness.curemegptapp.viewmodel.reportviewmodel.ReportViewModel

@Composable
fun AlertScreen(navController: NavHostController,viewModel: AlertViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        viewModel.getAlertListRequest()
    }
    Column(modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFFFF)) .statusBarsPadding()) {

        TopBarHeader1(title = stringResource(R.string.alerts_title), onBackClick = {
            navController.navigateUp()
        })

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 19.dp)
        ) {
            if (state.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No Alerts Available",
                        color = Color(0xFF404657),
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                        fontWeight = FontWeight.Medium
                    )
                }
            } else{
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 50.dp, top = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),

                    ) {
                    items(
                        items = state,
                        key = { it.id ?: (it.title + it.reference_id) }
                    ) { item ->
                        AlertCard(
                            name = item.user_name?:"",
                            title = item.title?:"",
                            description = item.message?:"",
                            time = item.notification_time?:"",
                            showTags = item.action_required == 1,
                            priority = item.severity?.replaceFirstChar { it.uppercase() } ?: "",
                            actionRequired = item.action_required == 1,
                            readStatus = item.appointment_complete_status != "pending",
                            checkBoxShow = when (item.appointment_complete_status?.lowercase()) {
                                "", null,"null" -> false
                                else -> true
                            },
                            onCheckedChange={ checkData->
                                Log.d("AlertScreen", "Checkbox for ${item.title} changed to $checkData")
                                viewModel.updateReadStatus(item.reference_id.toString())
                            }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AlertScreenPreview() {
    val navController = rememberNavController()
    AlertScreen(navController = navController)
}



