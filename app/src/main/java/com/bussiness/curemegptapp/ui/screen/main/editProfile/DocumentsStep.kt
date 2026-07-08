package com.bussiness.curemegptapp.ui.screen.main.editProfile

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bussiness.curemegptapp.R
import com.bussiness.curemegptapp.data.model.ProfileData
import com.bussiness.curemegptapp.ui.component.DisclaimerBox
import com.bussiness.curemegptapp.ui.component.FileAttachment
import com.bussiness.curemegptapp.ui.component.GradientButton
import com.bussiness.curemegptapp.ui.component.ProfilePhotoPicker
import com.bussiness.curemegptapp.ui.viewModel.auth.ProfileCompletionViewModel
import com.bussiness.curemegptapp.ui.viewModel.main.AddFamilyMemberViewModel
import com.bussiness.curemegptapp.ui.viewModel.main.EditProfileViewModel
import com.bussiness.curemegptapp.util.ValidationUtils.getFileNameWithExtension

@Composable
fun DocumentsStep(
    viewModel: EditProfileViewModel,
    profileData: ProfileData,
    onNext: () -> Unit
) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()


    val profileFormState by viewModel::profileFormState
    val files = profileFormState.uploadedFiles

    // Fetch documents from API once
    LaunchedEffect(Unit) {
        viewModel.getProfileDocuments { error ->
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
        }
    }

    val filePickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        uris.forEach { uri ->
            viewModel.addUploadedFile(uri)
        }
    }

    fun validateFields(): Boolean {
        if (files.isEmpty()) {
            Toast.makeText(context, "Please upload at least one document", Toast.LENGTH_SHORT).show()
            return false
        }

        val invalidFiles = mutableListOf<String>()
        files.forEach { fileUri ->
            val fileName = fileUri.lastPathSegment ?: ""
            val isValidFile = fileName.endsWith(".pdf", ignoreCase = true) ||
                    fileName.endsWith(".jpg", ignoreCase = true) ||
                    fileName.endsWith(".jpeg", ignoreCase = true) ||
                    fileName.endsWith(".png", ignoreCase = true) ||
                    fileName.endsWith(".dcm", ignoreCase = true) ||
                    fileName.endsWith(".dicom", ignoreCase = true)

            if (!isValidFile) invalidFiles.add(fileName.take(20))
        }

        if (invalidFiles.isNotEmpty()) {
            val message = if (invalidFiles.size == 1)
                "Invalid file format: ${invalidFiles[0]}"
            else
                "Invalid file formats: ${invalidFiles.take(3).joinToString(", ")}${if (invalidFiles.size > 3) ", ..." else ""}"
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            return false
        }
        return true
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
                fileName = if (files.isEmpty()) stringResource(R.string.no_file_chosen)
                else "${files.size} ${stringResource(R.string.files_selected)}",
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
                stringResource(R.string.file_formats_supported),
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
                    text = stringResource(R.string.attached_files_title),
                    fontSize = 14.sp,
                    color = Color(0xFF4338CA),
                    fontFamily = FontFamily(Font(R.font.urbanist_semibold)),
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (files.isEmpty()) {
                    Text(
                        text = stringResource(R.string.no_files_uploaded),
                        color = Color.Gray,
                        fontSize = 13.sp
                    )
                } else {
                    files.forEach { fileUri ->
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
            text = stringResource(R.string.update),
            onClick = {
                if (files.isEmpty()) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.upload_atleast_one_file),
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    if (viewModel.profileFormState.uploadedDocument.isEmpty()) {
                        viewModel.completeProfileDocuments(
                            context,
                            onSuccess = {
                                onNext()
                            },
                            onError = { error ->
                                Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                            }
                        )
                    } else {
                        viewModel.updateProfileDocuments(
                            context,
                            onSuccess = {
                                onNext()
                            },
                            onError = { error ->
                                Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                            }
                        )
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))
    }





}