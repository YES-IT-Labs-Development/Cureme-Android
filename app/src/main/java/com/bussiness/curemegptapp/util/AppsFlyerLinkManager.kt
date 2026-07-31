package com.bussiness.curemegptapp.util

import com.appsflyer.share.LinkGenerator

object AppsFlyerLinkManager {

    fun generateChatLink(
        familyMemberID: String,
        chatId: String,
        memberName: String,
        type: String,
        chatHistory: Boolean = false,
    ): String {

        val encryptedChatId = chatId.toIntOrNull()
            ?.let { LinkEncryptionHelper.encrypt(it) }
            ?: chatId

        val encryptedFamilyId = familyMemberID.toIntOrNull()
            ?.let { LinkEncryptionHelper.encrypt(it) }
            ?: familyMemberID

        val encryptedMemberName = LinkEncryptionHelper.encryptString(memberName)

        return LinkGenerator("share").apply {

            setBaseURL(
                "Ehr9",
                "share-chat.onelink.me",
                "com.bussiness.curemegptapp"
            )

            campaign = "chat_share"
            channel = "share"

            addParameter("deep_link_value", "chat")

            addParameter("chatId", encryptedChatId)
            addParameter("familyMemberID", encryptedFamilyId)
            addParameter("memberName", encryptedMemberName)
            addParameter("type", type)
            addParameter("chatHistory", chatHistory.toString())

        }.generateLink()
    }

    fun generateReportLink(
        reportId: String
    ): String {

        val encryptedReportId = reportId.toIntOrNull()
            ?.let { LinkEncryptionHelper.encrypt(it) }
            ?: reportId

        return LinkGenerator("share").apply {

            setBaseURL(
                "Ehr9",
                "share-chat.onelink.me",
                "com.bussiness.curemegptapp"
            )

            campaign = "report_share"
            channel = "share"

            addParameter("deep_link_value", "report")
            addParameter("reportId", encryptedReportId)

        }.generateLink()
    }
}