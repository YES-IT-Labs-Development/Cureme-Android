package com.bussiness.curemegptapp.ui.screen.main.chat

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.bussiness.curemegptapp.R
import com.bussiness.curemegptapp.navigation.AppDestination
import com.bussiness.curemegptapp.ui.component.BottomMessageBar1
import com.bussiness.curemegptapp.ui.component.GradientRedButton
import com.bussiness.curemegptapp.ui.component.input.AIChatHeader
import com.bussiness.curemegptapp.ui.component.input.RightSideDrawer
import com.bussiness.curemegptapp.ui.dialog.CaseDialog
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import com.bussiness.curemegptapp.util.AppConstant
import com.bussiness.curemegptapp.ui.viewModel.main.ChatViewModel
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.imePadding
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import com.bussiness.curemegptapp.apimodel.chatModel.FamilyDetails
import com.bussiness.curemegptapp.data.model.ChatMessage
import com.bussiness.curemegptapp.ui.theme.AppGradientColors
import com.bussiness.curemegptapp.ui.viewModel.main.PromptViewModel
import com.bussiness.curemegptapp.ui.dialog.ShareChatDialog
import java.util.Calendar

@Composable
fun OpenChatScreen(
    navController: NavHostController,
    from: String? = "",
    viewModel: PromptViewModel = hiltViewModel()
) {

    var showMenu by remember { mutableStateOf(false) }
    var selectedUser by remember { mutableStateOf<FamilyDetails?>(null) }
    var showUserDropdown by remember { mutableStateOf(false) }
    var showCaseDialog by remember { mutableStateOf(false) }
    var showShareDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showRenameSheet by remember { mutableStateOf(false) }
    var chatToRename by remember { mutableStateOf<Pair<String, String>?>(null) }

    val chatViewModel: ChatViewModel = hiltViewModel()
    val chatInputState by chatViewModel.uiState.collectAsState()
    val messages by chatViewModel.messages.collectAsState()

    var shouldNavigate by remember { mutableStateOf(false) }
    var showDrawer by remember { mutableStateOf(false) }

    val chatHistoryList by viewModel.historyChatList.collectAsState()

    val response by viewModel.promptQuestions.collectAsState()
    val familyList = response.family_details
    val questionsList = response.prompt_questions
    fun getGreeting(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

        return when (hour) {
            in 0..11 -> "Good Morning"
            in 12..16 -> "Good Afternoon"
            in 17..20 -> "Good Evening"
            else -> "Good Night"
        }
    }

    val greeting = remember { getGreeting() }


    LaunchedEffect(familyList) {
        if (familyList.isNotEmpty() && selectedUser == null) {
            selectedUser = familyList.first()
        }
    }

    LaunchedEffect(showDrawer) {
        if (showDrawer) {
            viewModel.getUserChatHistoryList()
        }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty() && shouldNavigate) {

            val handle = navController.currentBackStackEntry?.savedStateHandle
            val hasMyself = selectedUser?.relationship?.contains("Myself", true)
            handle?.set("chatId", 0)
            handle?.set("familyMemberId", if (hasMyself == true) 0 else selectedUser?.id ?: 0)
            handle?.set("textMessage", viewModel.getMessage())
            handle?.set("type", "normal")
            handle?.set("familyList", familyList)
            handle?.set("chatHistory", false)


            navController.navigate(AppDestination.ChatDataScreen) {
                popUpTo("openChatScreen") { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
            shouldNavigate = false
        }
    }


    if (showRenameSheet && chatToRename != null) {
        RenameChatBottomSheet(
            id = chatToRename!!.first,
            currentName = chatToRename!!.second,
            onDismiss = { showRenameSheet = false },
            onSave = { newName ->
                viewModel.renameChat(
                    chatToRename!!.first,
                    newName,
                    success = {
                    })
                showDrawer = false
                showRenameSheet = false
            }
        )
    }


    BackHandler {
        if (from == "auth") {
            navController.navigate(AppDestination.MainScreen) {
                popUpTo(0)
                launchSingleTop = true
            }
        } else {
            navController.navigate(AppDestination.Home) {
                popUpTo(0)
                launchSingleTop = true
            }
        }
    }

    RightSideDrawer(
        drawerState = showDrawer,
        onClose = { showDrawer = false },
        drawerWidth = 320.dp,
        drawerContent = {
            MenuDrawer(
                onDismiss = { showDrawer = false },
                selectedUser = selectedUser?.name ?: "",
                onUserChange = { user ->
                    val hasMyself =
                        user?.relationship?.contains("Myself", ignoreCase = true) ?: false
                    if (!hasMyself) {
                        viewModel.getUserFamilyChatList(user.id)
                    } else {
                        viewModel.getUserChatHistoryList()
                    }
                },
                onClickNewCaseChat = {
                    showCaseDialog = true
                },
                familyList = familyList,
                chatHistory = chatHistoryList,
                onRenameClick = { id, newName ->
                    chatToRename = id to newName
                    showRenameSheet = true
                },
                onShareClick = {
                    showShareDialog = true
                    showDrawer = false
                }, onDeleteClick = { id ->
                    viewModel.deleteChat(id, {
                        Toast.makeText(context, "Chat Deleted Successfully", Toast.LENGTH_LONG)
                            .show()
                        showDrawer = false
                    })
                },
                onChatHistoryClick = { id, type ->
                    val message = ChatMessage(text = "", isUser = true)
                    val handle = navController.currentBackStackEntry?.savedStateHandle
                    val hasMyself = selectedUser?.relationship?.contains("Myself", true)
                    handle?.set("chatId", id)
                    handle?.set(
                        "familyMemberId",
                        if (hasMyself == true) 0 else selectedUser?.id ?: 0
                    )
                    handle?.set("textMessage", message)
                    handle?.set("type", type)
                    handle?.set("familyList", familyList)
                    handle?.set("chatHistory", true)

                    navController.navigate(AppDestination.ChatDataScreen) {
                        popUpTo("openChatScreen") { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                    shouldNavigate = false
                },
                onNewChatClick = {
                    val handle = navController.currentBackStackEntry?.savedStateHandle
                    val hasMyself = selectedUser?.relationship?.contains("Myself", true)
                    val chatMessage = ChatMessage(text = "", isUser = true)
                    handle?.set("chatId", 0)
                    handle?.set(
                        "familyMemberId",
                        if (hasMyself == true) 0 else selectedUser?.id ?: 0
                    )
                    handle?.set("textMessage", chatMessage)
                    handle?.set("type", "normal")
                    handle?.set("familyList", familyList)
                    handle?.set("chatHistory", false)
                    navController.navigate(AppDestination.ChatDataScreen)
                }
            )
        }
    ) {

        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp)
            ) {

                AIChatHeader(
                    logoRes = R.drawable.ic_logo,
                    sideArrow = R.drawable.left_ic,
                    filterIcon = R.drawable.filter_ic,
                    onLeftIconClick = {
                        if (from == "auth") {
                            navController.navigate(AppDestination.MainScreen) {
                                popUpTo(0)
                                launchSingleTop = true
                            }
                        } else {
                            navController.navigate(AppDestination.Home) {
                                popUpTo(0)
                                launchSingleTop = true
                            }
                        }
                    },
                    onFilterClick = { showDrawer = true },
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .imePadding()
                ) {

                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        item {
                            Spacer(Modifier.height(30.dp))

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth(0.8f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.main_ic),
                                    contentDescription = null
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                val gradient = Brush.linearGradient(
                                    colors = AppGradientColors
                                )

                                val greetingText = remember(selectedUser?.name ?: "") {
                                    buildAnnotatedString {
                                        append("Good afternoon, ")
                                        withStyle(SpanStyle(brush = gradient)) {
                                            append(selectedUser?.name ?: "")
                                        }
                                    }
                                }

                                Text(
                                    modifier = Modifier.align(Alignment.CenterHorizontally),
                                    text = greetingText,
                                    fontSize = 24.sp,
                                    fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Black,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { showUserDropdown = !showUserDropdown },
                                    shape = RoundedCornerShape(30.dp),
                                    color = Color(0xFFF0EDFF),
                                ) {
                                    Row(
                                        modifier = Modifier.padding(8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // LEFT SIDE
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                                        ) {
                                            val imageUrl = selectedUser?.profile_photo?.let {
                                                if (it.isEmpty()) null
                                                else if (it.startsWith("http://") || it.startsWith("https://")) it
                                                else AppConstant.IMAGE_BASE_URL + it
                                            }
                                            val hasValidImage = !imageUrl.isNullOrEmpty() &&
                                                    imageUrl != AppConstant.IMAGE_BASE_URL &&
                                                    imageUrl != "https://curemegpt.tgastaging.com" &&
                                                    imageUrl != "https://curemegpt.tgastaging.com/"

                                            if (hasValidImage) {
                                                AsyncImage(
                                                    model = imageUrl,
                                                    contentDescription = null,
                                                    modifier = Modifier
                                                        .size(24.dp)
                                                        .clip(CircleShape),
                                                    contentScale = ContentScale.Crop
                                                )
                                            } else {
                                                Image(
                                                    painter = painterResource(R.drawable.ic_chat_circle_person_icon),
                                                    contentDescription = null,
                                                    modifier = Modifier.size(24.dp)
                                                )
                                            }
                                            Text(
                                                text = "Ask for:",
                                                fontWeight = FontWeight.Medium,
                                                fontSize = 16.sp
                                            )
                                        }

                                        // RIGHT SIDE
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(5.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = selectedUser?.let { "${it.name} (${it.relationship})" }
                                                    ?: "",
                                                color = Color(0xFF5B47DB),
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Medium,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                modifier = Modifier.weight(1f, fill = false),

                                                )

                                            Image(
                                                painter = painterResource(
                                                    if (showUserDropdown) R.drawable.ic_dropdown_show
                                                    else R.drawable.ic_dropdown_icon
                                                ),
                                                contentDescription = null,
                                            )
                                        }
                                    }
                                }

                                // ✅ DROPDOWN
                                if (showUserDropdown) {
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 4.dp)
                                    ) {
                                        Column {
                                            familyList.forEach { user ->
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clickable {
                                                            selectedUser = user
                                                            showUserDropdown = false
                                                        }
                                                        .padding(16.dp)
                                                ) {
                                                    Row(
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        modifier = Modifier.fillMaxWidth()
                                                    ) {
                                                        Text(text = "${user.name} (${user.relationship})")
                                                        if (user == selectedUser) {
                                                            Image(
                                                                painter = painterResource(R.drawable.ic_tick_icon),
                                                                contentDescription = null
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                GradientRedButton(
                                    text = "New Case Chat",
                                    icon = R.drawable.page_img,
                                    modifier = Modifier
                                        .fillMaxWidth()
//                                        .padding(horizontal = 10.dp)
                                    ,
                                    height = 56.dp,
                                    fontSize = 14.sp,
                                    imageSize = 16.dp,
                                    gradientColors = listOf(Color(0xFF4338CA), Color(0xFF211C64)),
                                    onClick = { showCaseDialog = true }
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                            }
                        }

                        items(questionsList) { question ->
                            Log.d("TAG", "OpenChatScreen: $question")
                            QuestionCard(
                                question = question.question,
                                category = question.category,
                                onClick = {
                                    val message = ChatMessage(
                                        text = question.question,
                                        isUser = true
                                    )
                                    val handle =
                                        navController.currentBackStackEntry?.savedStateHandle
                                    val hasMyself =
                                        selectedUser?.relationship?.contains("Myself", true)
                                    handle?.set("chatId", 0)
                                    handle?.set(
                                        "familyMemberId",
                                        if (hasMyself == true) 0 else selectedUser?.id ?: 0
                                    )
                                    handle?.set("textMessage", message)
                                    handle?.set("type", "normal")
                                    handle?.set("familyList", familyList)
                                    handle?.set("chatHistory", false)

                                    navController.navigate(AppDestination.ChatDataScreen) {
                                        popUpTo("openChatScreen") { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                    shouldNavigate = false

                                }
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    BottomMessageBar1(
                        modifier = Modifier.fillMaxWidth(),
                        state = chatInputState,
                        viewModel = chatViewModel,
                        onSendClicked = { chatMessage ->
                            viewModel.setMessage(chatMessage)
                            shouldNavigate = true
                        }
                    )
                }
            }

            if (showCaseDialog) {
                CaseDialog(
                    onDismiss = { showCaseDialog = false },
                    onConfirm = {
                        val handle = navController.currentBackStackEntry?.savedStateHandle
                        val hasMyself = selectedUser?.relationship?.contains("Myself", true)
                        val chatMessage = ChatMessage(text = "", isUser = true)
                        handle?.set("chatId", 0)
                        handle?.set(
                            "familyMemberId",
                            if (hasMyself == true) 0 else selectedUser?.id ?: 0
                        )
                        handle?.set("textMessage", chatMessage)
                        handle?.set("type", "case")
                        handle?.set("familyList", familyList)
                        handle?.set("chatHistory", false)
                        navController.navigate(AppDestination.ChatDataScreen)

                        showCaseDialog = false
                    }
                )
            }

            if (showShareDialog) {
                ShareChatDialog(
                    onDismiss = { showShareDialog = false }
                )
            }
        }
    }
}


@Composable
fun QuestionCard(question: String, category: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CircleShape)
            .clickable { onClick() },
        shape = CircleShape,
        color = Color(0xFFF5F5F5)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Top
        ) {
            val imageRes = when (category) {
                "General" -> R.drawable.ic_circle_page_image
                "GetFit" -> R.drawable.ic_g_icon
                else -> R.drawable.ic_circle_page_image
            }

            Image(
                painter = painterResource(imageRes),
                contentDescription = null,
                modifier = Modifier
                    .size(51.dp)
                    .align(Alignment.CenterVertically)
            )

            Text(
                question,
                fontSize = 16.sp,
                modifier = Modifier
                    .weight(1f)
                    .align(alignment = Alignment.CenterVertically),
                fontFamily = FontFamily(Font(R.font.urbanist_regular))
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun OpenChatScreenPreview() {
    val navController = rememberNavController()
    OpenChatScreen(navController = navController)
}
