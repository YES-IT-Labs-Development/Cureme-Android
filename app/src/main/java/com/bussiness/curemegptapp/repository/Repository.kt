package com.bussiness.curemegptapp.repository

import com.bussiness.curemegptapp.apimodel.OnBoardingModel.OnboardingItem
import com.bussiness.curemegptapp.apimodel.OnBoardingModel.OnboardingResponse
import com.bussiness.curemegptapp.apimodel.QuestionAnswer
import com.bussiness.curemegptapp.apimodel.alertmodel.AlertDateModel
import com.bussiness.curemegptapp.apimodel.chatModel.ChatHistoryItem
import com.bussiness.curemegptapp.apimodel.chatModel.PromptQuestionResponse
import com.bussiness.curemegptapp.apimodel.familyProfile.FamilyMemberResponse
import com.bussiness.curemegptapp.apimodel.getAppointmentList.AppointmentData
import com.bussiness.curemegptapp.apimodel.getAppointmentList.AppointmentItem
import com.bussiness.curemegptapp.apimodel.getAppointmentList.AppointmentListResponse
import com.bussiness.curemegptapp.apimodel.homemodel.HomeDataModel
import com.bussiness.curemegptapp.apimodel.loginmodel.LoginResponse
import com.bussiness.curemegptapp.apimodel.medication.MedicationItem
import com.bussiness.curemegptapp.apimodel.medication.MedicationItemDetail
import com.bussiness.curemegptapp.apimodel.personalmodel.PersonalModel
import com.bussiness.curemegptapp.apimodel.personalmodel.ProfileResponse
import com.bussiness.curemegptapp.apimodel.personalmodel.User1
import com.bussiness.curemegptapp.apimodel.profilemodel.Data.UserProfile
import com.bussiness.curemegptapp.apimodel.profilemodel.DeleteMedicalDocRequest
import com.bussiness.curemegptapp.apimodel.profilemodel.UserProfileResponse
import com.bussiness.curemegptapp.apimodel.reportdetailmodel.ReportDetailsModel
import com.bussiness.curemegptapp.apimodel.reportmodel.ReportModel
import com.bussiness.curemegptapp.apimodel.scheduleAppointment.AppointmentTypeModel
import com.bussiness.curemegptapp.apimodel.scheduleAppointment.FamilyModel
import com.bussiness.curemegptapp.data.model.ChatMessage
import com.bussiness.curemegptapp.data.model.ProfileData
import com.bussiness.curemegptapp.ui.viewModel.main.FamilyMember
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.Part

interface Repository {

    fun loginRequest(emailOrPhone: String, password: String, fcmToken: String) : Flow<Resource<LoginResponse>>
    fun registerRequest(name: String, emailOrPhone: String, password: String, deviceType: String, fcmToken: String): Flow<Resource<LoginResponse>>
    fun updatePasswordRequest(emailOrPhone: String, password: String): Flow<Resource<LoginResponse>>
    fun forgotOtpRequest(emailOrPhone: String): Flow<Resource<LoginResponse>>
    fun otpVerifyRequest(emailOrPhone: String,otp: String,fcmToken: String,fromScreen: String): Flow<Resource<LoginResponse>>
    fun sendOtpRequest(emailOrPhone: String): Flow<Resource<LoginResponse>>

    fun profileRequest(): Flow<Resource<PersonalModel>>
    fun reportListRequest(): Flow<Resource<ReportModel>>
    fun getHomeRequest(): Flow<Resource<HomeDataModel>>

    fun getAlertListRequest(): Flow<Resource<AlertDateModel>>

    fun updateAlertReadStatus(id: String): Flow<Resource<AlertDateModel>>

    fun getReportDetailsRequest(id: String): Flow<Resource<ReportDetailsModel>>

    fun updatePersonalRequest(
        name: String, phone: String, email: String, dob: String,
        gender: String, height: String, heightType: String,
        weight: String, weightType: String,imageProfile: MultipartBody.Part?
    ): Flow<Resource<PersonalModel>>

    fun generalProfileRequest(
        bloodGroup: String,
        allergies:String,
        emergencyContactName: String,
        emergencyContactPhone: String
    ): Flow<Resource<ProfileResponse>>

     fun verifyEmailPhoneRequest(
        emailOrPhone: String
    ) : Flow<NetworkResult<String>>

    suspend fun completeGeneralProfileHistoryRequest(
        @Field("chronic_condition[]") chronicConditions: String,
        @Field("surgical_history") surgicalHistory: String,
        @Field("current_medications[]") currentMedication: String,
        @Field("current_supplements[]") currentSupplement : String
    ) : Flow<Resource<ProfileResponse>>


    suspend fun completeProfileDocumentsRequest(
        @Part files: List<MultipartBody.Part>
    ): Flow<Resource<ProfileResponse>>


    suspend fun onBoardingData() : Flow<Resource<OnboardingResponse>>

    suspend fun getUserDetails() : Flow<NetworkResult<UserProfile>>

