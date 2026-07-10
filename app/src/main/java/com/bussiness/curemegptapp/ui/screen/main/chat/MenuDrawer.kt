package com.bussiness.curemegptapp.ui.screen.main.chat

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bussiness.curemegptapp.R
import com.bussiness.curemegptapp.apimodel.chatModel.ChatHistoryItem
import com.bussiness.curemegptapp.apimodel.chatModel.FamilyDetails
import com.bussiness.curemegptapp.apimodel.scheduleAppointment.FamilyModel
import com.bussiness.curemegptapp.ui.component.LayerShadowBlueButton
import com.bussiness.curemegptapp.ui.screen.main.schedule.EditDeleteMenu
import com.bussiness.curemegptapp.ui.theme.gradientBrush
import android.util.Log
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.text.style.TextOverflow
import coil.compose.AsyncImage
import com.bussiness.curemegptapp.util.AppConstant
import androidx.compose.foundation.shape.CircleShape

@Composable
fun MenuDrawer(
               onDismiss: () -> Unit,
               selectedUser: String, onUserChange: (FamilyDetails) -> Unit,
               onClickNewCaseChat:()-> Unit,
               familyList: List<FamilyDetails>,
               chatHistory : MutableList<ChatHistoryItem>,
               onRenameClick: (id :String, title :String) -> Unit,
               onDeleteClick: (id:String) -> Unit,
               onShareClick :() ->Unit,
               onChatHistoryClick :(id:Int, type:String) ->Unit,
               onNewChatClick : ()->Unit,
               isCaseChat: Boolean = false
               ) {

    Log.d("ChatHistory", "chatHistory size = ${chatHistory.size}")

    val normalHistory = remember(chatHistory) {
        chatHistory.filter { it.type != "case" }.toMutableList()
    }

    val caseHistory = remember(chatHistory) {
        chatHistory.filter { it.type == "case" }.toMutableList()
    }


    var selectedUserCurr by remember {
        mutableStateOf(selectedUser)
    }

    LaunchedEffect(selectedUser) {
        selectedUserCurr = selectedUser
    }

    var selectedCategory by remember { mutableStateOf("normal") } // "normal" or "case"
    var showCategoryDropdown by remember { mutableStateOf(false) }
    var selectedChatIndex by remember { mutableStateOf<Int?>(null) }
    val userCaseHistoryMap = familyList

    var searchQuery by remember { mutableStateOf("") }
    var showUserDropdown1 by remember { mutableStateOf(false) }
    val users = familyList


    val filteredNormalHistory = remember(normalHistory, searchQuery) {
        if (searchQuery.isBlank()) {
            normalHistory
        } else {
            normalHistory.filter {
                it.title?.contains(searchQuery, ignoreCase = true) == true
            }
        }
    }

    val filteredCaseHistory = remember(caseHistory, searchQuery) {
        if (searchQuery.isBlank()) {
            caseHistory
        } else {
            caseHistory.filter {
                it.title?.contains(searchQuery, ignoreCase = true) == true
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize().clip(shape = RoundedCornerShape(topStart = 40.dp,bottomStart = 40.dp))
            .background(Color.White)
            .clickable(interactionSource = remember { MutableInteractionSource() },
                indication = null) { onDismiss() }
    ) {
        Surface(
            modifier = Modifier
                .fillMaxHeight().padding(vertical = 10.dp)
                .width(340.dp)
                .align(Alignment.CenterEnd)
                .clickable(enabled = false, interactionSource = remember { MutableInteractionSource() },
                    indication = null) { },
            color = Color.White,
            shape = RoundedCornerShape(topStart = 40.dp,bottomStart = 40.dp)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    // Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {

                            Image(
                                painter = painterResource(id = R.drawable.ic_logo),
                                contentDescription = stringResource(R.string.logo_icon_description),
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .wrapContentWidth()
                                    .height(30.dp) // Adjust size according to your logo
                            )
                        }

                    }

                    Surface(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(40.dp),
                        color = Color(0xFFF4F4F4),
                    ) {
                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            singleLine = true,
                            maxLines = 1,
                            placeholder = {
                                Text(
                                    text = stringResource(R.string.search_placeholder),
                                    color = Color(0xFFBCBCBC),
                                    fontSize = 16.sp,
                                    fontFamily = FontFamily(Font(R.font.urbanist_regular))
                                )
                            },
                            leadingIcon = {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_search_icon),
                                    contentDescription = stringResource(R.string.search_icon_description),
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


                    Spacer(modifier = Modifier.height(16.dp))



                    LayerShadowBlueButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .padding(horizontal = 16.dp),
                        onClick = { onNewChatClick() }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_add_chat_icon),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.new_chat_button),
                                color = Color.White,
                                fontSize = 16.sp,
                                fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    LayerShadowBlueButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .padding(horizontal = 16.dp),
                        onClick = { onClickNewCaseChat() }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.page_img),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.new_case_chat_button),
                                color = Color.White,
                                fontSize = 16.sp,
                                fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))



                    Spacer(modifier = Modifier.height(24.dp))

                    // User Selector
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth().clickable(
                                enabled = !isCaseChat,
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ){
                                showUserDropdown1 = !showUserDropdown1
                            }
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(30.dp),
                        border = BorderStroke(1.dp, if (isCaseChat) Color(0xFFE2E8F0) else Color(0xFFE7E9EC)),
                        color = if (isCaseChat) Color(0xFFF8FAFC) else Color.Unspecified
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = selectedUserCurr, 
                                color = if (isCaseChat) Color.Gray else Color(0xFF4338CA), 
                                fontWeight = FontWeight.Medium
                            )
                            if (!isCaseChat) {
                                Image(
                                    painter = painterResource(if (showUserDropdown1) R.drawable.ic_dropdown_show else R.drawable.ic_dropdown_icon), 
                                    contentDescription = null
                                )
                            }
                        }
                    }

                    if (showUserDropdown1 && !isCaseChat) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth().padding(horizontal = 5.dp)
                                .wrapContentHeight(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 8.dp)
                            ) {
                                users.forEachIndexed { index, user ->
                                    val isMyself = user.relationship?.trim()?.equals("myself", ignoreCase = true) == true
                                    val displayName = if (isMyself) "${user.name} (Myself)" else user.name
                                    val isSelected = displayName == selectedUserCurr

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(44.dp)
                                            .clickable(interactionSource = remember { MutableInteractionSource() },
                                                indication = null
                                            ) {
                                                showUserDropdown1 = !showUserDropdown1
                                                selectedUserCurr = displayName
                                                onUserChange(user)
                                            }
                                            .padding(horizontal = 16.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxSize(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = displayName,
                                                modifier = Modifier.weight(1f),
                                                fontSize = 16.sp,
                                                fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                                                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                                                color = if (isSelected) Color(0xFF4338CA) else Color(0xFF374151),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )

                                            if (isSelected) {
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Image(
                                                    painter = painterResource(id = R.drawable.ic_tick_icon),
                                                    contentDescription = stringResource(R.string.selected_icon_description),
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Chat History Selector Button with Dropdown Icon
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(interactionSource = remember { MutableInteractionSource() },
                                indication = null) { showCategoryDropdown = !showCategoryDropdown }
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val categoryLabel = if (selectedCategory == "normal") {
                            stringResource(R.string.chat_history_title)
                        } else {
                            stringResource(R.string.case_chat_history_title)
                        }
                        Text(categoryLabel, fontSize = 20.sp, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.width(10.dp))
                        Icon(
                            painter = painterResource(if (showCategoryDropdown) R.drawable.ic_dropdown_show else R.drawable.ic_dropdown_icon),
                            contentDescription = null
                        )
                    }

                    if (showCategoryDropdown) {
                        // Dropdown Card listing options
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                                .wrapContentHeight(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 8.dp)
                            ) {
                                listOf("normal", "case").forEach { category ->
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(44.dp)
                                            .clickable(interactionSource = remember { MutableInteractionSource() },
                                                indication = null
                                            ) {
                                                selectedCategory = category
                                                showCategoryDropdown = false
                                            }
                                            .padding(horizontal = 16.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxSize(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            val labelText = if (category == "normal") {
                                                stringResource(R.string.chat_history_title)
                                            } else {
                                                stringResource(R.string.case_chat_history_title)
                                            }
                                            Text(
                                                text = labelText,
                                                modifier = Modifier.weight(1f),
                                                fontSize = 16.sp,
                                                fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                                                fontWeight = if (category == selectedCategory) FontWeight.Medium else FontWeight.Normal,
                                                color = if (category == selectedCategory) Color(0xFF4338CA) else Color(0xFF374151),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )

                                            if (category == selectedCategory) {
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Image(
                                                    painter = painterResource(id = R.drawable.ic_tick_icon),
                                                    contentDescription = stringResource(R.string.selected_icon_description),
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Chat History Items (Always shown, filtered by selectedCategory)
                val activeList = if (selectedCategory == "normal") filteredNormalHistory else filteredCaseHistory
                if (activeList.isEmpty()) {
                    item {
                        val emptyText = if (selectedCategory == "normal") {
                            "No chat history found"
                        } else {
                            "No case chat history found"
                        }
                        Text(
                            text = emptyText,
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    items(activeList.size) { index ->
                        val chatItem = activeList[index]
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(30.dp),
                            color = Color(0xFFF5F0FF)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(interactionSource = remember { MutableInteractionSource() },
                                        indication = null) {
                                        onChatHistoryClick(chatItem.id?.toInt() ?: 0, chatItem.type)
                                    }
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Image(
                                        painter = painterResource(R.drawable.ic_clock_new_icon),
                                        contentDescription = stringResource(R.string.clock_icon_description),
                                        modifier = Modifier.size(31.dp)
                                    )
                                    Text(chatItem.title ?: "", fontSize = 14.sp)
                                }

                                RenameDeleteShareMenu(
                                    modifier = Modifier,
                                    onEditClick = { onRenameClick(chatItem.id ?: "0", chatItem.title ?: "") },
                                    onShareClick = { onShareClick() },
                                    onDeleteClick = { onDeleteClick(chatItem.id ?: "0") }
                                )
                            }
                        }
                    }
                }


                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}
