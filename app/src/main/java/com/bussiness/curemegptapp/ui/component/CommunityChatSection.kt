package com.bussiness.curemegptapp.ui.component

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.bussiness.curemegptapp.data.model.ChatMessage

@Composable
fun CommunityChatSection(
    messages: List<ChatMessage>,
    listState: LazyListState,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier
) {
    LazyColumn(
        state = listState,
        modifier = modifier,
        reverseLayout = false
    ) {
        items(messages) { msg ->
//            ChatMessageBubble(message = msg)
        }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.lastIndex)
        }
    }
}