package com.bussiness.curemegptapp.ui.dialog

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import java.io.File
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.bussiness.curemegptapp.R
import com.bussiness.curemegptapp.ui.theme.AppGradientColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement

@Composable
fun SummaryDialog(
    summaryData: com.bussiness.curemegptapp.apimodel.chatModel.SummaryData,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val formattedText = remember(summaryData) { formatSummaryToText(summaryData) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnClickOutside = false,
            dismissOnBackPress = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Color.White)
                .padding(20.dp)
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_check),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(38.dp)
                    )

                    Spacer(modifier = Modifier.width(11.dp))

                    Text(
                        text = "Summary",
                        fontSize = 17.sp,
                        fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f)
                    )

                    Icon(
                        painter = painterResource(id = R.drawable.ic_close),
                        contentDescription = "Close",
                        tint = Color.Unspecified,
                        modifier = Modifier
                            .size(38.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onDismiss() }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Box(
                    modifier = Modifier
                        .height(300.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Title
                        if (!summaryData.title.isNullOrBlank()) {
                            Text(
                                text = summaryData.title,
                                fontSize = 16.sp,
                                fontFamily = FontFamily(Font(R.font.urbanist_bold)),
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }

                        // Summary
                        if (!summaryData.summary.isNullOrBlank()) {
                            SummarySection(title = "Summary Description", content = summaryData.summary)
                        }

                        // Symptoms
                        val symptoms = summaryData.symptoms?.filter { !it.isNullOrBlank() }
                        if (!symptoms.isNullOrEmpty()) {
                            BulletSection(title = "Symptoms", items = symptoms)
                        }

                        // Recommendations
                        val recommendations = summaryData.recommendations?.filter { !it.isNullOrBlank() }
                        if (!recommendations.isNullOrEmpty()) {
                            BulletSection(title = "Recommendations", items = recommendations)
                        }

                        // Medications
                        val meds = summaryData.medications?.filter { !it?.name.isNullOrBlank() }
                        if (!meds.isNullOrEmpty()) {
                            MedicationsSection(meds = meds)
                        }

                        // Next Steps
                        val nextSteps = summaryData.next_steps?.filter { !it.isNullOrBlank() }
                        if (!nextSteps.isNullOrEmpty()) {
                            BulletSection(title = "Next Steps", items = nextSteps)
                        }

                        // Fallback when all fields are empty or null
                        if (summaryData.title.isNullOrBlank() &&
                            summaryData.summary.isNullOrBlank() &&
                            symptoms.isNullOrEmpty() &&
                            recommendations.isNullOrEmpty() &&
                            meds.isNullOrEmpty() &&
                            nextSteps.isNullOrEmpty()
                        ) {
                            Text(
                                text = "No summary data available.",
                                fontSize = 14.sp,
                                fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                                color = Color.Gray
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .clip(RoundedCornerShape(26.dp))
                        .background(
                            brush = Brush.linearGradient(AppGradientColors)
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            saveDescriptionToFile(
                                context = context,
                                description = formattedText
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Download",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily(Font(R.font.urbanist_semibold))
                    )
                }
            }
        }
    }
}

@Composable
fun SummarySection(title: String, content: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = title,
            fontSize = 14.sp,
            fontFamily = FontFamily(Font(R.font.urbanist_bold)),
            fontWeight = FontWeight.Bold,
            color = Color(0xFF4338CA)
        )
        Text(
            text = content,
            fontSize = 14.sp,
            fontFamily = FontFamily(Font(R.font.urbanist_regular)),
            color = Color(0xFF374151),
            lineHeight = 20.sp
        )
    }
}

