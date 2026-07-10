package com.bussiness.curemegptapp.ui.screen.main.chat

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester.Companion.createRefs
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.bussiness.curemegptapp.R
import com.bussiness.curemegptapp.ui.component.BottomMessageBar2
import com.bussiness.curemegptapp.ui.component.input.ChatHeader
import com.bussiness.curemegptapp.ui.component.input.CommunityChatSection
import com.bussiness.curemegptapp.ui.viewModel.main.ChatDataViewModel
// Dono imports important hain:
import androidx.constraintlayout.compose.*  // ConstraintLayout, createRefs, etc.
import androidx.constraintlayout.compose.Dimension  // Dimension class ke liye
import com.bussiness.curemegptapp.apimodel.chatModel.FamilyDetails
import com.bussiness.curemegptapp.data.model.ChatMessage
import com.bussiness.curemegptapp.ui.component.input.RightSideDrawer
import com.bussiness.curemegptapp.ui.dialog.DeleteChatDialog
import com.bussiness.curemegptapp.ui.dialog.ShareChatDialog
import com.bussiness.curemegptapp.ui.dialog.SwitchToDialog

@SuppressLint("SuspiciousIndentation")
@Composable
fun ChatDataScreen(
    navController: NavHostController,
    viewModel: ChatDataViewModel = hiltViewModel(),
) {

    val handle = navController.previousBackStackEntry?.savedStateHandle
    val chatId = handle?.get<Int>("chatId") ?: 0
    val familyMemberId = handle?.get<Int>("familyMemberId") ?: 0
    val chatMessage = handle?.get<ChatMessage>("textMessage")
    var type = handle?.get<String>("type") ?: "normal"
    val familyList = handle?.get<List<FamilyDetails>>("familyList") ?: emptyList()
    val chatHistory = handle?.get("chatHistory") ?: false
    val currentHandle = navController.currentBackStackEntry?.savedStateHandle
    val isInitialized = currentHandle?.get<Boolean>("isInitialized") ?: false
    val context = LocalContext.current
    var showRenameSheet by remember { mutableStateOf(false) }
    var chatToRename by remember { mutableStateOf<Pair<String, String>?>(null) }
    val selectedMember = familyList.find { it.id == familyMemberId } ?: familyList.find {
        it.relationship.equals("myself", ignoreCase = true)
    }

    val chatHistoryList by viewModel.historyChatList.collectAsState()

    Log.d("ChatScreen", "familyMemberId: $familyMemberId")
    Log.d("ChatScreen", "chatHistory: $chatHistory")
    Log.d("ChatScreen", "chatId: $chatId")

    LaunchedEffect(Unit) {
        if (!isInitialized) {
            viewModel.setChatArgs(
                context = context, chatId = chatId, familyMemberId = familyMemberId,
                chatMessage = chatMessage, type = type, familyList = familyList
            )
            if (!chatHistory) {
                viewModel.onChatScreenOpened(
                    chatId = chatId,
                    type = type,
                    message = chatMessage?.text,
                    familyMemberId = familyMemberId,
                    images = chatMessage?.images ?: emptyList(),
                    pdfs = chatMessage?.pdfs ?: emptyList()
                )
            } else {
                viewModel.getChatHistoryData(chatId, type)
            }
            currentHandle?.set("isInitialized", true)
        }
    }

    val uiState by viewModel.uiState.collectAsState()
    val messages by viewModel.messages.collectAsState()
    val chatArgs by viewModel.chatArgs.collectAsState()
    val listState = rememberLazyListState()
    var showSwitchDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showShareDialog by remember { mutableStateOf(false) }
    var showDrawer by remember { mutableStateOf(false) }
    var selectedUser by remember { mutableStateOf("James (Myself)") }
    var showCaseDialog by remember { mutableStateOf(false) }
    val shareChatMessage = stringResource(R.string.share_chat_message)

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


    LaunchedEffect(showDrawer) {
        if (showDrawer) {
            if (familyMemberId == 0) {
                viewModel.getUserChatHistoryList()
            } else {
                viewModel.getUserFamilyChatList(familyMemberId)
            }
        }
    }

    RightSideDrawer(
        drawerState = showDrawer,
        onClose = { showDrawer = false },
        drawerWidth = 320.dp,
        drawerContent = {
            val drawerSelectedUser = selectedMember?.let { member ->
                val isMyself = member.relationship?.trim()?.equals("myself", ignoreCase = true) == true
                if (isMyself) {
                    "${member.name} (Myself)"
                } else {
                    member.name
                }
            } ?: ""

            MenuDrawer(
                onDismiss = { showDrawer = false },
                selectedUser = drawerSelectedUser,
                onUserChange = { user ->
                    selectedUser = user.name
                    val hasMyself =
                        user.relationship?.contains("Myself", ignoreCase = true) ?: false
                    if (!hasMyself) {
                        viewModel.getUserFamilyChatList(user.id)
                    } else {
                        viewModel.getUserChatHistoryList()
                    }
                },
                onClickNewCaseChat = {
                    viewModel.switchNewChat(context, "case")
                    showDrawer = false
                },
                familyList = familyList,
                chatHistory = chatHistoryList,   // mutableListOf() ki jagah actual list
                onRenameClick = { id, newName ->
                    chatToRename = id to newName
                    showRenameSheet = true
                },
                onShareClick = {
                    showShareDialog = true
                    showDrawer = false
                },
                onDeleteClick = { id ->
                    viewModel.deleteChat(id) {
                        Toast.makeText(
                            context,
                            "Chat Deleted Successfully",
                            Toast.LENGTH_LONG
                        ).show()
                        showDrawer = false
                    }
                },
                onChatHistoryClick = { id, typeString ->
                    viewModel.getChatHistoryData(id, typeString)
                },
                onNewChatClick = {
                    viewModel.switchNewChat(context, "normal")
                    showDrawer = false
                },
                isCaseChat = chatArgs.type == "case"
            )


//            MenuDrawer(
//                onDismiss = { showDrawer = false },
//                selectedUser = selectedUser,
//                onUserChange = {
//                    selectedUser = it.name
//                    showDrawer = false
//                },
//                onClickNewCaseChat = {
//                    showCaseDialog = true
//                },
//                familyList = familyList,
//                chatHistory = mutableListOf(),
//                onRenameClick = { id, newName ->
//
//                },
//                onShareClick = {
//
//                }, onDeleteClick = {
//
//                }
//            )
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
        ) {
            // Background Image
            Image(
                painter = painterResource(id = R.drawable.chat_background),
                contentDescription = stringResource(R.string.chat_background_description),
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.FillBounds
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(12.dp))

                ChatHeader(
                    logoRes = R.drawable.ic_logo,
                    sideArrow = R.drawable.ic_cross_icon,
                    filterIcon = R.drawable.ic_filter_menu_icon3,
                    menuIcon = R.drawable.ic_menu_icon3,
                    onLeftIconClick = { navController.popBackStack() },
                    onFilterClick = {
                        showDrawer = true
                    },
                    menuContent = {
                        SwitchShareDeletePopUpMenu(
                            switchText = if (chatArgs.type == "case") stringResource(R.string.switch_to_normal_text) else stringResource(
                                R.string.switch_to_case_text
                            ),
                            showSwitch = true,
                            onSwitchClick = {
                                showSwitchDialog = true
                            },
                            onShareClick = {
                                showShareDialog = true
                            },
                            onDeleteClick = {
                                showDeleteDialog = true
                            }
                        )
                    }
                )

                ConstraintLayout(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    val (chatSection, messageBar) = createRefs()

                    CommunityChatSection(
                        messages = messages,
                        listState = listState,
                        viewModel = viewModel,
                        navController = navController,
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .constrainAs(chatSection) {
                                top.linkTo(parent.top)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                                bottom.linkTo(messageBar.top)
                                height = Dimension.fillToConstraints
                            }
                    )

                    BottomMessageBar2(
                        modifier = Modifier
                            .fillMaxWidth()
                            .constrainAs(messageBar) {
                                bottom.linkTo(parent.bottom)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                            },
                        state = uiState,
                        viewModel = viewModel,
                        familyList, familyMemberId
                    )
                }
            }
        }
    }

    if (showSwitchDialog) {
        val isCaseChat = chatArgs.type == "case"
        SwitchToDialog(
            title = if (isCaseChat) stringResource(R.string.switch_to_normal_dialog_title) else stringResource(
                R.string.switch_to_case_dialog_title
            ),
            description = if (isCaseChat) stringResource(R.string.switch_to_normal_dialog_description) else stringResource(
                R.string.switch_to_case_dialog_description
            ),
            buttonText = if (isCaseChat) stringResource(R.string.stay_on_case_chat_button) else stringResource(
                R.string.stay_on_normal_chat_button
            ),
            onDismiss = {
                showSwitchDialog = false
            },
            onConfirm = {
                val currentChatId = viewModel.chatArgs.value.chatId
                val targetType = if (isCaseChat) "normal" else "case"
                val successToastMessage =
                    if (isCaseChat) "Switched to Normal Chat Successfully" else "Switched to Case Chat Successfully"
                if (currentChatId == 0) {
                    Toast.makeText(context, successToastMessage, Toast.LENGTH_LONG).show()
                    type = targetType
                    viewModel.updateChatType(targetType)
                } else {
                    viewModel.switchChat(
                        chatId = currentChatId,
                        success = { message ->
                            Log.d("SwitchChat", message)

                            Toast.makeText(
                                context,
                                successToastMessage,
                                Toast.LENGTH_LONG
                            ).show()
                            type = targetType
                            viewModel.updateChatType(targetType)
                        },
                        error = { errorMessage ->
                            Log.e("SwitchChat", errorMessage)
                            Toast.makeText(
                                context,
                                errorMessage,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    )
                }
                showSwitchDialog = false
            }
        )
    }
    if (showDeleteDialog) {
        DeleteChatDialog(
            title = stringResource(R.string.delete_chat_dialog_title),
            message = stringResource(R.string.delete_chat_dialog_message),
            warningText = stringResource(R.string.delete_chat_warning_text),
            bottomText = stringResource(R.string.delete_chat_bottom_text),
            cancelText = stringResource(R.string.cancel_button),
            confirmText = stringResource(R.string.delete_chat_confirm_button),
            onDismiss = { showDeleteDialog = false },
            onConfirm = {
                showDeleteDialog = false
                viewModel.deleteChat(chatId.toString()) {
                    Toast.makeText(
                        context,
                        "Chat Deleted Successfully",
                        Toast.LENGTH_LONG
                    ).show()
                    navController.popBackStack()
                }
            }
        )
    }

    if (showShareDialog) {
        ShareChatDialog(
            onDismiss = { showShareDialog = false }
        )
    }

}


@Preview(showBackground = true)
@Composable
fun ChatDataScreenPreview() {
    val navController = rememberNavController()
    // ChatDataScreen(navController = navController)
}


