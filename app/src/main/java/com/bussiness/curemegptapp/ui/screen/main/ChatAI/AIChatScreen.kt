package com.bussiness.curemegptapp.ui.screen.main.ChatAI

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.bussiness.curemegptapp.R
import com.bussiness.curemegptapp.navigation.AppDestination
import com.bussiness.curemegptapp.ui.component.input.AIChatHeader
import com.bussiness.curemegptapp.ui.component.input.NewCaseContent
import com.bussiness.curemegptapp.ui.screen.main.chat.OpenChatScreen
import com.bussiness.curemegptapp.ui.viewModel.main.ChatViewModel

@Composable
fun AIChatScreen(navController: NavHostController) {
    val viewModel: ChatViewModel = hiltViewModel()
    val uiState = viewModel.uiState.collectAsState()
    Log.d("ChatScreen", "User is on Ai chat screen")
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AIChatHeader(
            logoRes = R.drawable.ic_logo,
            sideArrow = R.drawable.left_ic,
            filterIcon = R.drawable.filter_ic,
            onLeftIconClick = { navController.popBackStack() },
            onFilterClick = {},
        )

        Spacer(Modifier.height(10.dp))

        NewCaseContent(
            userName = "James",
            selectedProfile = "James (Myself)",
            profileList = listOf("Family Member", "Friend", "Other"),
            questions = listOf(
                "Why do my teeth hurt when I drink something cold?",
                "What’s the best way to treat bleeding gums at home?",
                "Do I need to remove my wisdom tooth if it hurts?",
                "Why do I keep getting frequent headaches?",
                "How can I tell if my stomach pain is serious?",
                "What should I do if I feel dizzy often?"
            ),
            onProfileChange = {},
            onNewCaseClick = { navController.navigate(AppDestination.ChatScreen)},
            onQuestionClick = {}
        )
    }

    Box(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 15.dp)
    ) {
//        BottomMessageBar(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 16.dp, vertical = 10.dp).align(Alignment.BottomCenter),
//            state = uiState.value,
//            onEvent = viewModel::onEvent
//        )
    }

}



@Preview(showBackground = true)
@Composable
fun AIChatScreenPreview() {
    val navController = rememberNavController()
    AIChatScreen(navController = navController)
}