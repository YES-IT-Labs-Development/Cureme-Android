package com.bussiness.curemegptapp.ui.screen.main.ChatAI

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.bussiness.curemegptapp.R
import com.bussiness.curemegptapp.ui.component.BottomMessageBar
import com.bussiness.curemegptapp.ui.component.input.ChatHeader
import com.bussiness.curemegptapp.ui.component.input.CommunityChatSection
import com.bussiness.curemegptapp.ui.screen.main.chat.OpenChatScreen
import com.bussiness.curemegptapp.ui.screen.main.chat.SwitchShareDeletePopUpMenu
import com.bussiness.curemegptapp.ui.viewModel.main.ChatViewModel


@Composable
fun ChatScreen(navController  : NavHostController) {

    val viewModel: ChatViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val messages by viewModel.messages.collectAsState()
    val listState = rememberLazyListState()
    Log.d("ChatScreen", "User is on  chat screen")
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        /** HEADER */
        ChatHeader(
            logoRes = R.drawable.ic_logo,
            sideArrow = R.drawable.left_ic,
            filterIcon = R.drawable.filter_ic,
            menuIcon = R.drawable.menu_ic,
            onLeftIconClick = { navController.popBackStack() },
            onFilterClick = {},
          //  onMenuClick = {}
            menuContent = {
                SwitchShareDeletePopUpMenu(
                    switchText = "Switch to Case",
                    onSwitchClick = {
                        // Share logic
                    },  onShareClick = {
                        // Share logic
                    },
                    onDeleteClick = {
                        // Delete logic
                    }
                )
            }
        )

        /** CHAT BODY + MESSAGE BAR */
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .imePadding()
        ) {
            CommunityChatSection(
                messages = messages,
                listState = listState,
                navController = navController,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 90.dp)
            )


            BottomMessageBar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 5.dp, vertical = 10.dp),
                state = uiState,
                viewModel = viewModel
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {
    val navController = rememberNavController()
    ChatScreen(navController = navController)
}