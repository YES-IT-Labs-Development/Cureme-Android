package com.bussiness.curemegptapp.ui.screen.main.addFamilyMemberScreen

import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bussiness.curemegptapp.R
import com.bussiness.curemegptapp.data.model.ProfileData
import com.bussiness.curemegptapp.ui.component.CancelButton
import com.bussiness.curemegptapp.ui.component.ContinueButton
import com.bussiness.curemegptapp.ui.component.DisclaimerBox
import com.bussiness.curemegptapp.ui.component.FileAttachment
import com.bussiness.curemegptapp.ui.component.GradientButton
import com.bussiness.curemegptapp.ui.component.ProfilePhotoPicker
import com.bussiness.curemegptapp.ui.viewModel.auth.ProfileCompletionViewModel
import com.bussiness.curemegptapp.ui.viewModel.main.AddFamilyMemberViewModel
import com.bussiness.curemegptapp.util.ValidationUtils.getFileNameWithExtension

@Composable
fun DocumentsStep(
    viewModel: AddFamilyMemberViewModel,
    profileData: ProfileData,
    isEditMode: Boolean = false,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val filePickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        val maxSize = 10 * 1024 * 1024 // 10MB
        uris.forEach { uri ->
            val cursor = context.contentResolver.query(uri, arrayOf(OpenableColumns.SIZE), null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val sizeIndex = it.getColumnIndex(OpenableColumns.SIZE)
                    val fileSize = it.getLong(sizeIndex)
                    if (fileSize <= maxSize) {
                        viewModel.addUploadedFile(uri)
                    } else {
                        Toast.makeText(
                            context,
                            "File must be less than 10MB",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 10.dp)) {
            ProfilePhotoPicker(
                label = stringResource(R.string.upload_files_label),
                //  label = "Upload Files (X-Rays, Dental Scans, Prescriptions, Lab Reports)",
                fileName = if (profileData.uploadedFiles.isEmpty()) stringResource(R.string.no_file_chosen) //"No file chosen"
                else "${profileData.uploadedFiles.size} ${stringResource(R.string.files_selected)}", //files selected
                buttonBgResId = R.drawable.ic_bg_button_blue,
                onChooseClick = {
                    filePickerLauncher.launch(
                        arrayOf(
                            "image/*",
                            "application/pdf",
                            "application/dicom"
                        )
                    )
                }
            )

            Text(
                stringResource(R.string.file_formats_supported),//"PDF, JPG, PNG, DICOM Supported",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        filePickerLauncher.launch(
                            arrayOf(
                                "image/*",
                                "application/pdf",
                                "application/dicom"
                            )
                        )
                    }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = Color(0xFFE7E6F8),
                        shape = RoundedCornerShape(30.dp)
                    )
                    .background(
                        color = Color(0xFFF9F9FD),
                        shape = RoundedCornerShape(30.dp)
                    )
                    .padding(20.dp)
            ) {
                Text(
                    text = stringResource(R.string.attached_files_title), //"Attached Files",
                    fontSize = 14.sp,
                    color = Color(0xFF4338CA),
                    fontFamily = FontFamily(Font(R.font.urbanist_semibold)),
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (profileData.uploadedFiles.isEmpty()) {
                    Text(
                        text = stringResource(R.string.no_files_uploaded),//"No files uploaded",
                        color = Color.Gray,
                        fontSize = 13.sp
                    )
                } else {
                    profileData.uploadedFiles.forEach { fileUri ->
                        val fileName = getFileNameWithExtension(context, fileUri)
                        FileAttachment(
                            fileName = fileName,
                            onDeleteClick = { viewModel.removeUploadedFile(fileUri) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }


        GradientButton(
            text = "Save Member",
            horizontalPadding = 8.dp,
            onClick = {
                if (profileData.uploadedFiles.isEmpty()) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.upload_atleast_one_file),
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    val hasNewFiles = profileData.uploadedFiles.any { it.scheme == "content" || it.scheme == "file" }
                    if (!hasNewFiles) {
                        onNext()
                    } else {
                        if (!isEditMode) {
                            viewModel.uploadFiles(
                                context,
                                onSuccess = {
                                    onNext()
                                },
                                onError = { msg ->
                                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                                }
                            )
                        } else {
                            viewModel.updateFiles(
                                context, onSuccess = {
                                    onNext()
                                },
                                onError = { msg ->
                                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                                }
                            )
                        }
                    }
                }
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

private fun fakeProfileData() = ProfileData(
    fullName = "Vipin Khatri",
    contactNumber = "9876543210",
    email = "vipin@gmail.com",
    uploadedFiles = listOf(
        Uri.parse("file://xray_report.pdf"),
        Uri.parse("file://blood_test.png")
    )
)
@Preview(showBackground = true)
@Composable
fun DocumentsStepPreview() {
//    DocumentsStep(
//        viewModel = AddFamilyMemberViewModel(),
//        profileData = fakeProfileData(),
//        onNext = {},
//        onBack = {}
//    )
}