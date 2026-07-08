package com.bussiness.curemegptapp.ui.screen.main.editFamilyMemberDetails

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
    val filePickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        uris?.forEach { uri ->
            viewModel.addUploadedFile(uri)
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
                        text =  stringResource(R.string.no_files_uploaded),//"No files uploaded",
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
//
//            Spacer(modifier = Modifier.height(24.dp))
//
//            DisclaimerBox(
//                title = stringResource(R.string.disclaimer_title), //"You're almost ready!",
//                description = stringResource(R.string.disclaimer_description),//"You can always add more details, upload documents, or update your profile later from the settings menu.",
//                titleColor = Color(0xFF4338CA),
//                backColor = Color(0x084338CA)
//            )

            Spacer(modifier = Modifier.height(24.dp))
        }
        GradientButton(
            text = stringResource(R.string.update),//"Get Started",
            onClick = {
                if (profileData.uploadedFiles.isEmpty()) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.upload_atleast_one_file),
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    viewModel.submitProfile()

                    // Toast show karein summary
                    val summary = """
                        Profile Completed!
                        Name: ${profileData.fullName}
                        Contact: ${profileData.contactNumber}
                        Email: ${profileData.email}
                        Files: ${profileData.uploadedFiles.size}
                    """.trimIndent()
                    // Create toast message
                    val toastMessage = context.getString(R.string.profile_completed_toast)
                    Toast.makeText(
                        context,
                        toastMessage,
                        Toast.LENGTH_LONG
                    ).show()
                    onNext()
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))


    }
}