    fun updateProfilePicture(
        @Part profile_image: MultipartBody.Part
    ) : Flow<NetworkResult<String>>

    fun getFAQ(): Flow<NetworkResult<List<QuestionAnswer>>>

    fun getPrivacyPolicy() : Flow<NetworkResult<String>>

    fun getTermsAndConditions() : Flow<NetworkResult<String>>
    fun getAccountPrivacyPolicy() : Flow<NetworkResult<String>>

    fun helpSupport() : Flow<NetworkResult<String>>

    fun aboutUs() : Flow<NetworkResult<String>>

    fun getAppointmentType() : Flow<NetworkResult<List<AppointmentTypeModel>>>

    fun getFamilyMembersList() : Flow<NetworkResult<List<FamilyModel>>>

     fun addScheduleAppointment(
        @Field("for_whom_id") forWhomeId :String?,
        @Field("appointment_type_id") appointmentTypeId :String,
        @Field("description") description :String,
        @Field("date") date :String,
        @Field("time") time :String,
        @Field("preferred_doctor") preferredDoctor:String,
        @Field("preferred_clinic") preferredClinic :String,
        @Field("appointment_reminder") reminder :String,
        recommendedChatId:String?

     ) : Flow<NetworkResult<String>>

    fun  getPersonalProfile() : Flow<NetworkResult<User1>>

    fun getAllStaticPages(): kotlinx.coroutines.flow.Flow<NetworkResult<List<com.bussiness.curemegptapp.apimodel.StaticPage>>>

    fun getParticularStaticPage(slug: String): kotlinx.coroutines.flow.Flow<NetworkResult<com.bussiness.curemegptapp.apimodel.StaticPage>>

    fun updatePersonalProfile(
        name: String, phone: String, email: String, dob: String, gender: String,
        height: String, weight: String,
        profileImage: MultipartBody.Part?
    ): Flow<NetworkResult<String>>

    fun  getAppointmentList() : Flow<NetworkResult<List<AppointmentItem>>>

    fun getGeneralProfile() : Flow<NetworkResult<User1>>

    fun updateGeneralProfile(
        bloodGroup: String,
        allergies: String,
         contactName: String,
         contactNumber : String,
    ) : Flow<NetworkResult<String>>

    fun getGeneralProfileHistory() : Flow<NetworkResult<User1>>

    suspend fun updateGeneralProfileHistory(
         chronicConditions: String,
         surgicalHistory: String,
         currentMedication: String,
         currentSupplement : String
    ) : Flow<NetworkResult<String>>

    fun getProfileDocuments() : Flow<NetworkResult<List<String>>>

    fun completeProfileDocuments(
        files: List<MultipartBody.Part>
    ) : Flow<NetworkResult<String>>

    fun updateProfileDocuments(
        files :List<MultipartBody.Part>
    ) : Flow<NetworkResult<String>>


    fun getScheduleAppointmentDetails(
         appointmentId :String
    ) : Flow<NetworkResult<AppointmentData>>


    fun rescheduleAppointment(
        @Field("appointment_id") appointmentId:Int,
        @Field("date") date :String,
        @Field("time") time :String,
        @Field("appointment_reminder") reminder :String
    ) : Flow<NetworkResult<String>>


    fun markAppointmentComplete(
        @Field("appointment_id") id:Int
    ) : Flow<NetworkResult<String>>

    fun deleteAppointment(@Field("appointment_id") id:Int) : Flow<NetworkResult<String>>

    fun addMedication(
        forWhomId: RequestBody?,
        medicationType: RequestBody,
        medicationName: RequestBody,
        dosage: RequestBody,
        frequency: RequestBody,
        days: RequestBody?,
        startDate: RequestBody,
        endDate: RequestBody,
        notes: RequestBody,
        reminderStatus: RequestBody,
        reminderTimes: List<MultipartBody.Part>,
        prescriptionDocs: MultipartBody.Part?
    ): Flow<NetworkResult<String>>

    fun getMedicationList() :  Flow<NetworkResult<MutableList<MedicationItem>>>

    fun getMedicationDetails(
        medicationId :Int
    ) : Flow<NetworkResult<MedicationItemDetail>>


    fun deleteMedication(
        @Field("medication_id") medicationId :Int
    ) : Flow<NetworkResult<String>>

    fun updateMedication(
        @Part("medication_id") medicationId : RequestBody?,
        @Part("for_whom_id") forWhomId: RequestBody?,
        @Part("medication_type") medicationType: RequestBody,
        @Part("medication_name") medicationName: RequestBody,
        @Part("dosage") dosage: RequestBody,
        @Part("frequency") frequency: RequestBody,
        @Part("days") days: RequestBody?,
        @Part("start_date") startDate: RequestBody,
        @Part("end_date") endDate: RequestBody,
        @Part("notes") notes: RequestBody,
        @Part("reminder_status") reminderStatus: RequestBody,
        @Part reminderTimes: List<MultipartBody.Part>,
        @Part prescriptionDocs: MultipartBody.Part?
    ) : Flow<NetworkResult<String>>

