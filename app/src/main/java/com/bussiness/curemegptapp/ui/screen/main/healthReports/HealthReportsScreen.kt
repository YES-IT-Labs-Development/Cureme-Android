package com.bussiness.curemegptapp.ui.screen.main.healthReports

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
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
import com.bussiness.curemegptapp.navigation.AppDestination
import com.bussiness.curemegptapp.ui.component.CommonHeader
import com.bussiness.curemegptapp.ui.sheet.BottomSheetDialog
import com.bussiness.curemegptapp.ui.sheet.BottomSheetDialogProperties
import com.bussiness.curemegptapp.ui.sheet.FilterHealthReportsBottomSheet
import com.bussiness.curemegptapp.viewmodel.reportviewmodel.ReportViewModel

//Health Reports

@Composable
fun HealthReportsScreen(navController: NavHostController,viewModel: ReportViewModel = hiltViewModel()) {

    var showSheet by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showDeleteDialog1 by remember { mutableStateOf(false) }
    val shareChatMessage = stringResource(R.string.share_chat_message)
    var selectedFilter by remember { mutableStateOf("All") }
    var selectedMember by remember { mutableStateOf<String?>(null) }

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val members by viewModel.memberOptions.collectAsStateWithLifecycle()

    val filteredState = remember(state, searchQuery, selectedFilter, selectedMember, members) {
        state.filter { item ->
            val matchesSearch = if (searchQuery.isBlank()) {
                true
            } else {
                val titleMatches = item.title?.contains(searchQuery, ignoreCase = true) == true
                val memberMatches = item.user_name?.contains(searchQuery, ignoreCase = true) == true
                val severityMatches = item.severity?.contains(searchQuery, ignoreCase = true) == true
                titleMatches || memberMatches || severityMatches
            }

            val matchesFilter = if (selectedFilter == "All" || selectedFilter.isBlank()) {
                true
            } else {
                item.severity?.equals(selectedFilter, ignoreCase = true) == true
            }

            val matchesMember = if (selectedMember == null || selectedMember == "All" || selectedMember == "Select") {
                true
            } else {
                val memberToCompare = if (selectedMember == "My Self") {
                    viewModel.getUserName()
                } else {
                    selectedMember
                }
                item.user_name?.equals(memberToCompare, ignoreCase = true) == true ||
                (memberToCompare?.isNotBlank() == true && item.user_name?.contains(memberToCompare, ignoreCase = true) == true)
            }

            matchesSearch && matchesFilter && matchesMember
        }
    }

    LaunchedEffect(Unit) {
        viewModel.getReportListRequest()
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFFFF)) .statusBarsPadding()
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {

            CommonHeader(stringResource(R.string.health_reports_title))

            Spacer(modifier = Modifier.height(7.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {

                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(40.dp),
                        color = Color(0xFFF4F4F4)
                    ) {
                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            singleLine = true,
                            maxLines = 1,
                            placeholder = {
                                Text(
                                    text = stringResource(R.string.search_placeholder)/*"Search"*/,
                                    color = Color(0xFFBCBCBC),
                                    fontSize = 16.sp,
                                    fontFamily = FontFamily(Font(R.font.urbanist_regular))
                                )
                            },
                            leadingIcon = {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_search_icon),
                                    contentDescription = stringResource(R.string.search_placeholder),
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            textStyle = TextStyle(
                                color = Color.Black,
                                fontSize = 13.sp,
                                fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                                fontWeight = FontWeight.Normal
                            ),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFFF4F4F4),
                                unfocusedContainerColor = Color(0xFFF4F4F4),
                                disabledContainerColor = Color(0xFFF4F4F4),
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Image(
                        painter = painterResource(id = R.drawable.ic_filter_icon),
                        contentDescription = "Filter",
                        modifier = Modifier
                            .wrapContentSize()
                            .clickable( interactionSource = remember { MutableInteractionSource() },
                                indication = null) {
                                showSheet = true
                            }
                    )
                }

                // ---------- LIST ----------
            if (filteredState.isNotEmpty()) {

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(
                        bottom = 100.dp,
                        start = 16.dp,
                        end = 16.dp,
                        top = 16.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(
                        items = filteredState,
                        key = { it.chat_id ?: (it.title + it.chat_date) }
                    ) { item ->

                        ReportCard(
                            icon = R.drawable.ic_app_reporting_icon,
                            title = item.title?:"",
                            patientName = item.user_name?:"",
                            priority = (item.severity ?: "").replaceFirstChar { it.uppercase() },
                            date = item.chat_date?:"",
                            note = item.ai_message?:"",
                            filesCount = item.files_count?:0,
                            onViewClick = {
                                navController.navigate("${AppDestination.ReportScreen}?id=${item.chat_id}")
                            },
                            onShareClick = {
                                viewModel.shareReportPdf(
                                    context = context,
                                    id = item.chat_id ?: 0,
                                    error = { msg ->
                                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No Data Found",
                            color = Color.Gray,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }


    if (showSheet) {
        BottomSheetDialog(
            onDismissRequest = {
                showSheet = false
            },
            properties = BottomSheetDialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = false,
                dismissWithAnimation = true,
                enableEdgeToEdge = false,
            )
        ) {
            FilterHealthReportsBottomSheet(
                memberOptions = members,
                initialSelectedFilter = selectedFilter,
                initialSelectedMember = selectedMember,
                onDismiss = { showSheet = false },
                onApply = { filter, member ->
                    selectedFilter = filter
                    selectedMember = member
                    showSheet = false
                }
            )
        }
    }

//    if (showDeleteDialog) {
//        AlertCardDialog(
//            icon = R.drawable.ic_delete_icon_new,
//            title = "Delete Appointment?",
//            message = "Are you sure you want to delete Peter’s appointment? This action cannot be undone.",
//            confirmText = "Delete",
//            cancelText = "Cancel",
//            onDismiss = { showDeleteDialog = false},
//            onConfirm = {  showDeleteDialog = false
//            }
//        )
//
//    }

}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HealthReportsScreenPreview() {
    // Fake NavController for Preview
    val navController = rememberNavController()
    HealthReportsScreen(navController = navController)
}