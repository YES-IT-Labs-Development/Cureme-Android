package com.bussiness.curemegptapp.ui.screen.main.familyPersonProfile


import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.OpenableColumns
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
import androidx.compose.runtime.LaunchedEffect
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
import com.bussiness.curemegptapp.ui.dialog.AlertCardDialog
import com.bussiness.curemegptapp.ui.sheet.BottomSheetDialog
import com.bussiness.curemegptapp.ui.sheet.BottomSheetDialogProperties
import com.bussiness.curemegptapp.ui.sheet.ProfilePhotoBottomSheet
import com.bussiness.curemegptapp.ui.theme.AppGradientColors
import com.bussiness.curemegptapp.ui.viewModel.main.DownloadState
import com.bussiness.curemegptapp.ui.viewModel.main.Document
import com.bussiness.curemegptapp.ui.viewModel.main.FamilyMember
import com.bussiness.curemegptapp.ui.viewModel.main.FamilyProfileViewModel
import com.bussiness.curemegptapp.util.UriToRequestBody
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions

@Composable
fun FamilyPersonProfileScreen(
    navController: NavHostController,
    id: Int? =0,
    viewModel: FamilyProfileViewModel = hiltViewModel()
) {

    val context = LocalContext.current

    LaunchedEffect(id) {
        id?.let {
            viewModel.loadFamilyMember(it)
        }
    }

    var bitmap: Bitmap? by remember { mutableStateOf(null) }
    var showPhotoSheet by remember { mutableStateOf(false) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // यह state profile photo के लिए है
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

                // Process bitmap (if required)
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

    // Permission launcher for camera
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {

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
    var showDeleteDialog by remember { mutableStateOf(false) }



    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Loading State
        if (isLoading) {
//            CircularProgressIndicator(
//                modifier = Modifier.align(Alignment.Center)
//            )
        }

        // Error State
        errorMessage?.let { message ->
            Text(
                text = message,
                modifier = Modifier.align(Alignment.Center),
                color = Color.Red
            )
        }

        val downloadStates by viewModel.downloadStates.collectAsState()

        // Toast error message if a download fails
        LaunchedEffect(downloadStates) {
            downloadStates.forEach { (_, state) ->
                if (state is DownloadState.Error) {
                    Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Success State
        familyMember?.let { member ->
            FamilyMemberProfileContent(
                member = member,
                navController = navController,
                selectedProfilePhotoUri = selectedProfilePhotoUri,
                downloadStates = downloadStates,
                onEditClick = {
                    // Handle edit click
                },
                onDeleteClick = {
                    showDeleteDialog = true
                },
                onDownloadClick = { document ->
                    viewModel.downloadDocument(context, document)
                },
                openProfilePhotoPicker = {
                    showPhotoSheet = true
                }
            )
            /*
            FamilyMemberProfileContent(
    member = member,
    navController = navController,
    selectedProfilePhotoUri = selectedProfilePhotoUri,
    onEditClick = { },
    onDeleteClick = { showDeleteDialog = true },
    onDownloadClick = { },
    openProfilePhotoPicker = { openProfilePhotoPicker() }
)
             */
        } ?: run {
            if (!isLoading && errorMessage == null) {
                Text(
                    text = stringResource(R.string.no_family_member_found),
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }

    if (showDeleteDialog) {
        AlertCardDialog(
            icon = R.drawable.ic_delete_icon_new,
            title = stringResource(R.string.delete_member_dialog_title),
            message = stringResource(R.string.delete_member_dialog_message, familyMember?.name ?: stringResource(R.string.peter_logan_son_user).split(" (")[0]),
            confirmText = stringResource(R.string.delete_button),
            cancelText = stringResource(R.string.cancel_button),
            onDismiss = { showDeleteDialog = false },
            onConfirm = {
                showDeleteDialog = false
              //  viewModel.deleteFamilyMember()
                id?.let {
                    viewModel.deleteProfile(id, {
                        navController.popBackStack()
                        }, { msg ->
                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                    })
                }
            }
        )
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
                    id?.let { memberId ->
                        viewModel.deleteFamilyProfilePhoto(
                            id = memberId,
                            onSuccess = { msg ->
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                selectedProfilePhotoUri = null
                                imageUri = null
                                bitmap = null
                            },
                            onError = { msg ->
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            }
                        )
                    } ?: run {
                        selectedProfilePhotoUri = null
                        imageUri = null
                        bitmap = null
                    }
                }
            )
        }
    }
}

@Composable
fun FamilyMemberProfileContent(
    member: FamilyMember,
    navController: NavHostController,
    selectedProfilePhotoUri: Uri?,
    downloadStates: Map<String, DownloadState>,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onDownloadClick: (Document) -> Unit,
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
                // Purple background
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(
                            brush = Brush.linearGradient(AppGradientColors),
                            RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                        )
                )

                // Top bar
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
                                contentDescription = stringResource(R.string.back_button_description)
                            )
                        }

                        Text(
                            text = member.name,
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
                                contentDescription = stringResource(R.string.edit_profile_description),
                                modifier = Modifier.clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                 //   navController.navigate(AppDestination.EditFamilyMemberDetailsScreen)
                                    navController.navigate("addFamilyMember?from=${member.id}")

                                }
                            )
                        }

                        IconButton(
                            onClick = onDeleteClick,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_delete_icon_circle),
                                contentDescription = stringResource(R.string.delete_profile_description)
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
                    ) {
                        if (selectedProfilePhotoUri != null) {
                            AsyncImage(
                                model = selectedProfilePhotoUri,
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
                        else if (!member.profileImage.isNullOrEmpty() && 
                                 member.profileImage != "https://curemegpt.tgastaging.com/" && 
                                 member.profileImage != "https://curemegpt.tgastaging.com") {
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
                        else {
                            Image(
                                painter = painterResource(id = R.drawable.ic_user_profile_family),
                                contentDescription = stringResource(R.string.profile_photo_description, member.name),
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(13.dp)
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
                            contentDescription = stringResource(R.string.upload_photo_description)
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
            SectionTitle(stringResource(R.string.personal_information_title))
            InfoCard {
                InfoRow(stringResource(R.string.full_name_label), member.name)
                InfoRow(stringResource(R.string.contact_number_label), member.contactNumber)
                InfoRow(stringResource(R.string.email_label), member.email)
                InfoRow(stringResource(R.string.relation_to_you_label), member.relation)
                InfoRow(stringResource(R.string.date_of_birth_label), member.dateOfBirth)
                InfoRow(stringResource(R.string.gender_label), member.gender)
                InfoRow(stringResource(R.string.height_label), com.bussiness.curemegptapp.util.CommonUtils.cleanMeasurement(member.height, "Cm"))
                InfoRow(stringResource(R.string.weight_label), com.bussiness.curemegptapp.util.CommonUtils.cleanMeasurement(member.weight, "Kg"), isLast = true)
            }
        }

        // General Health
        item {
            SectionTitle(stringResource(R.string.general_health_title))
            InfoCard {
                InfoRow(stringResource(R.string.blood_group_label), member.bloodGroup)
                InfoRow(
                    label = stringResource(R.string.known_allergies_label),
                    value = member.allergies
                        .split(",")
                        .map { it.trim() }
                        .filter { it.isNotEmpty() }
                        .joinToString(", ")
                        .ifEmpty { member.allergies }
                )
                InfoRow(stringResource(R.string.emergency_contact_label), member.emergencyContact)
                InfoRow(stringResource(R.string.emergency_phone_label1), member.emergencyPhone, isLast = true)
            }
        }

        // Medical History
        item {
            SectionTitle(stringResource(R.string.medical_history_title))
            InfoCard {
                InfoRow(
                    label = stringResource(R.string.chronic_conditions_label),
                    value = member.chronicConditions
                        .split(",")
                        .map { it.trim() }
                        .filter { it.isNotEmpty() }
                        .joinToString(", ")
                        .ifEmpty { member.chronicConditions }
                )
                InfoRow(stringResource(R.string.surgical_history_label1), member.surgicalHistory)

                Column {
                    InfoRow(
                        stringResource(R.string.current_medications_label1),
                        if (member.currentMedications.isNotEmpty()) member.currentMedications[0]
                        else stringResource(R.string.not_available_placeholder)
                    )
                    member.currentMedications.drop(1).forEach { medication ->
                        InfoRow("", medication)
                    }
                }

                Column {
                    InfoRow(
                        stringResource(R.string.current_supplements_label1),
                        if (member.currentSupplements.isNotEmpty()) member.currentSupplements[0]
                        else stringResource(R.string.not_available_placeholder)
                    )
                    member.currentSupplements.drop(1).forEach { supplement ->
                        InfoRow("", supplement)
                    }
                }
            }
        }

        // Documents
        item {
            SectionTitle(stringResource(R.string.documents))
            member.documents.forEach { document ->
                val state = downloadStates[document.id] ?: DownloadState.Idle
                DocumentItem(
                    document = document,
                    downloadState = state,
                    onDownloadClick = { onDownloadClick(document) }
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun DocumentItem(
    document: Document,
    downloadState: DownloadState,
    onDownloadClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFD9D7F4))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 5.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_files_icon),
                    contentDescription = stringResource(R.string.file_icon_description),
                    modifier = Modifier.size(41.dp, 55.dp)
                )
                Text(
                    text = document.fileName,
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF4338CA)
                )
            }

            IconButton(
                onClick = onDownloadClick,
                modifier = Modifier.size(48.dp),
                enabled = downloadState !is DownloadState.Downloading
            ) {
                if (downloadState is DownloadState.Downloading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color(0xFF4338CA),
                        strokeWidth = 2.dp
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.ic_download_icon),
                        contentDescription = stringResource(R.string.download_file_description)
                    )
                }
            }
        }
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
            fontWeight = FontWeight.Normal,
            textAlign = androidx.compose.ui.text.style.TextAlign.End
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FamilyPersonProfileScreenPreview() {
    val navController = rememberNavController()
    FamilyPersonProfileScreen(navController = navController)
}