@Composable
fun BulletSection(title: String, items: List<String>) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = title,
            fontSize = 14.sp,
            fontFamily = FontFamily(Font(R.font.urbanist_bold)),
            fontWeight = FontWeight.Bold,
            color = Color(0xFF4338CA)
        )
        items.forEach { item ->
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.padding(start = 4.dp, top = 2.dp)
            ) {
                Text(
                    text = "•",
                    fontSize = 14.sp,
                    color = Color(0xFF4338CA),
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = item,
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                    color = Color(0xFF374151),
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Composable
fun MedicationsSection(meds: List<com.bussiness.curemegptapp.apimodel.chatModel.MedicationSummary>) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = "Medications",
            fontSize = 14.sp,
            fontFamily = FontFamily(Font(R.font.urbanist_bold)),
            fontWeight = FontWeight.Bold,
            color = Color(0xFF4338CA)
        )
        meds.forEach { med ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF9FAFB), RoundedCornerShape(8.dp))
                    .border(1.dp, Color(0xFFF3F4F6), RoundedCornerShape(8.dp))
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = med.name ?: "",
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(R.font.urbanist_bold)),
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111827)
                )
                if (!med.dosage.isNullOrBlank()) {
                    Text(
                        text = "Dosage: ${med.dosage}",
                        fontSize = 13.sp,
                        fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                        color = Color(0xFF4B5563)
                    )
                }
                if (!med.instructions.isNullOrBlank()) {
                    Text(
                        text = "Instructions: ${med.instructions}",
                        fontSize = 13.sp,
                        fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                        color = Color(0xFF4B5563)
                    )
                }
            }
        }
    }
}

fun formatSummaryToText(summaryData: com.bussiness.curemegptapp.apimodel.chatModel.SummaryData): String {
    val builder = StringBuilder()
    if (!summaryData.title.isNullOrBlank()) {
        builder.append("Title: ").append(summaryData.title).append("\n\n")
    }
    if (!summaryData.summary.isNullOrBlank()) {
        builder.append("Summary:\n").append(summaryData.summary).append("\n\n")
    }
    val symptoms = summaryData.symptoms?.filter { !it.isNullOrBlank() }
    if (!symptoms.isNullOrEmpty()) {
        builder.append("Symptoms:\n")
        symptoms.forEach { builder.append("- ").append(it).append("\n") }
        builder.append("\n")
    }
    val recommendations = summaryData.recommendations?.filter { !it.isNullOrBlank() }
    if (!recommendations.isNullOrEmpty()) {
        builder.append("Recommendations:\n")
        recommendations.forEach { builder.append("- ").append(it).append("\n") }
        builder.append("\n")
    }
    val meds = summaryData.medications?.filter { !it?.name.isNullOrBlank() }
    if (!meds.isNullOrEmpty()) {
        builder.append("Medications:\n")
        meds.forEach { med ->
            builder.append("- ").append(med.name)
            if (!med.dosage.isNullOrBlank()) {
                builder.append(" (Dosage: ").append(med.dosage).append(")")
            }
            if (!med.instructions.isNullOrBlank()) {
                builder.append(" (Instructions: ").append(med.instructions).append(")")
            }
            builder.append("\n")
        }
        builder.append("\n")
    }
    val nextSteps = summaryData.next_steps?.filter { !it.isNullOrBlank() }
    if (!nextSteps.isNullOrEmpty()) {
        builder.append("Next Steps:\n")
        nextSteps.forEach { builder.append("- ").append(it).append("\n") }
        builder.append("\n")
    }
    return builder.toString().trim()
}

fun saveDescriptionToFile(
    context: Context,
    description: String
) {
    try {
        val timeStamp = SimpleDateFormat(
            "yyyy-MM-dd_HH-mm-ss",
            Locale.getDefault()
        ).format(Date())
        val fileName = "summary_$timeStamp.txt"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "text/plain")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
            val uri = context.contentResolver.insert(
                MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                contentValues
            )
            uri?.let {
                context.contentResolver.openOutputStream(it)?.use { outputStream ->
                    outputStream.write(description.toByteArray())
                }
                Toast.makeText(context, "Summary downloaded to Downloads", Toast.LENGTH_SHORT).show()
            } ?: run {
                Toast.makeText(context, "Failed to download summary", Toast.LENGTH_SHORT).show()
            }
        } else {
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDir, fileName)
            file.writeBytes(description.toByteArray())
            Toast.makeText(context, "Summary saved to Downloads", Toast.LENGTH_SHORT).show()
        }
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Failed to save file: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
    }
}

@Preview(showBackground = true)
@Composable
fun SummaryDialogPreview() {
    SummaryDialog(
        summaryData = com.bussiness.curemegptapp.apimodel.chatModel.SummaryData(
            title = "Preview Title",
            summary = "Preview Summary Description",
            symptoms = listOf("Symptom 1", "Symptom 2"),
            recommendations = listOf("Recommendation 1"),
            next_steps = listOf("Next Step 1")
        ),
        onDismiss = { }
    )
}

