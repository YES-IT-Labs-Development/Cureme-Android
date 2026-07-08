package com.bussiness.curemegptapp.apimodel

import com.bussiness.curemegptapp.apimodel.chatModel.FamilyDetails
import com.bussiness.curemegptapp.data.model.ChatMessage

data class ChatScreenArgs(var chatId: Int = 0,
                          val familyMemberId: Int = 0,
                          val chatMessage: ChatMessage? = null,
                          val type: String = "normal",
                          val familyList: List<FamilyDetails> = emptyList())
