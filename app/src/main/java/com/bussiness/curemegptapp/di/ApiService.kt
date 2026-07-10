package com.bussiness.curemegptapp.di

import com.bussiness.curemegptapp.apiendpoint.ApiEndPoint
import com.bussiness.curemegptapp.apimodel.OnBoardingModel.OnboardingItem
import com.bussiness.curemegptapp.apimodel.OnBoardingModel.OnboardingResponse
import com.bussiness.curemegptapp.apimodel.alertmodel.AlertDateModel
import com.bussiness.curemegptapp.apimodel.homemodel.HomeDataModel
import com.bussiness.curemegptapp.apimodel.loginmodel.LoginResponse
import com.bussiness.curemegptapp.apimodel.personalmodel.PersonalModel
import com.bussiness.curemegptapp.apimodel.personalmodel.ProfileResponse
import com.bussiness.curemegptapp.apimodel.profilemodel.DeleteMedicalDocRequest
import com.bussiness.curemegptapp.apimodel.profilemodel.UserProfileResponse
import com.bussiness.curemegptapp.apimodel.reportdetailmodel.ReportDetailsModel
import com.bussiness.curemegptapp.apimodel.reportmodel.ReportModel
import com.google.gson.Gson
import kotlinx.serialization.json.JsonObject
import com.google.gson.JsonObject as GsonJsonObject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {

    @FormUrlEncoded
    @POST(ApiEndPoint.LOGIN_URL)
    suspend fun loginRequest(
        @Field("emailPhone") emailOrPhone: String,
        @Field("password") password: String,
        @Field("fcmToken") fcmToken: String
    ) : Response<LoginResponse>

    @FormUrlEncoded
    @POST(ApiEndPoint.SIGNUP_URL)
    suspend fun registerRequest(
        @Field("name") name: String,
        @Field("emailPhone") emailOrPhone: String,
        @Field("password") password: String,
        @Field("deviceType") deviceType: String,
        @Field("fcmToken") fcmToken: String
    ): Response<LoginResponse>

    @FormUrlEncoded
    @POST(ApiEndPoint.UPDATE_PASSWORD_URL)
    suspend fun updatePasswordRequest(
        @Field("emailPhone") emailOrPhone: String,
        @Field("password") password: String
    ): Response<LoginResponse>


    @FormUrlEncoded
    @POST(ApiEndPoint.RESEND_OTP_URL)
    suspend fun sendOtpRequest(
        @Field("emailPhone") emailOrPhone: String
    ): Response<LoginResponse>

    @FormUrlEncoded
    @POST(ApiEndPoint.FORGOT_OTP_URL)
    suspend fun forgotOtpRequest(
        @Field("emailPhone") emailOrPhone: String
    ): Response<LoginResponse>

    @FormUrlEncoded
    @POST(ApiEndPoint.VERIFY_ACCOUNT_URL)
    suspend fun otpVerifySignUpRequest(
        @Field("emailPhone") emailOrPhone: String,
        @Field("otp") otp: String,
        @Field("fcmToken") fcmToken: String
    ): Response<LoginResponse>

    @FormUrlEncoded
    @POST(ApiEndPoint.VERIFY_OTP_URL)
    suspend fun otpVerifyRequest(
        @Field("emailPhone") emailOrPhone: String,
        @Field("otp") otp: String
    ): Response<LoginResponse>



    @POST(ApiEndPoint.USER_DETAILS_URL)
    suspend fun profileRequest(): Response<PersonalModel>

    @POST(ApiEndPoint.REPORT_DATA)
    suspend fun reportListRequest(): Response<ReportModel>

    @POST(ApiEndPoint.HOME_DATA)
    suspend fun getHomeRequest(): Response<HomeDataModel>

    @POST(ApiEndPoint.ALERT_LIST)
    suspend fun getAlertListRequest(): Response<AlertDateModel>

    @POST(ApiEndPoint.ALERT_READ_UPDATE_STATUS)
    @FormUrlEncoded
    suspend fun updateAlertReadStatus(@Field("appointment_id") id: String): Response<AlertDateModel>


    @POST(ApiEndPoint.REPORT_DETAILS)
    @FormUrlEncoded
    suspend fun getReportDetailsRequest(@Field("chat_id") chatId: String): Response<ReportDetailsModel>

    @Multipart
    @POST(ApiEndPoint.COMPLETE_PERSONAL_URL)
    suspend fun updatePersonalRequest(
        @Part("full_name") nameBody: RequestBody,
        @Part("contact_number") phoneBody: RequestBody,
        @Part("email") emailBody: RequestBody,
        @Part("dob") dobBody: RequestBody,
        @Part("gender") genderBody: RequestBody,
        @Part("height") heightBody: RequestBody,
        @Part("weight") weightBody: RequestBody,
        @Part profile_image:  MultipartBody.Part?
    ): Response<PersonalModel>

    @FormUrlEncoded
    @POST(ApiEndPoint.COMPLETE_GENERAL_URL)
    suspend fun  completeGeneralRequest(
        @Field("blood_group") bloodGroup: String,
        @Field("allergies[]") allergies: String,
        @Field("emergency_contact_name") contactName: String,
        @Field("emergency_contact_number") contactNumber :String
    ): Response<ProfileResponse>

    @FormUrlEncoded
    @POST("verify_email_phone")
    suspend fun verifyEmailPhoneRequest(
        @Field("emailPhone") emailOrPhone: String
    ): Response<GsonJsonObject>


    @FormUrlEncoded
    @POST("complete_general_profile_history")
    suspend fun completeGeneralProfileHistoryRequest(
        @Field("chronic_condition[]") chronicConditions: String,
        @Field("surgical_history") surgicalHistory: String,
        @Field("current_medications[]") currentMedication: String,
        @Field("current_supplements[]") currentSupplement : String
    ): Response<ProfileResponse>


    @Multipart
    @POST("complete_profile_documents")
    suspend fun completeProfileDocumentsRequest(
        @Part files: List<MultipartBody.Part>
    ): Response<ProfileResponse>

    @POST("onboarding_data")
    suspend fun onBoardingData() :Response<OnboardingResponse>

    @POST("get_user_details")
    suspend fun getUserDetails() : Response<GsonJsonObject>


    @Multipart
    @POST("update_profile_picture")
    suspend fun updateProfilePicture(
        @Part profile_image: MultipartBody.Part
    ) : Response<GsonJsonObject>


    @POST("faqs")
    suspend fun getFAQs() : Response<GsonJsonObject>

    @POST("privacy_policy")
    suspend fun getPrivacyPolicy() : Response<GsonJsonObject>

     @POST("terms_condition")
    suspend fun getTermsConditions() : Response<GsonJsonObject>


    @POST("account_privacy")
    suspend fun getAccountPrivacy() : Response<GsonJsonObject>

    @POST("account_privacy")
    suspend fun getAccountPrivacyPolicy() : Response<GsonJsonObject>

    @POST("help_support")
    suspend fun helpSupport() : Response<GsonJsonObject>
    @POST("about_us")
    suspend fun  aboutUs() : Response<GsonJsonObject>

    @POST("get_appointment_type")
    suspend fun getAppointmentType() : Response<GsonJsonObject>


    @POST("get_family_members_list")
    suspend fun getFamilyMembersList() : Response<GsonJsonObject>


    @POST("schedule_appointment")
    @FormUrlEncoded
    suspend fun addScheduleAppointment(
        @Field("for_whom_id") forWhomeId :String?,
        @Field("appointment_type_id") appointmentTypeId :String,
        @Field("description") description :String,
        @Field("date") date :String,
        @Field("time") time :String,
        @Field("preferred_doctor") preferredDoctor:String,
        @Field("preferred_clinic") preferredClinic :String,
        @Field("appointment_reminder") reminder :String,
        @Field("recommended_chat_id") recommendedChatId :String?
    ) : Response<GsonJsonObject>


    @POST("get_personal_profile")
    suspend fun getPersonalProfile() : Response<GsonJsonObject>

    @Multipart
    @POST("update_personal_profile")
    suspend fun updatePersonalProfile(
        @Part("full_name") nameBody: RequestBody,
        @Part("contact_number") phoneBody: RequestBody,
        @Part("email_address") emailBody: RequestBody,
        @Part("dob") dobBody: RequestBody,
        @Part("gender") genderBody: RequestBody,
        @Part("height") heightBody: RequestBody,
        @Part("weight") weightBody: RequestBody,
        @Part profile_image:  MultipartBody.Part?
    ): Response<GsonJsonObject>
    @POST("get_appointment_list")
    suspend fun getAppointmentList() : Response<GsonJsonObject>

    @POST("get_general_profile")
    suspend fun getGeneralProfile() : Response<GsonJsonObject>

    @POST("complete_general_profile")
    @FormUrlEncoded
    suspend fun completeGeneralProfile(
        @Field("blood_group") bloodGroup: String,
        @Field("allergies[]") allergies: String,
        @Field("emergency_contact_name") contactName: String,
        @Field("emergency_contact_number") contactNumber : String,
    ) : Response<GsonJsonObject>


    @POST("get_general_profile_history")
    suspend fun getGeneralProfileHistory() : Response<GsonJsonObject>

    @POST("complete_general_profile_history")
    @FormUrlEncoded
    suspend fun updateGeneralProfileHistory(
        @Field("chronic_condition[]") chronicConditions: String,
        @Field("surgical_history") surgicalHistory: String,
        @Field("current_medications[]") currentMedication: String,
        @Field("current_supplements[]") currentSupplement : String
        ): Response<GsonJsonObject>

    @POST("get_profile_documents")
    suspend fun getProfileDocuments() : Response<GsonJsonObject>


    @POST("complete_profile_documents")
    @Multipart
    suspend fun completeProfileDocuments(
        @Part files: List<MultipartBody.Part>
    ) : Response<GsonJsonObject>

    @POST("update_profile_documents")
    @Multipart
    suspend fun updateProfileDocuments(
        @Part files: List<MultipartBody.Part>
    ) : Response<GsonJsonObject>

    @POST("get_schedule_appointment_details")
    @FormUrlEncoded
    suspend fun getScheduleAppointmentDetails(
        @Field("appointment_id") appointmentId :String
    ) : Response<GsonJsonObject>


    @POST("reschedule_appointment")
    @FormUrlEncoded
    suspend fun rescheduleAppointment(
        @Field("appointment_id") appointmentId:Int,
        @Field("date") date :String,
        @Field("time") time :String,
        @Field("appointment_reminder") reminder :String
    ) : Response<GsonJsonObject>


    @POST("appointment_mark_as_complete_incomplete")
    @FormUrlEncoded
    suspend fun markAppointmentComplete(
        @Field("appointment_id") id:Int
    ) : Response<GsonJsonObject>


    @POST("delete_appointment")
    @FormUrlEncoded
    suspend fun deleteAppointment(
        @Field("appointment_id") id:Int
    ) : Response<GsonJsonObject>


    @Multipart
    @POST("add_medication")
    suspend fun addMedication(
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
     ): Response<GsonJsonObject>

    @POST("get_medication_list")
    suspend fun getMedicationList() : Response<GsonJsonObject>

    @POST("get_medication_details")
    @FormUrlEncoded
    suspend fun getMedicationDetails(
        @Field("medication_id") medicationId :Int
    ) : Response<GsonJsonObject>

    @POST("delete_medication")
    @FormUrlEncoded
    suspend fun deleteMedication(
        @Field("medication_id") medicationId :Int
    ) : Response<GsonJsonObject>


    @Multipart
    @POST("update_medication")
    suspend fun updateMedication(
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
    ) : Response<GsonJsonObject>

    @POST("add_family_member_personal")
    @Multipart
    suspend fun addFamilyMemberPersonal(
        @Part("relation") relation : RequestBody,
        @Part("full_name") nameBody: RequestBody,
        @Part("contact_number") phoneBody: RequestBody,
        @Part("email_address") emailBody: RequestBody,
        @Part("dob") dobBody: RequestBody,
        @Part("gender") genderBody: RequestBody,
        @Part("height") heightBody: RequestBody,
        @Part("weight") weightBody: RequestBody,
        @Part profile_image:  MultipartBody.Part?
    ) : Response<GsonJsonObject>

    @POST("add_family_member_general")
    @FormUrlEncoded
    suspend fun addFamilyMemberGeneral(
        @Field("family_member_id") familyMemberId :Int,
        @Field("blood_group") bloodGroup: String,
        @Field("known_allergies[]") knownAllergies :String,
        @Field("emergency_contact_name") contactName :String,
        @Field("emergency_contact_number") contactNumber :String
    ) : Response<GsonJsonObject>

    @POST("add_family_member_history")
    @FormUrlEncoded
    suspend fun addFamilyMemberHistory(
        @Field("family_member_id") familyMemeberId:Int,
        @Field("chronic_conditions[]") chronicCondition : String,
        @Field("surgical_history") surgicalHistory :String,
        @Field("current_medication[]") currentMedication: String,
        @Field("current_supplements[]") currentSupplements : String
    ) : Response<GsonJsonObject>

    @POST("add_family_member_medical_documents")
    @Multipart
    suspend fun addFamilyMemberMedicalDocuments(
        @Part("family_member_id") familyMemeberId : RequestBody,
        @Part  profile_image:  MutableList<MultipartBody.Part?>
    ) : Response<GsonJsonObject>

    @POST("get_family_member_profile")
    @FormUrlEncoded
    suspend fun getFamilyMemberProfile(
        @Field("famil_member_id") id :Int
    ) : Response<GsonJsonObject>

    @POST("update_family_personal_profile")
    @Multipart
    suspend fun updateFamilyPersonalProfile(
        @Part("family_member_id") familyMemberBody : RequestBody,
        @Part("fullName") nameBody: RequestBody,
        @Part("contactNumber") phoneBody: RequestBody,
        @Part("emailAddress") emailBody: RequestBody,
        @Part("dateOfBirth") dobBody: RequestBody,
        @Part("gender") genderBody: RequestBody,
        @Part("height") heightBody: RequestBody,
        @Part("weight") weightBody: RequestBody,
        @Part profile_image:  MultipartBody.Part?
    ) : Response<GsonJsonObject>

    @POST("update_family_general_profile")
    @FormUrlEncoded
    suspend fun updateFamilyGeneralProfile(
        @Field("family_member_id") familyMemberId :Int,
        @Field("bloodGroup") bloodGroup: String,
        @Field("knownAllergies[]") knownAllergies :String,
        @Field("emergencyContactName") contactName :String,
        @Field("emergencyContactNumber") contactNumber :String
    ) : Response<GsonJsonObject>
    

    @POST("update_family_history_profile")
    @FormUrlEncoded
    suspend fun updateFamilyHistoryProfile(
        @Field("family_member_id") familyMemberId :Int,
        @Field("chronicCondition[]") chronicCondition :String,
        @Field("surgicalHistory") surgicalHistory :String,
        @Field("currentMedications[]") currentMediation :String,
        @Field("currentSupplements[]") supplement :String
    ) : Response<GsonJsonObject>


    @POST("delete_user_medical_documents")
    suspend fun deleteUserMedicalDocuments(
        @Body request : DeleteMedicalDocRequest
    )

    @POST("update_family_medical_documents")
    @Multipart
    suspend fun updateFamilyMedicalDocuments(
        @Part("family_member_id") id : RequestBody,
        @Part  profile_image:  MutableList<MultipartBody.Part?>
    )  : Response<GsonJsonObject>

    @POST("family_member_list")
    suspend fun familyMemberList() : Response<GsonJsonObject>

    @POST("get_family_member_profile")
    @FormUrlEncoded
    suspend fun getFamilyMemberProfileDetails(
        @Field("famil_member_id") familyMemberId :Int
    ) : Response<GsonJsonObject>

    @POST("delete_family_member")
    @FormUrlEncoded
    suspend fun deleteFamilyMember(
        @Field("family_member_id")familyMemberId :Int
    ) : Response<GsonJsonObject>

    @POST("delete_family_profile_photo")
    @FormUrlEncoded
    suspend fun deleteFamilyProfilePhoto(
        @Field("family_member_id") familyMemberId: Int
    ): Response<GsonJsonObject>

    @POST("update_family_profile_picture")
    @Multipart
    suspend fun updateFamilyProfilePhoto(
        @Part("family_member_id") familyMemberId: RequestBody,
        @Part profile_image: MultipartBody.Part
    ): Response<GsonJsonObject>




    @POST("user_with_family_details")
    suspend fun getUserWithFamilyDetails() : Response<GsonJsonObject>

    @POST("chat")
    @Multipart
    suspend fun getChatResponse(
        @Part("family_member_id") familyMemberId :RequestBody?,
        @Part("message") message :RequestBody,
        @Part("type") type:RequestBody,
        @Part("chat_id") chatId :RequestBody?,
        @Part profile_image:  MultipartBody.Part?
        ) : Response<GsonJsonObject>


     @POST("get_prompt_questions")
     suspend fun getPromptQuestions() : Response<GsonJsonObject>

     @POST("user_chat_list")
     suspend fun getUserChatList() : Response<GsonJsonObject>

     @POST("rename_chat")
     @FormUrlEncoded
     suspend fun renameChat(
         @Field("chat_id") chatId :Int,
         @Field("title") title :String
     ) : Response<GsonJsonObject>

     @POST("delete_chat")
     @FormUrlEncoded
     suspend fun deleteChat(
         @Field("chat_id") chatId :Int
     ) : Response<GsonJsonObject>

     @POST("user_family_chat_list")
     @FormUrlEncoded
     suspend fun userFamilyChatList(
         @Field("family_member_id") familyMemberId :Int
     ) : Response<GsonJsonObject>

     @POST("get_chat_messages")
     @FormUrlEncoded
     suspend fun getChatMessages(
         @Field("chat_id") chatId :Int
     ) : Response<GsonJsonObject>

     @POST("download_health_report_pdf")
     @FormUrlEncoded
     suspend fun downloadHealthReportPdf(
         @Field("chat_id") familyMemberId :Int
     ) : Response<GsonJsonObject>


     @POST("switch_chat")
     @FormUrlEncoded
     suspend fun switchChat(
         @Field("chat_id") chatId :Int
     ) : Response<GsonJsonObject>

      @POST("delete_profile_photo")
      suspend fun deleteProfilePhoto(): Response<GsonJsonObject>

      @POST("get_all_static_pages")
      suspend fun getAllStaticPages() : Response<GsonJsonObject>

      @POST("get_particular_static_page")
      @FormUrlEncoded
      suspend fun getParticularStaticPage(
          @Field("slug") slug: String
      ) : Response<GsonJsonObject>

      @POST(ApiEndPoint.RESPONSE_LIKE_DISLIKE)
      @FormUrlEncoded
      suspend fun responseLikeDislikeRequest(
          @Field("chat_message_id") chatMessageId: String,
          @Field("like_dislike_status") likeDislikeStatus: String
      ) : Response<GsonJsonObject>

      @POST(ApiEndPoint.VIEW_SUMMARY)
      @FormUrlEncoded
      suspend fun viewSummaryRequest(
          @Field("chat_id") chatId: String
      ) : Response<com.bussiness.curemegptapp.apimodel.chatModel.SummaryResponse>

      @POST("delete_account")
      @FormUrlEncoded
      suspend fun deleteAccountRequest(
          @Field("delete_account_feedback") feedback: String
      ) : Response<GsonJsonObject>
}