package com.bussiness.curemegptapp.ui.viewModel.main

//AddFamilyMemberViewModel


import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bussiness.curemegptapp.data.model.ProfileData
import com.bussiness.curemegptapp.repository.NetworkResult
import com.bussiness.curemegptapp.repository.Repository
import com.bussiness.curemegptapp.ui.component.LoaderOverlay
import com.bussiness.curemegptapp.util.LoaderManager
import com.bussiness.curemegptapp.util.MultipartHelper
import com.bussiness.curemegptapp.util.UriToRequestBody
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

@HiltViewModel
class AddFamilyMemberViewModel @Inject constructor(
   val  repository: Repository
) : ViewModel() {

    val currentStep = mutableStateOf(0)
    private  val TAG = "FamilyProfileVM"
    private val _profileData = MutableStateFlow(ProfileData())
    val profileData = _profileData.asStateFlow()
    val familyMemberId = mutableStateOf(0)


    fun getFamilyMemberProfileData(id:Int){
        Log.d("TESTING_FAMILY","HERE INSIDE THE GET FAMILY MEMBER PROFILE")

        viewModelScope.launch {
            LoaderManager.show()
            if(id < 1){
                LoaderManager.hide()
                return@launch
            }
            Log.d("TESTING_FAMILY","FAMILY MEMBER PROFILE id is "+id)
            familyMemberId.value = id
            Log.d("TESTING_FAMILY","FAMILY MEMBER PROFILE id is more"+familyMemberId.value)
            repository.getFamilyMemberProfile(familyMemberId.value).collectLatest {
                when(it){
                  is NetworkResult.Success -> {
                      LoaderManager.hide()
                      Log.d(TAG, "✅ Success: ${it.data}")
                      val data = it.data
                      if (data != null) {
                          _profileData.value = data
                      }
                  }
                  is NetworkResult.Error ->{
                      LoaderManager.hide()
                      Log.e(TAG, "❌ Error: ${it.message}")
                  }
                  else ->{
                  }
              }
            }
        }
    }


    fun updatePersonalInfo(
        context : Context,
        fullName: String,
        contactNumber: String,
        email: String,
        dateOfBirth: String,
        gender: String,
        height: String,
        weight: String,
        profilePhotoUri: Uri?,
        relationShip :String,
        success :() ->Unit
    ) {

        viewModelScope.launch {
            Log.d("TESTING_IDS","Family Member Id is "+familyMemberId.value)
            val memberBody = familyMemberId.value.toString().toRequestBody()
            val nameBody = fullName.toRequestBody()
            val phoneBody = contactNumber.toRequestBody()
            val emailBody = email.toRequestBody()
            val dobBody = dateOfBirth.toRequestBody()
            val genderBody = gender.toRequestBody()
            val heightBody = height.toRequestBody()
            val weightBody = weight.toRequestBody()
            var profilePart: MultipartBody.Part? = null

            profilePhotoUri?.let {
                profilePart = UriToRequestBody.uriToMultipart(context, profilePhotoUri, "profilePhoto")
            }

            LoaderManager.show()

            repository.updateFamilyPersonalProfile(
                memberBody,
                nameBody = nameBody,
                phoneBody = phoneBody,
                emailBody = emailBody,
                dobBody = dobBody,
                genderBody = genderBody,
                heightBody = heightBody,
                weightBody = weightBody,
                profile_image = profilePart
            ).collect{
                when(it){
                    is NetworkResult.Success ->{
                        LoaderManager.hide()
                        val current = profileData.value.copy(
                            fullName = fullName,
                            contactNumber = contactNumber,
                            email = email,
                            dateOfBirth = dateOfBirth,
                            gender = gender,
                            height = height,
                            weight = weight,
                            profilePhotoUri = profilePhotoUri
                        )
                        _profileData.value= current


                        success()
                    }
                    is NetworkResult.Error ->{
                        LoaderManager.hide()
                        Toast.makeText(context,it.message.toString(), Toast.LENGTH_LONG).show()
                    }
                    else ->{

                    }
                }
            }
        }
    }

    fun addPersonalInfo(
        context : Context,
        fullName: String,
        contactNumber: String,
        email: String,
        dateOfBirth: String,
        gender: String,
        height: String,
        weight: String,
        profilePhotoUri: Uri?,
        relationShip :String,
        success :() ->Unit
    ) {

        viewModelScope.launch {
            val relationBody = relationShip.toRequestBody()
            val nameBody = fullName.toRequestBody()
            val phoneBody = contactNumber.toRequestBody()
            val emailBody = email.toRequestBody()
            val dobBody = dateOfBirth.toRequestBody()
            val genderBody = gender.toRequestBody()
            val heightBody = height.toRequestBody()
            val weightBody = weight.toRequestBody()
            var profilePart: MultipartBody.Part? = null

            profilePhotoUri?.let {
                profilePart = UriToRequestBody.uriToMultipart(context, profilePhotoUri, "profile_photo")
            }

            LoaderManager.show()

            repository.addFamilyMemberPersonal(
                relation = relationBody,
                nameBody = nameBody,
                phoneBody = phoneBody,
                emailBody = emailBody,
                dobBody = dobBody,
                genderBody = genderBody,
                heightBody = heightBody,
                weightBody = weightBody,
                profile_image = profilePart
            ).collect{
               when(it){
                   is NetworkResult.Success ->{
                       LoaderManager.hide()
                       val current = profileData.value.copy(
                           fullName = fullName,
                           contactNumber = contactNumber,
                           email = email,
                           dateOfBirth = dateOfBirth,
                           gender = gender,
                           height = height,
                           weight = weight,
                           profilePhotoUri = profilePhotoUri
                       )
                       _profileData.value= current
                       it.data?.let {
                           familyMemberId.value = it
                       }

                       success()
                   }
                   is NetworkResult.Error ->{
                       LoaderManager.hide()
                       Toast.makeText(context,it.message.toString(), Toast.LENGTH_LONG).show()
                   }
                   else ->{

                   }
               }
           }
        }
    }

    fun updateGeneralInfo(
        bloodGroup: String,
        allergies: List<String>,
        emergencyName: String,
        emergencyPhone: String,
        onError :(msg :String) -> Unit,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            LoaderManager.show()
            Log.d("TESTING_ID", "Family member id is" + familyMemberId.value)
            repository.updateFamilyGeneralProfile(
                               familyMemberId.value,
                               bloodGroup,
                allergies.joinToString(","),
                  emergencyName,
                emergencyPhone
            ).collectLatest {
                when(it){
                    is NetworkResult.Success ->{
                        LoaderManager.hide()
                        val current = profileData.value.copy(
                            bloodGroup = bloodGroup, allergies = allergies, emergencyContactName = emergencyName,
                            emergencyContactPhone = emergencyPhone
                        )
                        _profileData.value = current
                        onSuccess()
                    }
                    is NetworkResult.Error ->{
                        LoaderManager.hide()
                        onError(it.message.toString())
                    }
                    else ->{
                    }
                }
            }
        }
    }

    fun addGeneralInfo(
        bloodGroup: String,
        allergies: List<String>,
        emergencyName: String,
        emergencyPhone: String,
        onError :(msg :String) -> Unit,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            LoaderManager.show()
            Log.d("TESTING_ID", "Family member id is" + familyMemberId.value)
            repository.addFamilyMemberGeneral(
                familyMemberId.value,
                bloodGroup,
                allergies.joinToString(","),
                emergencyName,
                emergencyPhone
            ).collectLatest {
                when(it){
                    is NetworkResult.Success ->{
                        LoaderManager.hide()
                        val current = profileData.value.copy(
                            bloodGroup = bloodGroup, allergies = allergies, emergencyContactName = emergencyName,
                            emergencyContactPhone = emergencyPhone
                        )
                        _profileData.value = current
                        onSuccess()
                    }
                    is NetworkResult.Error ->{
                        LoaderManager.hide()
                        onError(it.message.toString())
                    }
                    else ->{
                    }
                }
            }
        }
    }

    fun updateMedicalHistory(
        chronicConditions: List<String>,
        surgicalHistory: String,
        currentMedications: List<String>,
        currentSupplements: List<String>,
        onSuccess: () -> Unit,
        onError: (msg :String) ->Unit
    ) {
        viewModelScope.launch {
            LoaderManager.show()
            repository.updateFamilyHistoryProfile(
                familyMemberId.value,
                chronicConditions.joinToString(","),
                surgicalHistory,
                currentMedications.joinToString(","),
                currentSupplements.joinToString(",")
            ).collectLatest {
               when(it){
                   is NetworkResult.Success ->{
                       LoaderManager.hide()
                       val current = profileData.value.copy(
                           chronicConditions = chronicConditions,
                           surgicalHistory = surgicalHistory,
                           currentMedications = currentMedications,
                           currentSupplements = currentSupplements
                       )
                       _profileData.value = current
                       onSuccess()
                   }
                   is NetworkResult.Error ->{
                       LoaderManager.hide()
                       onError(it.message.toString())
                   }
                   else ->{
                       LoaderManager.hide()
                   }
               }
            }
        }
    }

    fun addMedicalHistory(
        chronicConditions: List<String>,
        surgicalHistory: String,
        currentMedications: List<String>,
        currentSupplements: List<String>,
        onSuccess: () -> Unit,
        onError: (msg:String) ->Unit
    ) {
        viewModelScope.launch {
            LoaderManager.show()
            repository.addFamilyMemberHistory(
                familyMemberId.value,
                chronicConditions.joinToString(","),
                surgicalHistory,
                currentMedications.joinToString(","),
                currentSupplements.joinToString(",")
            ).collectLatest {
                when(it){
                    is NetworkResult.Success ->{
                        LoaderManager.hide()
                        val current = profileData.value.copy(
                            chronicConditions = chronicConditions,
                            surgicalHistory = surgicalHistory,
                            currentMedications = currentMedications,
                            currentSupplements = currentSupplements
                        )
                        _profileData.value = current
                         onSuccess()
                    }
                    is NetworkResult.Error ->{
                        LoaderManager.hide()
                        onError(it.message.toString())
                    }
                    else ->{
                        LoaderManager.hide()
                    }
                }
            }
        }
    }




    fun addUploadedFile(uri: Uri) {
        val currentFiles = profileData.value.uploadedFiles.toMutableList()
        currentFiles.add(uri)
        _profileData.value = profileData.value.copy(uploadedFiles = currentFiles)
    }

    fun uploadFiles(context:Context,
                    onSuccess:()->Unit,
                    onError: (msg:String)->Unit
    ){
        viewModelScope.launch(Dispatchers.IO) {
            val parts = mutableListOf<MultipartBody.Part>()
            profileData.value.uploadedFiles.forEach { item ->
                when (item.scheme) {
                    "http", "https" -> {
                        val part = MultipartHelper.preparePart(context,"medical_documents[]",item)
                        part?.let { parts.add(it) }
                    }
                    "content", "file" -> {
                        val part = UriToRequestBody.uriToMultipart(context,item,"medical_documents[]")
                        part.let { parts.add(it) }
                    }
                    else -> {

                    }
                }
            }

            val familyIdBody = familyMemberId.value.toString().
            toRequestBody("text/plain".toMediaTypeOrNull())

            LoaderManager.show()

            repository.addFamilyMemberMedicalDocuments(
                familyIdBody,
                parts.toMutableList()
            ).collect {
                when(it){
                    is NetworkResult.Success ->{
                        LoaderManager.hide()
                        withContext(Dispatchers.Main) {
                            onSuccess()
                        }
                    }
                    is NetworkResult.Error ->{
                        LoaderManager.hide()
                        withContext(Dispatchers.Main) {
                            onError(it.message.toString())
                        }
                    }
                    else ->{

                    }
                }

            }
        }
    }

    fun updateFiles(
        context:Context,
        onSuccess:()->Unit,
        onError: (msg:String)->Unit
    ){
        viewModelScope.launch(Dispatchers.IO) {
            val parts = mutableListOf<MultipartBody.Part>()
            profileData.value.uploadedFiles.forEach { item ->
                when (item.scheme) {
                    "http", "https" -> {
                        val part = MultipartHelper.preparePart(context,"medical_documents[]",item)
                        part?.let { parts.add(it) }
                    }
                    "content", "file" -> {
                        val part = UriToRequestBody.uriToMultipart(context,item,"medical_documents[]")
                        part.let { parts.add(it) }
                    }
                    else -> {

                    }
                }
            }

            val familyIdBody = familyMemberId.value.toString().
            toRequestBody("text/plain".toMediaTypeOrNull())

            LoaderManager.show()

            repository.updateFamilyMedicalDocuments(
                familyIdBody,
                parts.toMutableList()
            ).collect {
                when(it){
                    is NetworkResult.Success ->{
                        LoaderManager.hide()
                        withContext(Dispatchers.Main) {
                            onSuccess()
                        }
                    }
                    is NetworkResult.Error ->{
                        LoaderManager.hide()
                        withContext(Dispatchers.Main) {
                            onError(it.message.toString())
                        }
                    }
                    else ->{

                    }
                }

            }
        }
    }



    fun removeUploadedFile(uri: Uri) {
        val currentFiles = profileData.value.uploadedFiles.toMutableList()
        currentFiles.remove(uri)
        _profileData.value = profileData.value.copy(uploadedFiles = currentFiles)
    }

    fun clearUploadedFiles() {
        _profileData.value = profileData.value.copy(uploadedFiles = emptyList())
    }

    fun goToNextStep() {
        currentStep.value++
    }

    fun goToPreviousStep() {
        if (currentStep.value > 0) {
            currentStep.value--
        }
    }

    fun submitProfile() {
        // Sab data log karo
        Log.d("PROFILE_DATA", "========== PROFILE COMPLETION DATA ==========")

        // Personal Info
        Log.d("PROFILE_DATA", "PERSONAL INFORMATION:")
        Log.d("PROFILE_DATA", "Full Name: ${profileData.value.fullName}")
        Log.d("PROFILE_DATA", "Contact Number: ${profileData.value.contactNumber}")
        Log.d("PROFILE_DATA", "Email: ${profileData.value.email}")
        Log.d("PROFILE_DATA", "Date of Birth: ${profileData.value.dateOfBirth}")
        Log.d("PROFILE_DATA", "Gender: ${profileData.value.gender}")
        Log.d("PROFILE_DATA", "Height: ${profileData.value.height}")
        Log.d("PROFILE_DATA", "Weight: ${profileData.value.weight}")
        Log.d("PROFILE_DATA", "Profile Photo URI: ${profileData.value.profilePhotoUri}")

        // General Info
        Log.d("PROFILE_DATA", "\nGENERAL INFORMATION:")
        Log.d("PROFILE_DATA", "Blood Group: ${profileData.value.bloodGroup}")
        Log.d("PROFILE_DATA", "Allergies: ${profileData.value.allergies}")
        Log.d("PROFILE_DATA", "Emergency Contact Name: ${profileData.value.emergencyContactName}")
        Log.d("PROFILE_DATA", "Emergency Contact Phone: ${profileData.value.emergencyContactPhone}")

        // Medical History
        Log.d("PROFILE_DATA", "\nMEDICAL HISTORY:")
        Log.d("PROFILE_DATA", "Chronic Conditions: ${profileData.value.chronicConditions}")
        Log.d("PROFILE_DATA", "Surgical History: ${profileData.value.surgicalHistory}")
        Log.d("PROFILE_DATA", "Current Medications: ${profileData.value.currentMedications}")
        Log.d("PROFILE_DATA", "Current Supplements: ${profileData.value.currentSupplements}")

        // Documents
        Log.d("PROFILE_DATA", "\nDOCUMENTS:")
        Log.d("PROFILE_DATA", "Uploaded Files Count: ${profileData.value.uploadedFiles.size}")
        profileData.value.uploadedFiles.forEachIndexed { index, uri ->
            Log.d("PROFILE_DATA", "File ${index + 1}: ${uri.lastPathSegment}")
        }

        Log.d("PROFILE_DATA", "========== END OF PROFILE DATA ==========\n")

        // Yahan aap local storage ya navigation kar sakte hain
        // For example, navigate to home screen
        // navController.navigate("home")
    }
}