     fun addFamilyMemberPersonal(
        @Part("relation") relation : RequestBody,
        @Part("full_name") nameBody: RequestBody,
        @Part("contact_number") phoneBody: RequestBody,
        @Part("email_address") emailBody: RequestBody,
        @Part("dob") dobBody: RequestBody,
        @Part("gender") genderBody: RequestBody,
        @Part("height") heightBody: RequestBody,
        @Part("weight") weightBody: RequestBody,
        @Part profile_image:  MultipartBody.Part?
    ) :Flow<NetworkResult<Int>>

    fun addFamilyMemberGeneral(
               familyMemberId :Int,
               bloodGroup: String,
               knownAllergies :String,
               contactName :String,
               contactNumber :String
    ) : Flow<NetworkResult<String>>

    fun addFamilyMemberHistory(
          familyMemberId:Int,
          chronicCondition : String,
          surgicalHistory :String,
          currentMedication: String,
          currentSupplements : String
    ) : Flow<NetworkResult<String>>

    fun addFamilyMemberMedicalDocuments(
        familyMemeberId : RequestBody,
        @Part  profile_image:  MutableList<MultipartBody.Part?>
    ) : Flow<NetworkResult<String>>

    fun getFamilyMemberProfile(
        id :Int
    ) : Flow<NetworkResult<ProfileData>>

    fun updateFamilyPersonalProfile(
        @Part("family_member_id") familyMemberBody : RequestBody,
        @Part("fullName") nameBody: RequestBody,
        @Part("contactNumber") phoneBody: RequestBody,
        @Part("emailAddress") emailBody: RequestBody,
        @Part("dateOfBirth") dobBody: RequestBody,
        @Part("gender") genderBody: RequestBody,
        @Part("height") heightBody: RequestBody,
        @Part("weight") weightBody: RequestBody,
        @Part profile_image:  MultipartBody.Part?
    ) : Flow<NetworkResult<String>>

    fun updateFamilyGeneralProfile(
          familyMemberId :Int,
          bloodGroup: String,
          knownAllergies :String,
          contactName :String,
          contactNumber :String
    ) : Flow<NetworkResult<String>>

    fun updateFamilyHistoryProfile(
         familyMemberId :Int,
         chronicCondition :String,
         surgicalHistory :String,
         currentMediation :String,
         supplement :String
    ) : Flow<NetworkResult<String>>

    fun updateFamilyMedicalDocuments(
         id : RequestBody,
         profile_image:  MutableList<MultipartBody.Part?>
    )  : Flow<NetworkResult<String>>

    suspend fun familyMemberList() : Flow<NetworkResult<FamilyMemberResponse>>
     suspend fun getFamilyMemberProfileDetails(
         familyMemberId :Int
    ) : Flow<NetworkResult<FamilyMember>>

    suspend  fun deleteFamilyMember(
        familyMemberId :Int
    ) : Flow<NetworkResult<String>>

    suspend fun deleteFamilyProfilePhoto(
        familyMemberId: Int
    ): Flow<NetworkResult<String>>

    suspend fun updateFamilyProfilePhoto(
        familyMemberId: RequestBody,
        profileImage: MultipartBody.Part
    ): Flow<NetworkResult<String>>

    suspend fun getUserWithFamilyDetails() :  Flow<NetworkResult<MutableList<FamilyModel>>>

     suspend fun getChatResponse(
         familyMemberId: RequestBody?,
         message: RequestBody,
         type: RequestBody,
         chatId: RequestBody?,
         profile_image: MultipartBody.Part?
    ) : Flow<NetworkResult<ChatMessage>>

    suspend fun getPromptQuestions() : Flow<NetworkResult<PromptQuestionResponse>>

    suspend fun getUserChatHistoryList() : Flow<NetworkResult<MutableList<ChatHistoryItem>>>
    suspend fun renameChat(
        @Field("chat_id") chatId :Int,
        @Field("title") title :String
    ) : Flow<NetworkResult<String>>

    suspend fun deleteChat(
        @Field("chat_id") chatId :Int,
    ) : Flow<NetworkResult<String>>

    suspend fun getUserFamilyMemberChatList(id:Int) :  Flow<NetworkResult<MutableList<ChatHistoryItem>>>

    suspend fun getChatMessage(id:Int): Flow<NetworkResult<MutableList<ChatMessage>>>

    suspend fun downloadHealthReportPdf(
        @Field("chat_id") familyMemberId :Int
    ) : Flow<NetworkResult<String>>


    suspend fun switchChat(
        @Field("chat_id") chatId :Int
    ) : Flow<NetworkResult<String>>

    fun deleteProfilePhoto(): Flow<NetworkResult<String>>

    fun responseLikeDislike(
        chatMessageId: String,
        likeDislikeStatus: String
    ): Flow<NetworkResult<String>>

    fun viewSummary(chatId: Int): Flow<NetworkResult<com.bussiness.curemegptapp.apimodel.chatModel.SummaryData?>>
}