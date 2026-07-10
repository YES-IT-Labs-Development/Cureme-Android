package com.bussiness.curemegptapp.ui.screen.main.myProfile

//MyProfileScreen

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.bussiness.curemegptapp.R
import com.bussiness.curemegptapp.navigation.AppDestination
import com.bussiness.curemegptapp.ui.component.DocumentItem
import com.bussiness.curemegptapp.ui.sheet.BottomSheetDialog
import com.bussiness.curemegptapp.ui.sheet.BottomSheetDialogProperties
import com.bussiness.curemegptapp.ui.sheet.FilterAppointmentsBottomSheet
import com.bussiness.curemegptapp.ui.sheet.ProfilePhotoBottomSheet
import com.bussiness.curemegptapp.ui.theme.AppGradientColors
import com.bussiness.curemegptapp.ui.viewModel.main.Document
import com.bussiness.curemegptapp.ui.viewModel.main.FamilyMember
import com.bussiness.curemegptapp.ui.viewModel.main.MyProfileViewModel
import com.bussiness.curemegptapp.util.DownloadUtils
import com.bussiness.curemegptapp.util.UriToRequestBody
import com.canhub.cropper.CropImage.CancelledResult.uriContent
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions

@Composable
fun MyProfileScreen(
    navController: NavHostController,
    viewModel: MyProfileViewModel = hiltViewModel()
) {
    androidx.compose.runtime.LaunchedEffect(Unit) {
        viewModel.userProfileDetail()
    }

    val context = LocalContext.current
    var bitmap: Bitmap? by remember { mutableStateOf(null) }
    var showPhotoSheet by remember { mutableStateOf(false) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }


    var selectedProfilePhotoUri by remember { mutableStateOf<Uri?>(null) }

    // Image crop launcher
    val imageCropLauncher = rememberLauncherForActivityResult(
        contract = CropImageContract()
    ) { result ->
        if (result.isSuccessful) {
            result.uriContent?.let { uri ->
                // Process the cropped image
                imageUri = uri

                val maxSize = 2 * 1024 * 1024 // 2MB

                val cursor = context.contentResolver.query(
                    uri,
                    arrayOf(OpenableColumns.SIZE),
                    null,
                    null,
                    null
                )

                var fileSize = 0L

                cursor?.use {
                    if (it.moveToFirst()) {
                        val sizeIndex = it.getColumnIndex(OpenableColumns.SIZE)
                        fileSize = it.getLong(sizeIndex)
                    }
                }

                if (fileSize > maxSize) {
                    Toast.makeText(context, "Image must be less than 2MB", Toast.LENGTH_SHORT).show()
                    return@let
                }

                selectedProfilePhotoUri = uri
                UriToRequestBody.uriToMultipart(context,uri,"profile_image")?.let { requestBody ->
                    viewModel.uploadProfilePhoto(requestBody,onSuccess = {
                        Toast.makeText(context, "Profile photo updated successfully", Toast.LENGTH_SHORT).show()
                    }, onError = { error ->
                        Toast.makeText(context, "Failed to upload photo: $error", Toast.LENGTH_SHORT).show()
                    })
                }

                // Bitmap process करें (अगर जरूरी हो)
                if (Build.VERSION.SDK_INT < 28) {
                    bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                } else {
                    val source = ImageDecoder.createSource(context.contentResolver, uri)
                    bitmap = ImageDecoder.decodeBitmap(source)
                }


            }
        } else {
            // Handle error
            println("ImageCropping error: ${result.error}")
        }
    }


    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permission granted, launch camera
            val cropOptions = CropImageContractOptions(
                null,
                CropImageOptions(imageSourceIncludeGallery = false)
            )
            imageCropLauncher.launch(cropOptions)
            showPhotoSheet = false
        } else {
            // Permission denied
            Toast.makeText(context, "Camera permission is required to take photos", Toast.LENGTH_SHORT).show()
        }
    }

    val familyMember by viewModel.familyMember.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        // Loading State
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // Error State
        errorMessage?.let { message ->
            Text(
                text = message,
                modifier = Modifier.align(Alignment.Center),
                color = Color.Red
            )
        }

        // Success State
        familyMember?.let { member ->
            ProfileContent(
                member = member,
                navController = navController,
                selectedProfilePhotoUri = selectedProfilePhotoUri, // यहाँ हम URI पास कर रहे हैं
                onEditClick = {
                    // Handle edit click
                    navController.navigate(AppDestination.EditProfileScreen)
                },
                onSettingClick = {
                    navController.navigate(AppDestination.SettingsScreen)
                },
                onDownloadClick = { documentUrl ->
                          DownloadUtils.downloadFile(context,documentUrl,"profile_document_${System.currentTimeMillis()}")
                },
                openProfilePhotoPicker = {
                    showPhotoSheet = true
                },
//                onDeletePhoto = {
//                    // Delete photo logic
//                    selectedProfilePhotoUri = null
//                    imageUri = null
//                    bitmap = null
//                    // viewModel.deleteProfilePhoto()
//                }
            )
        } ?: run {
            if (errorMessage == null) {
                Text(
                    text = "Loading profile...",
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }

    // Update the camera click handler in the BottomSheet
    if (showPhotoSheet) {
        BottomSheetDialog(
            onDismissRequest = { showPhotoSheet = false },
            properties = BottomSheetDialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = false,
                dismissWithAnimation = true,
                enableEdgeToEdge = false,
            )
        ) {
            ProfilePhotoBottomSheet(
                onDismiss = { showPhotoSheet = false },
                onCameraClick = {
                    // Check and request permission before launching camera
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED) {
                        // Permission already granted
                        val cropOptions = CropImageContractOptions(
                            null,
                            CropImageOptions(imageSourceIncludeGallery = false)
                        )
                        imageCropLauncher.launch(cropOptions)
                        showPhotoSheet = false
                    } else {
                        // Request permission
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                },
                onGalleryClick = {
                    // Gallery doesn't need camera permission
                    val cropOptions = CropImageContractOptions(
                        null,
                        CropImageOptions(imageSourceIncludeCamera = false)
                    )
                    imageCropLauncher.launch(cropOptions)
                    showPhotoSheet = false
                },
                onDeleteClick = {
                    showPhotoSheet = false
                    viewModel.deleteProfilePhoto(
                        onSuccess = {
                            selectedProfilePhotoUri = null
                            imageUri = null
                            bitmap = null
                            viewModel.userProfileDetail()
                            Toast.makeText(context, "Profile photo deleted successfully", Toast.LENGTH_SHORT).show()
                        },
                        onError = { error ->
                            Toast.makeText(context, "Failed to delete photo: $error", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            )
        }
    }
}

@Composable
fun ProfileContent(
    member: FamilyMember,
    navController: NavHostController,
    selectedProfilePhotoUri: Uri?,
    onEditClick: () -> Unit,
    onSettingClick: () -> Unit,
    onDownloadClick: (String) -> Unit,
    openProfilePhotoPicker: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            // Header with curved background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(
                            brush = Brush.linearGradient(AppGradientColors),
                            RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                        )
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 48.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        IconButton(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier.size(45.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_back_nav_blue_icon),
                                contentDescription = "Back"
                            )
                        }

                        Text(
                            text = stringResource(R.string.my_profile_title),
                            fontSize = 20.sp,
                            fontFamily = FontFamily(Font(R.font.onest_medium)),
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(
                            onClick = onEditClick,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_edit_icon_circle),
                                contentDescription = "Edit"
                            )
                        }

                        IconButton(
                            onClick = onSettingClick,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_setting_icon),
                                contentDescription = "Setting"
                            )
                        }
                    }
                }

                // Profile image
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(y = 40.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(156.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .padding(13.dp)
                    ) {

                        if (selectedProfilePhotoUri != null) {
                            AsyncImage(
                                model = selectedProfilePhotoUri,
                                contentDescription = stringResource(R.string.profile_photo_description, member.name),
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }
                        else if(member.profileImage.isNullOrEmpty() || 
                                 member.profileImage == "https://curemegpt.tgastaging.com/" || 
                                 member.profileImage == "https://curemegpt.tgastaging.com"){
                            Log.d("PROFILE_IMAGE", member.profileImage)
                            Image(
                                    painter = painterResource(id = R.drawable.user_not_found),
                            contentDescription = stringResource(R.string.profile_photo_description, member.name),
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                            )
                        }
                        else {
                            Log.d("PROFILE_IMAGE", member.profileImage)

                            AsyncImage(
                                model = member.profileImage,
                                contentDescription = stringResource(
                                    R.string.profile_photo_description,
                                    member.name
                                ),
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )

                        }
                    }

                    // Upload button
                    IconButton(
                        onClick = { openProfilePhotoPicker() },
                        modifier = Modifier
                            .size(39.dp)
                            .align(Alignment.BottomEnd)
                            .offset(x = (-8).dp, y = (-8).dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_upload_icon),
                            contentDescription = "Upload"
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }

        // Personal Information
        item {
            SectionTitle("Personal Information")
//            InfoCard {
//                InfoRow("Full Name", member.name)
//                InfoRow("Contact Number", member.contactNumber)
//                InfoRow("Email Address", member.email)
//                InfoRow("Relation to You", member.relation)
//                InfoRow("Date of Birth", member.dateOfBirth)
//                InfoRow("Gender", member.gender)
//                InfoRow("Height (cm/ft)", member.height)
//                InfoRow("Weight (kg/lb)", member.weight, isLast = true)
//            }

            PersonalInformationSection(member)
        }

        // General Health
        item {
            SectionTitle("General Health")
            InfoCard {
                InfoRow("Blood Group", member.bloodGroup)
                InfoRow(
                    label = "Known Allergies",
                    value = member.allergies
                        .split(",")
                        .map { it.trim() }
                        .filter { it.isNotEmpty() }
                        .joinToString(", ")
                        .ifEmpty { member.allergies }
                )
                InfoRow("Emergency Contact", member.emergencyContact)
                InfoRow("Emergency Ph.", member.emergencyPhone, isLast = true)
            }
        }

        // Medical History
        item {
            SectionTitle("Medical History")
            InfoCard {
                InfoRow(
                    label = "Chronic Conditions",
                    value = member.chronicConditions
                        .split(",")
                        .map { it.trim() }
                        .filter { it.isNotEmpty() }
                        .joinToString(", ")
                        .ifEmpty { member.chronicConditions }
                )
                InfoRow("Surgical History", member.surgicalHistory)

                Column {
                    InfoRow(
                        "Current Medications",
                        if (member.currentMedications.isNotEmpty()) member.currentMedications[0] else "--"
                    )
                    member.currentMedications.drop(1).forEach { medication ->
                        InfoRow("", medication)
                    }
                }

                Column {
                    InfoRow(
                        "Current Supplements",
                        if (member.currentSupplements.isNotEmpty()) member.currentSupplements[0] else "--"
                    )
                    member.currentSupplements.drop(1).forEach { supplement ->
                        InfoRow("", supplement)
                    }
                }
            }
        }

        // Documents
        item {
            SectionTitle("Documents")
            member.documents.forEach { document ->
                DocumentItem(
                    document = document,
                    onDownloadClick = { onDownloadClick(document.fileUrl) }
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun PersonalInformationSection(member: FamilyMember) {
    var selected by remember { mutableStateOf("name") }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {

        // Row 1
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SelectableInfoBox(
                icon = R.drawable.ic_profile_unselected,   // change as needed
                value = member.name,
                isSelected = selected == "name",
                onClick = { selected = "name" },
                modifier = Modifier.weight(1f)
            )

            SelectableInfoBox(
                icon = R.drawable.ic_dob,
                value = member.dateOfBirth,
                isSelected = selected == "dob",
                onClick = { selected = "dob" },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Row 2
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SelectableInfoBox(
                icon = R.drawable.ic_weight,
                value = com.bussiness.curemegptapp.util.CommonUtils.cleanMeasurement(member.weight, "Kg"),
                isSelected = selected == "weight",
                onClick = { selected = "weight" },
                modifier = Modifier.weight(1f)
            )

            SelectableInfoBox(
                icon = R.drawable.ic_phone,
                value = member.contactNumber,
                isSelected = selected == "phone",
                onClick = { selected = "phone" },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Row 3
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SelectableInfoBox(
                icon = R.drawable.ic_height,
                value = com.bussiness.curemegptapp.util.CommonUtils.cleanMeasurement(member.height, "Cm"),
                isSelected = selected == "height",
                onClick = { selected = "height" },
                modifier = Modifier.weight(1f)
            )

            SelectableInfoBox(
                icon = R.drawable.ic_gender,
                value = member.gender,
                isSelected = selected == "gender",
                onClick = { selected = "gender" },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Full Width Row (Email)
        SelectableInfoBox(
            icon = R.drawable.ic_email,
            value = member.email,
            isSelected = selected == "email",
            onClick = { selected = "email" },
            modifier = Modifier.fillMaxWidth()
        )
    }
}



@Composable
fun SelectableInfoBox(
    icon: Int,
    value: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    val backgroundColor = if (isSelected) Color(0xFF382FAA) else Color(0xFFF9F9FD)
    val borderColor = if (isSelected) Color(0xFF382FAA) else Color(0xFFE7E6F8)
    val iconBg = if (isSelected) Color.White else Color.Unspecified
    val textColor = if (isSelected) Color.White else Color.Black

    Box(
        modifier = modifier
            .height(135.dp)
            .clip(RoundedCornerShape(36.dp))
            .background(backgroundColor)
            .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(36.dp))
            .clickable( interactionSource = remember { MutableInteractionSource() },
                indication = null) { onClick() }
            .padding(15.dp)
    ) {
        // Icon circle
        Box(
            modifier = Modifier
                .size(53.dp)
                .clip(CircleShape)
                .background(iconBg)

        ) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
        }
        // Value
        Text(
            text = value,
            color = textColor,
            fontSize = 17.sp,
            fontFamily = FontFamily(Font(R.font.urbanist_medium)),
            modifier = Modifier
                .align(Alignment.BottomEnd)
        )
    }
}


@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 18.sp,
        fontFamily = FontFamily(Font(R.font.urbanist_medium)),
        fontWeight = FontWeight.Medium,
        color = Color.Black,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
    )
}

@Composable
fun InfoCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .border(width = 1.dp, color = Color(0xFFEAE7FA), shape = RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFCFBFF))
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            content()
        }
    }
}

@Composable
fun InfoRow(
    label: String,
    value: String,
    isLast: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp,
                vertical = 16.dp
            ),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 15.sp,
            color = Color(0xFF4338CA),
            modifier = Modifier.weight(1f),
            fontFamily = FontFamily(Font(R.font.urbanist_medium)),
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            fontSize = 15.sp,
            color = Color(0xFF181818),
            modifier = Modifier.weight(1f),
            fontFamily = FontFamily(Font(R.font.urbanist_regular)),
            fontWeight = FontWeight.Normal
        )
    }
}


@Preview(showBackground = true)
@Composable
fun MyProfileScreenPreview() {
    val navController = rememberNavController()
    MyProfileScreen(navController = navController)
}