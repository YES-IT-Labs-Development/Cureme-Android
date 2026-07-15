package com.bussiness.curemegptapp.ui.component.input

import android.content.Intent
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Download
import com.bussiness.curemegptapp.util.DownloadUtils
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import coil.compose.rememberAsyncImagePainter
import coil.compose.AsyncImage
import com.bussiness.curemegptapp.util.AppConstant
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.bussiness.curemegptapp.R
import com.bussiness.curemegptapp.data.model.ChatMessage
import com.bussiness.curemegptapp.data.model.PdfData
import com.bussiness.curemegptapp.ui.component.GradientButton1
import com.bussiness.curemegptapp.ui.theme.AppGradientColors
import com.bussiness.curemegptapp.ui.theme.gradientBrush
import com.bussiness.curemegptapp.ui.viewModel.main.ChatDataViewModel
import androidx.navigation.NavHostController
import androidx.compose.runtime.collectAsState
import com.bussiness.curemegptapp.navigation.AppDestination
import com.bussiness.curemegptapp.ui.viewModel.main.FakeChatDataViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun SessionCartIcon(
    count: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .wrapContentSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(45.dp)
                .clip(CircleShape)
                .background(Color(0xFF5C3843)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_cart_sseion),
                contentDescription = "Cart",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }

        if (count > 0) {
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 4.dp, y = (-4).dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = count.toString(),
                    color = Color(0xFF5C3843),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun ChatHeader(
    logoRes: Int,
    sideArrow: Int,
    filterIcon: Int,
    menuIcon: Int,
    modifier: Modifier = Modifier,
    showCart: Boolean = false,
    cartCount: Int = 1,
    onCartClick: () -> Unit = {},
    onLeftIconClick: () -> Unit,
    onFilterClick: () -> Unit,
   // onMenuClick: () -> Unit,
    menuContent: @Composable () -> Unit
) {
    Column (modifier = Modifier.background(color = Color.White)) {


        Row(
            modifier = modifier
                .fillMaxWidth()
                .background(Color.White).padding(horizontal = 15.dp).padding(top = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Logo
            Image(
                painter = painterResource(id = logoRes),
                contentDescription = "Logo",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .wrapContentWidth()
                    .height(30.dp) // Adjust size according to your logo
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (showCart) {
                    SessionCartIcon(
                        count = cartCount,
                        onClick = onCartClick,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }

                // Notification Icon
                IconButton(onClick = { onLeftIconClick() }) {
                    Icon(
                        painter = painterResource(sideArrow),
                        contentDescription = "Arrow",
                        modifier = Modifier.size(42.dp),
                        tint = Color.Unspecified
                    )
                }


                // Profile Image
                Row(
                    modifier = modifier
                        .width(92.dp)
                        .height(52.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    // ---- Profile Circular Image ----
                    IconButton(onClick = { onFilterClick() }) {
                        Icon(
                            painter = painterResource(filterIcon),
                            contentDescription = "filter",
                            modifier = Modifier.size(42.dp),
                            tint = Color.Unspecified
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // ---- Arrow Icon ----
//                    IconButton(onClick = { onMenuClick() }) {
//                        Icon(
//                            painter = painterResource(menuIcon),
//                            contentDescription = "menu",
//                            modifier = Modifier.size(42.dp),
//                            tint = Color.Unspecified
//                        )
//                    }
                    menuContent()
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Divider(color = Color(0xFFEBE1FF), thickness = 1.dp)
    }
}


//@Composable
//fun BottomMessageBar(
//    modifier: Modifier = Modifier,
//    state: ChatInputState,
//    onEvent: (ChatInputEvent) -> Unit
//) {
//    var showAttachmentMenu by remember { mutableStateOf(false) }
//
//    val imagePickerLauncher = rememberLauncherForActivityResult(
//        ActivityResultContracts.GetContent()
//    ) { uri -> uri?.let { onEvent(ChatInputEvent.OnImageSelected(it)) } }
//
//    val pdfPickerLauncher = rememberLauncherForActivityResult(
//        ActivityResultContracts.GetContent()
//    ) { uri ->
//        uri?.let {
//            onEvent(ChatInputEvent.OnPdfSelected(PdfFile(it, "Document.pdf")))
//        }
//    }
//
//    Column(
//        modifier = modifier
//            .fillMaxWidth()
//            .background(Color.White)
//            .navigationBarsPadding()
//            .padding(horizontal = 12.dp, vertical = 6.dp)
//    ) {
//
//        // -------------------------------
//        // Attachments Preview Row
//        // -------------------------------
//        if (state.images.isNotEmpty() || state.pdfFiles.isNotEmpty()) {
//            AttachmentPreviewRow(
//                state = state,
//                onEvent = onEvent
//            )
//            Spacer(Modifier.height(6.dp))
//        }
//
//        // -------------------------------
//        // Main Input Box
//        // -------------------------------
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .background(Color.White),
//            verticalAlignment = Alignment.Bottom
//        ) {
//
//            // -------------------------------
//            //     CHATGPT STYLE INPUT
//            // -------------------------------
//            Row(
//                modifier = Modifier
//                    .weight(1f)
//                    .background(Color(0xFFF2F4F7), RoundedCornerShape(24.dp))
//                    .padding(horizontal = 12.dp, vertical = 6.dp),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//
//                // ATTACH BUTTON
//                Box {
//                    IconButton(
//                        onClick = { showAttachmentMenu = true },
//                        modifier = Modifier.size(38.dp)
//                    ) {
//                        Icon(
//                            painter = painterResource(R.drawable.attach_ic),
//                            contentDescription = "Attach",
//                            tint = Color(0xFF6B7280)
//                        )
//                    }
//
//                    DropdownMenu(
//                        expanded = showAttachmentMenu,
//                        onDismissRequest = { showAttachmentMenu = false }
//                    ) {
//                        DropdownMenuItem(
//                            text = { Text("Image") },
//                            onClick = {
//                                showAttachmentMenu = false
//                                imagePickerLauncher.launch("image/*")
//                            }
//                        )
//                        DropdownMenuItem(
//                            text = { Text("PDF") },
//                            onClick = {
//                                showAttachmentMenu = false
//                                pdfPickerLauncher.launch("application/pdf")
//                            }
//                        )
//                    }
//                }
//
//                // MESSAGE FIELD
//                BasicTextField(
//                    value = state.message,
//                    onValueChange = { onEvent(ChatInputEvent.OnMessageChanged(it)) },
//                    maxLines = 4,
//                    textStyle = LocalTextStyle.current.copy(
//                        fontSize = 16.sp,
//                        color = Color.Black
//                    ),
//                    decorationBox = { innerTextField ->
//                        if (state.message.isEmpty()) {
//                            Text(
//                                "Message…",
//                                color = Color(0xFF9CA3AF),
//                                fontSize = 16.sp
//                            )
//                        }
//                        innerTextField()
//                    },
//                    modifier = Modifier
//                        .weight(1f)
//                        .padding(horizontal = 6.dp)
//                )
//            }
//
//            Spacer(Modifier.width(10.dp))
//
//            // -----------------------------------
//            //   ANIMATED MIC → SEND BUTTON
//            // -----------------------------------
//            val canSend = state.message.isNotBlank()
//                    || state.images.isNotEmpty()
//                    || state.pdfFiles.isNotEmpty()
//
//            IconButton(
//                onClick = {
//                    if (canSend) onEvent(ChatInputEvent.OnSendClicked)
//                    else onEvent(ChatInputEvent.OnMicClicked)
//                },
//                modifier = Modifier
//                    .size(52.dp)
//                    .clip(CircleShape)
//                    .background(Color(0xFF6633FF))
//            ) {
//                AnimatedContent(
//                    targetState = canSend,
//                    label = ""
//                ) { isSending ->
//                    if (isSending) {
//                        Icon(
//                            painter = painterResource(R.drawable.send_ic),
//                            contentDescription = "Send",
//                            tint = Color.White
//                        )
//                    } else {
//                        Icon(
//                            painter = painterResource(R.drawable.voiceinc_ic),
//                            contentDescription = "Voice Input",
//                            tint = Color.White
//                        )
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun AttachmentPreviewRow(
//    state: ChatInputState,
//    onEvent: (ChatInputEvent) -> Unit
//) {
//    Row(
//        modifier = Modifier.fillMaxWidth(),
//        horizontalArrangement = Arrangement.Start
//    ) {
//
//        // IMAGES
//        state.images.forEach { uri ->
//            Box(
//                modifier = Modifier
//                    .size(70.dp)
//                    .clip(RoundedCornerShape(14.dp))
//                    .background(Color(0xFFEDE9FF))
//            ) {
//                AsyncImage(
//                    model = uri,
//                    contentDescription = null,
//                    modifier = Modifier.fillMaxSize(),
//                    contentScale = ContentScale.Crop
//                )
//
//                Icon(
//                    painter = painterResource(R.drawable.remove_ic),
//                    contentDescription = "Remove",
//                    tint = Color.White,
//                    modifier = Modifier
//                        .padding(4.dp)
//                        .size(22.dp)
//                        .background(Color(0x80000000), CircleShape)
//                        .align(Alignment.TopEnd)
//                        .clickable( interactionSource = remember { MutableInteractionSource() },
  //                      indication = null){ onEvent(ChatInputEvent.RemoveImage(uri)) }
//                )
//            }
//            Spacer(Modifier.width(8.dp))
//        }
//
//        // PDF FILES
//        state.pdfFiles.forEach { pdf ->
//            Surface(
//                color = Color(0xFFF5F0FF),
//                shape = RoundedCornerShape(16.dp),
//                tonalElevation = 1.dp,
//                modifier = Modifier.padding(end = 6.dp)
//            ) {
//                Row(
//                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Icon(
//                        painter = painterResource(R.drawable.pdf_ic),
//                        contentDescription = null
//                    )
//                    Spacer(Modifier.width(6.dp))
//                    Text(
//                        pdf.fileName,
//                        fontSize = 13.sp,
//                        maxLines = 1
//                    )
//                    Spacer(Modifier.width(6.dp))
//                    Icon(
//                        painter = painterResource(R.drawable.remove_ic),
//                        contentDescription = "Remove",
//                        tint = Color.Gray,
//                        modifier = Modifier
//                            .size(18.dp)
//                            .clickable( interactionSource = remember { MutableInteractionSource() },
 //                       indication = null){ onEvent(ChatInputEvent.RemovePdf(pdf)) }
//                    )
//                }
//            }
//        }
//    }
//}


/*
@Composable
fun CommunityChatSection(
    messages: List<ChatMessage>,
    listState: LazyListState,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        var lastDate: String? = null

        items(messages) { message ->
            val currentDate = formatDate(message.timestamp)

            // Show date separator when new date starts
//            if (currentDate != lastDate) {
//                DateSeparator(dateText = currentDate)
//                lastDate = currentDate
//            }

            ChatBubble(message)
        }
    }
}

 */

@Composable
fun CommunityChatSection(
    messages: List<ChatMessage>,
    listState: LazyListState,
    modifier: Modifier = Modifier,
    viewModel: ChatDataViewModel = hiltViewModel(),
    navController: NavHostController? = null
) {
    val chatArgs by viewModel.chatArgs.collectAsState()
    val isCaseChat = chatArgs.type == "case"
    val context = LocalContext.current
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.lastIndex)
        }
    }

    LazyColumn(
        state = listState,
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(messages, key = { it.id }) { message ->
            if (message.isUser) {
                // User message (right-aligned)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.End
                ) {
                    Column(
                        modifier = Modifier.widthIn(max = LocalConfiguration.current.screenWidthDp.dp * 0.7f),
                        horizontalAlignment = Alignment.End
                    ) {
                        // Show images if any
                        if (message.images.isNotEmpty()) {
                            message.images.forEach { imageUri ->
                                Image(
                                    painter = rememberAsyncImagePainter(imageUri),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(max = 200.dp)
                                        .clip(RoundedCornerShape(12.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }

                        if (message.pdfs.isNotEmpty()) {
                            message.pdfs.forEach { pdf ->
                                PdfPreviewCard(
                                    pdf = pdf,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 8.dp),
                                    onClick = {
                                        // PDF open karo
                                        val intent = Intent(Intent.ACTION_VIEW).apply {
                                            setDataAndType(pdf.uri, "application/pdf")
                                            flags = Intent.FLAG_ACTIVITY_NO_HISTORY or
                                                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                                        }
                                        try {
                                            context.startActivity(intent)
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "PDF viewer app nahi mila", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                        // Show text if any
                        if (!message.text.isNullOrBlank()) {
                            Column {
                                Box(
                                    modifier = Modifier
                                        .background(
                                            gradientBrush,
                                            RoundedCornerShape(16.dp)
                                        )
                                        .padding(12.dp)
                                ) {
                                    Text(
                                        text = message.text,
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontFamily = FontFamily(Font(R.font.urbanist_regular))
                                    )
                                }

                                UserMessageActions(
                                    onCopy = { message.text?.let { viewModel.copyMessage(it) } },
                                    onEdit = { viewModel.editMessage(message.id) },
                                    modifier = Modifier.align(Alignment.End)
                                )
                            }

                        }

                    }
                }
            } else {
                // AI message (left-aligned)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Column(
                        modifier = Modifier.widthIn(max = 330.dp)
                    ) {
                        if (message.isGenerating) {
                            // Show typing indicator
                            Row(
                                modifier = Modifier
                                    .padding(start = 0.dp, top = 8.dp, end = 8.dp, bottom = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Image(
                                    painter = painterResource(id = R.drawable.ap_button),
                                    contentDescription = "Profile Picture",
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .align(Alignment.Bottom)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                TypingIndicator()
                            }
                        } else {
                            Row(
                                modifier = Modifier
                                    .padding(start = 0.dp, top = 8.dp, end = 8.dp, bottom = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Image(
                                    painter = painterResource(id = R.drawable.ap_button),
                                    contentDescription = "Profile Picture",
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .align(Alignment.Bottom)
                                )
                                Column(
                                    modifier = Modifier
                                        .background(
                                            Color(0xFFF8F8F8),
                                            RoundedCornerShape(16.dp)
                                        )
                                        .padding(8.dp),

                                )  {


                                // Show images if any
                                if (message.images.isNotEmpty()) {
                                    message.images.forEach { imageUri ->
                                        Image(
                                            painter = rememberAsyncImagePainter(imageUri),
                                            contentDescription = null,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .heightIn(max = 200.dp)
                                                .clip(RoundedCornerShape(12.dp)),
                                            contentScale = ContentScale.Crop
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                    }
                                }

                                // Show text if any
                                if (!message.text.isNullOrBlank()) {
                                    Box(
                                        modifier = Modifier
                                            .padding(12.dp)
                                    ) {
                                        Text(
                                            text = message.text,
                                            color = Color.Black,
                                            fontSize = 14.sp
                                        )
                                    }
                                }
                                if (isCaseChat && !message.text.isNullOrBlank()) {
                                    CaseRecommendations(message.text, navController)
                                }
                                }
                            }
                            if (message.pdfs.isNotEmpty()) {
                                message.pdfs.forEach { pdf ->
                                    Row(
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    ) {
                                        Spacer(Modifier.width(45.dp))
                                        AiPdfCard(
                                            pdf = pdf,
                                            modifier = Modifier.width(280.dp),
                                            onCardClick = {
                                                val intent = Intent(Intent.ACTION_VIEW).apply {
                                                    setDataAndType(pdf.uri, "application/pdf")
                                                    flags = Intent.FLAG_ACTIVITY_NO_HISTORY or
                                                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                                                }
                                                try {
                                                    context.startActivity(intent)
                                                } catch (e: Exception) {
                                                    Toast.makeText(context, "PDF viewer app nahi mila", Toast.LENGTH_SHORT).show()
                                                }
                                            },
                                            onDownloadClick = {
                                                DownloadUtils.downloadFile(context, pdf.uri.toString(), pdf.name)
                                            }
                                        )
                                    }
                                }
                            }
                            if (message.severity) {
                                Row(
                                    modifier = Modifier.padding(vertical = 4.dp)
                                ) {
                                    Spacer(Modifier.width(45.dp))
                                    GradientButton1(
                                        text = "Schedule Appointment",
                                        modifier = Modifier
                                            .width(240.dp)
                                            .height(48.dp),
                                        onClick = {
                                            android.util.Log.d("ScheduleAppointment", "Setting chatId in savedStateHandle: ${chatArgs.chatId}")
                                            navController?.currentBackStackEntry?.savedStateHandle?.set("chatId", chatArgs.chatId)
                                            navController?.navigate(AppDestination.ScheduleNewAppointment)
                                        }
                                    )
                                }
                            }
                            Row{
                                Spacer(Modifier.width(45.dp))
                                AiMessageActions(
                                    message = message,
                                    onCopy = { message.text?.let { viewModel.copyMessage(it) } },
                                    onThumbsUp = { viewModel.rateMessage(message.id, true) },
                                    onThumbsDown = { viewModel.rateMessage(message.id, false) },
                                    onRegenerate = { viewModel.regenerateMessage(message.id) }
                                )
                            }
                            // Action buttons

                        }
                    }
                }
            }
        }
    }
}


@Composable
fun PdfPreviewCard(
    pdf: PdfData,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF2F2F2)).clickable{ onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.PictureAsPdf,
            contentDescription = "PDF",
            tint = Color.Red,
            modifier = Modifier.size(32.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = pdf.name ?: "Document.pdf",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            pdf.name?.let {
                Text(
                    text = it,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}


@Composable
fun AiPdfCard(
    pdf: PdfData,
    modifier: Modifier = Modifier,
    onCardClick: () -> Unit,
    onDownloadClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(50.dp))
            .background(Color(0xFFE2DCF7))
            .clickable { onCardClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left dark circle with PDF icon
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFF7B6EC7)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.PictureAsPdf,
                contentDescription = "PDF Icon",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // File name text
        Text(
            text = pdf.name.takeIf { !it.isNullOrBlank() } ?: "Summary_report.pdf",
            color = Color(0xFF4C3EAD),
            fontSize = 14.sp,
            fontFamily = FontFamily(Font(R.font.urbanist_medium)),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Right white circle with download icon
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.White)
                .clickable { onDownloadClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Download,
                contentDescription = "Download Icon",
                tint = Color(0xFF4C3EAD),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}


@Composable
fun ChatBubble(message: ChatMessage) {

    val bubbleColor = if (message.isUser) Color(0xFF4338CA) else Color(0xFFF8F8F8)
    val alignment = if (message.isUser) Arrangement.End else Arrangement.Start

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 2.dp),
        horizontalAlignment = if (message.isUser) Alignment.End else Alignment.Start
    ) {

        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = alignment,
            modifier = Modifier.padding(vertical = 10.dp)
        ) {

            // Bot profile image
            if (!message.isUser) {
                Image(
                    painter = painterResource(id = R.drawable.ap_button),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                        .align(Alignment.Top)
                )
                Spacer(Modifier.width(8.dp))
            }

            // Chat bubble
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = if (message.isUser) Alignment.End else Alignment.Start
            ) {

                // Chat bubble
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(bubbleColor)
                        .widthIn(
                            max = if (message.isUser)
                                LocalConfiguration.current.screenWidthDp.dp * 0.8f
                            else
                                LocalConfiguration.current.screenWidthDp.dp * 0.7f
                        )
                ) {
                    message.text?.let {
                        Text(
                            text = it,
                            color = if (message.isUser) Color.White else Color.Black,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                // Bot reaction bar (left side)
                if (!message.isUser) {
                    ReactionBar(
                        onCopy = { /* TODO */ },
                        onLike = { /* TODO */ },
                        onDislike = { /* TODO */ }
                    )
                }

                // User action bar (right side)
                else {
                    UserActionBar(
                        onCopy = { /* TODO */ },
                        onEdit = { /* TODO */ }
                    )
                }
            }


        }
        }
}

fun formatDate(timestamp: Long): String {
    val cal = Calendar.getInstance()
    val today = Calendar.getInstance()

    cal.timeInMillis = timestamp

    return when {
        cal.get(Calendar.YEAR) == today.get(Calendar.YEAR)
                && cal.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) -> "Today"

        cal.get(Calendar.YEAR) == today.get(Calendar.YEAR)
                && cal.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) - 1 -> "Yesterday"

        else -> SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(timestamp))
    }
}

@Composable
fun DateSeparator(dateText: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .background(Color(0xFFDDE5E8), RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Text(
                text = dateText,
                fontSize = 12.sp,
                color = Color.Black.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun ReactionBar(
    onCopy: () -> Unit = {},
    onLike: () -> Unit = {},
    onDislike: () -> Unit = {}
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 4.dp, top = 6.dp)
    ) {
        ReactionIcon(R.drawable.copy_ic, "Copy", onCopy)
        ReactionIcon(R.drawable.like_ic, "Like", onLike)
        ReactionIcon(R.drawable.dislike_ic, "Dislike", onDislike)
    }
}

@Composable
fun UserActionBar(
    onCopy: () -> Unit = {},
    onEdit: () -> Unit = {},
){
    Box {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 4.dp, top = 6.dp)
        ) {
            ReactionIcon(R.drawable.copy_ic, "Copy", onCopy)
            ReactionIcon(R.drawable.chat_edit_ic, "Like", onEdit)
        }
    }
}

@Composable
fun ReactionIcon(
    @DrawableRes icon: Int,
    contentDescription: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .size(30.dp)
            .clickable(  interactionSource = remember { MutableInteractionSource() },
                indication = null){ onClick() },
        shape = CircleShape,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.size(30.dp)
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = contentDescription,
                tint = Color.Unspecified,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun AIChatHeader(
    logoRes: Int,
    sideArrow: Int,
    filterIcon: Int,
    modifier: Modifier = Modifier,
    onLeftIconClick: () -> Unit,
    onFilterClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(top = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Logo
        Image(
            painter = painterResource(id = logoRes),
            contentDescription = "Logo",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .wrapContentWidth()
                .height(30.dp) // Adjust size according to your logo
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Notification Icon
            Icon(
                painter = painterResource(sideArrow),
                contentDescription = "Arrow",
                tint = Color(0xFF4338CA),
                modifier = Modifier
                    .size(40.dp)
                    .border(1.dp, Color(0x1A4338CA), CircleShape)
                    .clickable{onLeftIconClick()}
                    .padding(10.dp)
            )


            // Profile Image
            Icon(
                painter = painterResource(filterIcon),
                contentDescription = "filter",
                tint = Color(0xFF4338CA),
                modifier = Modifier
                    .size(40.dp)
                    .border(1.dp, Color(0x1A4338CA), CircleShape)
                    .clickable{onFilterClick()}
                    .padding(10.dp)
            )
        }
    }
}


@Composable
fun NewCaseContent(
    userName: String,
    selectedProfile: String,
    profileList: List<String>,
    questions: List<String>,
    onProfileChange: (String) -> Unit,
    onNewCaseClick: () -> Unit,
    onQuestionClick: (String) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        // Top Icon
        Image(
            painter = painterResource(R.drawable.main_ic),
            contentDescription = null,
            modifier = Modifier
                .wrapContentSize()
                .align(Alignment.CenterHorizontally)
        )

        Spacer(Modifier.height(24.dp))

        // Greeting
        val gradient = Brush.linearGradient(
            colors = AppGradientColors
        )

        val greetingText = remember(userName) {
            buildAnnotatedString {
                append("Good afternoon, ")
                withStyle(SpanStyle(brush = gradient)) {
                    append(userName)
                }
            }
        }

        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = greetingText,
            fontSize = 26.sp,
            fontFamily = FontFamily(Font(R.font.urbanist_medium)),
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )

        Spacer(Modifier.height(8.dp))

        // Profile Selector
        Row( modifier = Modifier
            .height(39.dp)
            .clip(RoundedCornerShape(90.dp))
            .background(Color(0xFFF5F0FF))
            .padding(horizontal = 14.dp, vertical = 0.dp)
            .align(Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Row (
                verticalAlignment = Alignment.CenterVertically
            ){
                Image(
                    painter = painterResource(R.drawable.mage_users),
                    contentDescription = null,
                    modifier = Modifier.wrapContentSize()
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = "Ask for:",
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
            }

            Spacer(Modifier.width(6.dp))

            Box {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable( interactionSource = remember { MutableInteractionSource() },
                        indication = null){ }
                        .clip(RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = selectedProfile,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF4B4BFF)
                    )
                    Icon(
                        painter = painterResource(R.drawable.arrow),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.wrapContentSize()
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // New Case Chat Button
        Box(
            modifier = Modifier
                .width(291.dp)
                .height(55.dp)
                .clip(RoundedCornerShape(45.dp))
                .background(Brush.linearGradient(AppGradientColors))
                .clickable( interactionSource = remember { MutableInteractionSource() },
                        indication = null){ onNewCaseClick() }
                .align(Alignment.CenterHorizontally),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {

                Image(
                    painter = painterResource(R.drawable.page_img),
                    contentDescription = null,
                    modifier = Modifier.wrapContentSize()
                )

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = "New Case Chat",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }



        Spacer(Modifier.height(20.dp))

        // Scrollable Question List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(questions) { question ->
                QuestionItem(
                    question = question,
                    onClick = { onQuestionClick(question) }
                )
            }
        }
    }
}

@Composable
fun QuestionItem(
    question: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .clickable( interactionSource = remember { MutableInteractionSource() },
                        indication = null){ onClick() }
            .padding(horizontal = 16.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.list_ic),
            contentDescription = null,
            modifier = Modifier.size(26.dp)
        )

        Spacer(Modifier.width(12.dp))

        Text(
            text = question,
            fontSize = 16.sp,
            color = Color.Black,
            fontWeight = FontWeight.Medium,
            fontFamily = FontFamily(Font(R.font.urbanist_regular)),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun UserMessageActions(
    onCopy: () -> Unit,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(top = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ActionButton(
            iconId = R.drawable.copy_ic, // You need to add these icons to your drawable folder
            label = "Copy",
            onClick = onCopy
        )

        ActionButton(
            iconId = R.drawable.chat_edit_ic, // You need to add these icons to your drawable folder
            label = "Edit",
            onClick = onEdit
        )
    }
}


@Composable
fun AiMessageActions(
    message: ChatMessage,
    onCopy: () -> Unit,
    onThumbsUp: () -> Unit,
    onThumbsDown: () -> Unit,
    onRegenerate: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        ActionButton(
            iconId = R.drawable.copy_ic,
            label = "Copy",
            onClick = onCopy,
            imageSize = 14.dp
        )

        // LIKE
        ActionButton(
            iconId = if (message.rating == 1) R.drawable.like_filled else R.drawable.like_ic,
            label = "Good",
            onClick = onThumbsUp,
            imageSize = 14.dp
        )

        // DISLIKE
        ActionButton(
            iconId = if (message.rating == -1) R.drawable.dislike_filled else R.drawable.dislike_ic,
            label = "Bad",
            onClick = onThumbsDown,
            imageSize = 14.dp
        )
    }
}


@Composable
private fun ActionButton(
    iconId: Int,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    imageSize: Dp = 14.dp
) {
    Row(
        modifier = modifier
            .shadow(
                elevation = 6.dp,           // shadow size
                shape = CircleShape,        // same shape as clip
                clip = false                // shadow visible rahega
            )
            .clip(CircleShape)               // content ko round banata hai
            .background(Color.White)         // bg required for shadow visibility
            .clickable(onClick = onClick,interactionSource = remember { MutableInteractionSource() },
                indication = null)
            .padding(horizontal = 5.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(id = iconId),
            contentDescription = label,
            modifier = Modifier.size(imageSize),
            tint = Color.Unspecified
        )
//        Text(
//            text = label,
//            color = Color.Gray,
//            fontSize = 12.sp
//        )
    }
}



@Preview(showBackground = true)
@Composable
fun CommunityChatSectionPreview() {

    val sampleMessages = listOf(
        ChatMessage(
            id = "1",
            text = "Hello! How can I help you today?",
            images = emptyList(),
            isUser = false,
            isGenerating = false
        ),
        ChatMessage(
            id = "2",
            text = "Mujhe project ka UI improve karna hai.",
            images = emptyList(),
            isUser = true,
            isGenerating = false
        ),
        ChatMessage(
            id = "3",
            text = "",
            images = emptyList(),
            isUser = false,
            isGenerating = true
        )
    )

    val listState = rememberLazyListState()

    CommunityChatSection(
        messages = sampleMessages,
        listState = listState,

        modifier = Modifier.fillMaxSize()
    )
}


@Composable
fun RightSideDrawer(
    drawerWidth: Dp = 300.dp,
    drawerState: Boolean,
    onClose: () -> Unit,
    drawerContent: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val offsetX by animateDpAsState(
        targetValue = if (drawerState) screenWidth - drawerWidth else screenWidth,
        label = "drawerAnimation"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // Main Screen
        content()

        // Overlay
        if (drawerState) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .clickable(interactionSource = remember { MutableInteractionSource() },
                        indication = null) { onClose() }
            )
        }

        // Drawer
        Box(
            modifier = Modifier
                .offset(x = offsetX)
                .width(drawerWidth)
                .fillMaxHeight()
                .background(Color.Unspecified)
        ) {
            drawerContent()
        }
    }
}


/*
@Composable
fun AiMessageActions(

    onCopy: () -> Unit,
    onThumbsUp: () -> Unit,
    onThumbsDown: () -> Unit,
    onRegenerate: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(top = 1.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        ActionButton(
            iconId = R.drawable.copy_ic,
            label = "Copy",
            onClick = onCopy,
            imageSize = 14.dp
        )

        ActionButton(
            iconId = R.drawable.like_ic,
            label = "Good",
            onClick = onThumbsUp,
            imageSize = 14.dp
        )

        ActionButton(
            iconId = R.drawable.dislike_ic,
            label = "Bad",
            onClick = onThumbsDown,
            imageSize = 14.dp
        )

        ActionButton(
            iconId = R.drawable.ic_regernate_icon2,
            label = "Regenerate",
            onClick = onRegenerate,
            imageSize = 14.dp
        )
    }
}
 */



//fun regenerateMessage(messageId: String) {
//    viewModelScope.launch {
//        val index = _messages.value.indexOfLast { it.id == messageId }
//        if (index != -1) {
//            // Show loading
//            _messages.update {
//                it.toMutableList().apply {
//                    this[index] = this[index].copy(isGenerating = true)
//                }
//            }
//            // Simulate API call
//            delay(800)
//            // Update with new response
//            _messages.update {
//                it.toMutableList().apply {
//                    this[index] = this[index].copy(
//                        text = "Regenerated response",
//                        isGenerating = false
//                    )
//                }
//            }
//        }
//    }
//}

@Composable
fun CaseRecommendations(
    messageText: String,
    navController: NavHostController?
) {
    val textLower = messageText.lowercase()
    val recommendMedication = listOf("medication", "pill", "tablet", "medicine", "dosage", "prescription", "ibuprofen", "paracetamol", "aspirin").any { textLower.contains(it) }

    if (recommendMedication) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .background(Color.White, RoundedCornerShape(12.dp))
                .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(12.dp))
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Case Suggestions & Actions",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF4338CA),
                fontFamily = FontFamily(Font(R.font.urbanist_semibold))
            )

            if (recommendMedication) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFEEF2FF), RoundedCornerShape(8.dp))
                        .border(1.dp, Color(0xFFC7D2FE), RoundedCornerShape(8.dp))
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Add Medication",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF4338CA),
                            fontFamily = FontFamily(Font(R.font.urbanist_medium))
                        )
                        Text(
                            text = "Log medication for health concern",
                            fontSize = 10.sp,
                            color = Color(0xFF312E81),
                            fontFamily = FontFamily(Font(R.font.urbanist_regular))
                        )
                    }
                    Box(
                        modifier = Modifier
                            .background(
                                brush = Brush.horizontalGradient(
                                    listOf(Color(0xFF463CC5), Color(0xFF4D42D4))
                                ),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable {
                                navController?.navigate(AppDestination.AddMedication)
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Add",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontFamily = FontFamily(Font(R.font.urbanist_semibold))
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TypingIndicator(
    modifier: Modifier = Modifier,
    dotSize: Dp = 8.dp,
    dotColor: Color = Color(0xFF258694),
    dotCount: Int = 3
) {
    val transition = rememberInfiniteTransition(label = "typing")
    val dotAnimations = (0 until dotCount).map { index ->
        transition.animateFloat(
            initialValue = 0f,
            targetValue = -6f,
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    durationMillis = 1000
                    0f at index * 150
                    -6f at index * 150 + 300 with FastOutSlowInEasing
                    0f at index * 150 + 600
                },
                repeatMode = RepeatMode.Restart
            ),
            label = "dotAnimation$index"
        )
    }

    Row(
        modifier = modifier
            .background(Color(0xFFF8F8F8), RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        dotAnimations.forEach { anim ->
            Box(
                modifier = Modifier
                    .size(dotSize)
                    .graphicsLayer(translationY = anim.value)
                    .background(dotColor, CircleShape)
            )
        }
    }
}



