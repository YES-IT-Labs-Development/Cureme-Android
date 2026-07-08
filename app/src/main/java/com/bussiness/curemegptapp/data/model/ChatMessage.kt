package com.bussiness.curemegptapp.data.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/*
data class PdfData(
    val uri: Uri,
    val name: String
)

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String? = null,
    val isUser: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(),
    val images: List<Uri> = emptyList(),
    val pdfs: List<PdfData> = emptyList()
)

 */

@Parcelize
data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String? = null,
    val isUser: Boolean = false,
    val images: List<Uri> = emptyList(),
    val pdfs: List<PdfData> = emptyList(),
    val timestamp: Long = System.currentTimeMillis(),
    val isGenerating: Boolean = false,
    val rating: Int = 0,
    val isRated : Boolean = false,
     val severity : Boolean = false,
    val chatId :Int =0
) :  Parcelable

@Parcelize
data class PdfData(
    val uri: Uri,
    val name: String
) : Parcelable



