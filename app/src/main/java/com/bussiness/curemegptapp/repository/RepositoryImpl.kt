package com.bussiness.curemegptapp.repository

import android.net.Uri
import android.util.Log
import com.bussiness.curemegptapp.apimodel.OnBoardingModel.OnboardingItem
import com.bussiness.curemegptapp.apimodel.OnBoardingModel.OnboardingResponse
import com.bussiness.curemegptapp.apimodel.QuestionAnswer
import com.bussiness.curemegptapp.apimodel.alertmodel.AlertDateModel
import com.bussiness.curemegptapp.apimodel.chatModel.ChatHistoryItem
import com.bussiness.curemegptapp.apimodel.chatModel.FamilyDetails
import com.bussiness.curemegptapp.apimodel.chatModel.PromptQuestionResponse
import com.bussiness.curemegptapp.apimodel.familyProfile.FamilyMemberResponse
import com.bussiness.curemegptapp.apimodel.getAppointmentList.AppointmentData
import com.bussiness.curemegptapp.apimodel.getAppointmentList.AppointmentItem
import com.bussiness.curemegptapp.apimodel.homemodel.HomeDataModel
import com.bussiness.curemegptapp.di.ApiService
import com.bussiness.curemegptapp.util.Messages
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import com.bussiness.curemegptapp.apimodel.loginmodel.LoginResponse
import com.bussiness.curemegptapp.apimodel.medication.MedicationItem
import com.bussiness.curemegptapp.apimodel.medication.MedicationItemDetail
import com.bussiness.curemegptapp.apimodel.personalmodel.PersonalModel
import com.bussiness.curemegptapp.apimodel.personalmodel.ProfileResponse
import com.bussiness.curemegptapp.apimodel.personalmodel.User
import com.bussiness.curemegptapp.apimodel.personalmodel.User1
import com.bussiness.curemegptapp.apimodel.profilemodel.Data.UserProfile
import com.bussiness.curemegptapp.apimodel.profilemodel.DeleteMedicalDocRequest
import com.bussiness.curemegptapp.apimodel.profilemodel.UserProfileResponse
import com.bussiness.curemegptapp.apimodel.reportdetailmodel.ReportDetailsModel
import com.bussiness.curemegptapp.apimodel.reportmodel.ReportModel
import com.bussiness.curemegptapp.apimodel.scheduleAppointment.AppointmentTypeModel
import com.bussiness.curemegptapp.apimodel.scheduleAppointment.FamilyModel
import com.bussiness.curemegptapp.data.model.ChatMessage
import com.bussiness.curemegptapp.data.model.PdfData
import com.bussiness.curemegptapp.data.model.ProfileData
import com.bussiness.curemegptapp.ui.viewModel.main.Document
import com.bussiness.curemegptapp.ui.viewModel.main.FamilyMember
import com.bussiness.curemegptapp.util.AppConstant
import com.bussiness.curemegptapp.util.UriToRequestBody
import com.google.gson.Gson
import com.google.gson.JsonArray
import kotlinx.coroutines.flow.catch
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Response
import retrofit2.http.Part
import javax.inject.Inject
import kotlin.Int
import kotlin.collections.get
//here all api implementation

class RepositoryImpl @Inject constructor(
    private val api: ApiService
) : Repository {

    override fun loginRequest(
        emailOrPhone: String,
        password: String,
        fcmToken: String
    ): Flow<Resource<LoginResponse>> = flow {
        emit(Resource.Loading)
        val result = safeApiCall { api.loginRequest(emailOrPhone, password, fcmToken) }
        emit(result)
    }.flowOn(Dispatchers.IO)

    override fun registerRequest(
        name: String,
        emailOrPhone: String,
        password: String,
        deviceType: String,
        fcmToken: String
    ): Flow<Resource<LoginResponse>> = flow {
        emit(Resource.Loading)
        val result = safeApiCall { api.registerRequest(name, emailOrPhone, password, deviceType, fcmToken) }
        emit(result)
    }.flowOn(Dispatchers.IO)

    override fun updatePasswordRequest(
        emailOrPhone: String,
        password: String
    ): Flow<Resource<LoginResponse>> = flow {
        emit(Resource.Loading)
        val result = safeApiCall { api.updatePasswordRequest(emailOrPhone, password) }
        emit(result)
    }.flowOn(Dispatchers.IO)

    override fun sendOtpRequest(
        emailOrPhone: String
    ): Flow<Resource<LoginResponse>> = flow {
        emit(Resource.Loading)
        val result = safeApiCall { api.sendOtpRequest(emailOrPhone) }
        emit(result)
    }.flowOn(Dispatchers.IO)

    override fun forgotOtpRequest(
        emailOrPhone: String
    ): Flow<Resource<LoginResponse>> = flow {

        emit(Resource.Loading)

        Log.d("TESTING", "Forgot OTP Request: emailOrPhone=$emailOrPhone")

        val result = safeApiCall { api.forgotOtpRequest(emailOrPhone) }

        emit(result)

    }.flowOn(Dispatchers.IO)

    override fun otpVerifyRequest(
        emailOrPhone: String,
        otp: String,
        fcmToken: String,
        fromScreen: String
    ): Flow<Resource<LoginResponse>> = flow {
        emit(Resource.Loading)
        val result = if (fromScreen.equals("create", true)) {
            safeApiCall { api.otpVerifySignUpRequest(emailOrPhone, otp, fcmToken) }
        } else {
            safeApiCall { api.otpVerifyRequest(emailOrPhone, otp) }
        }
        emit(result)
    }.flowOn(Dispatchers.IO)

    override fun profileRequest(): Flow<Resource<PersonalModel>> = flow {
        emit(Resource.Loading)
        val result = safeApiCall { api.profileRequest() }
        emit(result)
    }.flowOn(Dispatchers.IO)

    override fun reportListRequest(): Flow<Resource<ReportModel>> = flow {
        emit(Resource.Loading)
        val result = safeApiCall { api.reportListRequest() }
        emit(result)
    }.flowOn(Dispatchers.IO)

    override fun getHomeRequest(): Flow<Resource<HomeDataModel>> = flow {
        emit(Resource.Loading)
        val result = safeApiCall { api.getHomeRequest() }
        emit(result)
    }.flowOn(Dispatchers.IO)

    override fun getAlertListRequest(): Flow<Resource<AlertDateModel>> = flow {
        emit(Resource.Loading)
        val result = safeApiCall { api.getAlertListRequest() }
        emit(result)
    }.flowOn(Dispatchers.IO)

    override fun updateAlertReadStatus(id: String): Flow<Resource<AlertDateModel>> = flow {
        emit(Resource.Loading)
        val result = safeApiCall { api.updateAlertReadStatus(id) }
        emit(result)
    }.flowOn(Dispatchers.IO)

    override fun getReportDetailsRequest(id: String): Flow<Resource<ReportDetailsModel>> = flow {
        emit(Resource.Loading)
        val result = safeApiCall { api.getReportDetailsRequest(id) }
        emit(result)
    }.flowOn(Dispatchers.IO)

    override fun updatePersonalRequest(
        name: String, phone: String, email: String, dob: String, gender: String,
        height: String, heightType: String, weight: String, weightType: String,
        profileImage: MultipartBody.Part?
    ): Flow<Resource<PersonalModel>> = flow {

        emit(Resource.Loading)

        val nameBody = name.toRequestBody("text/plain".toMediaTypeOrNull())
        val phoneBody = phone.toRequestBody("text/plain".toMediaTypeOrNull())
        val emailBody = email.toRequestBody("text/plain".toMediaTypeOrNull())
        val dobBody = dob.toRequestBody("text/plain".toMediaTypeOrNull())
        val genderBody = gender.toRequestBody("text/plain".toMediaTypeOrNull())
        val heightBody = "$height$heightType".toRequestBody("text/plain".toMediaTypeOrNull())
        val weightBody = "$weight$weightType".toRequestBody("text/plain".toMediaTypeOrNull())
        val result = safeApiCall {
            api.updatePersonalRequest(
                nameBody, phoneBody, emailBody,
                dobBody, genderBody, heightBody, weightBody, profileImage
            )
        }

        emit(result)

    }.flowOn(Dispatchers.IO)

    override fun generalProfileRequest(
        bloodGroup: String, allergies: String,
        emergencyContactName: String, emergencyContactPhone: String
    ): Flow<Resource<ProfileResponse>> = flow {

        emit(Resource.Loading)
        val result = safeApiCall {
            api.completeGeneralRequest(
                bloodGroup,
                allergies,
                emergencyContactName,
                emergencyContactPhone
            )
        }
        emit(result)
    }.flowOn(Dispatchers.IO)


    override fun verifyEmailPhoneRequest(emailOrPhone: String): Flow<NetworkResult<String>> = flow {
        try {
            val response = api.verifyEmailPhoneRequest(emailOrPhone)
            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                        val data = respBody.get("data").asJsonObject
                        val otp = data.get("otp").asInt
                        emit(NetworkResult.Success(otp.toString()))
                    } else {
                        emit(NetworkResult.Error(respBody.get("message").asString))
                    }
                } else {
                    emit(NetworkResult.Error(AppConstant.serverError))
                }
            } else {
                emit(NetworkResult.Error(AppConstant.serverError))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(NetworkResult.Error(AppConstant.serverError))
        }
    }

    override suspend fun completeGeneralProfileHistoryRequest(
        chronicConditions: String,
        surgicalHistory: String,
        currentMedication: String,
        currentSupplement: String
    ): Flow<Resource<ProfileResponse>> = flow {
        emit(Resource.Loading)
        val result = safeApiCall {
            api.completeGeneralProfileHistoryRequest(
                chronicConditions, surgicalHistory, currentMedication,
                currentSupplement
            )
        }
        emit(result)
    }.flowOn(Dispatchers.IO)

    override suspend fun completeProfileDocumentsRequest(files: List<MultipartBody.Part>): Flow<Resource<ProfileResponse>> =
        flow {

            emit(Resource.Loading)

            val result = safeApiCall { api.completeProfileDocumentsRequest(files) }

            emit(result)

        }.flowOn(Dispatchers.IO)

    override suspend fun onBoardingData(): Flow<Resource<OnboardingResponse>> = flow {
        emit(Resource.Loading)
        val result = safeApiCall { api.onBoardingData() }

        emit(result)

    }.flowOn(Dispatchers.IO)

    override suspend fun getUserDetails(): Flow<NetworkResult<UserProfile>> = flow {
        try {
            val response = api.getUserDetails()
            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                        val data = respBody.get("data").asJsonObject
                        val userObj = data.get("user").asJsonObject
                        val userProfile = Gson().fromJson(userObj, UserProfile::class.java)
                        emit(NetworkResult.Success(userProfile))
                    } else {
                        emit(NetworkResult.Error(respBody.get("message").asString))
                    }
                } else {
                    emit(NetworkResult.Error(AppConstant.serverError))
                }
            } else {
                emit(NetworkResult.Error(AppConstant.serverError))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(NetworkResult.Error(AppConstant.serverError))
        }
    }

    override fun updateProfilePicture(profile_image: MultipartBody.Part): Flow<NetworkResult<String>> =
        flow {
            try {
                val response = api.updateProfilePicture(profile_image)
                if (response.isSuccessful) {
                    val respBody = response.body()
                    if (respBody != null) {
                        if (respBody.has("success") && respBody.get("success").asBoolean) {

                            emit(NetworkResult.Success("Profile picture updated successfully"))
                        } else {
                            emit(NetworkResult.Error(respBody.get("message").asString))
                        }
                    } else {
                        emit(NetworkResult.Error(AppConstant.serverError))
                    }

                } else {
                    emit(NetworkResult.Error(AppConstant.serverError))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                emit(NetworkResult.Error(AppConstant.serverError))
            }

        }

    override fun getFAQ(): Flow<NetworkResult<List<QuestionAnswer>>> = flow {

        try {
            val response = api.getFAQs()
            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                        val dataObject = respBody.get("data")?.asJsonObject
                        val dataArray = dataObject?.get("data")?.asJsonArray ?: return@flow
                        val faqList = dataArray.map { element ->
                            Gson().fromJson(element, QuestionAnswer::class.java)
                        }
                        emit(NetworkResult.Success(faqList))
                    } else {
                        emit(NetworkResult.Error(respBody.get("message").asString))
                    }
                } else {
                    emit(NetworkResult.Error(AppConstant.serverError))
                }
            } else {
                emit(NetworkResult.Error(AppConstant.serverError))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(NetworkResult.Error(AppConstant.serverError))
        }
    }

    override fun getPrivacyPolicy(): Flow<NetworkResult<String>> = flow {
        try {
            val response = api.getPrivacyPolicy()
            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                        val dataObject = respBody.get("data")?.asJsonObject
                        val secondData = dataObject?.get("data")?.asJsonObject
                        val content = secondData?.get("content")?.asString ?: ""
                        emit(NetworkResult.Success(content))
                    } else {
                        emit(NetworkResult.Error(respBody.get("message").asString))
                    }
                } else {
                    emit(NetworkResult.Error(AppConstant.serverError))
                }
            } else {
                emit(NetworkResult.Error(AppConstant.serverError))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(NetworkResult.Error(AppConstant.serverError))
        }
    }

    override fun getTermsAndConditions(): Flow<NetworkResult<String>> = flow {
        try {
            val response = api.getTermsConditions()
            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                        val dataObject = respBody.get("data")?.asJsonObject
                        val secondData = dataObject?.get("data")?.asJsonObject
                        val content = secondData?.get("content")?.asString ?: ""
                        emit(NetworkResult.Success(content))
                    } else {
                        emit(NetworkResult.Error(respBody.get("message").asString))
                    }
                } else {
                    emit(NetworkResult.Error(AppConstant.serverError))
                }
            } else {
                emit(NetworkResult.Error(AppConstant.serverError))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(NetworkResult.Error(AppConstant.serverError))
        }
    }

    override fun getAccountPrivacyPolicy(): Flow<NetworkResult<String>> = flow {
        try {
            val response = api.getAccountPrivacyPolicy()
            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                        val dataObject = respBody.get("data")?.asJsonObject
                        val secondData = dataObject?.get("data")?.asJsonObject
                        val content = secondData?.get("content")?.asString ?: ""
                        emit(NetworkResult.Success(content))
                    } else {
                        emit(NetworkResult.Error(respBody.get("message").asString))
                    }
                } else {
                    emit(NetworkResult.Error(AppConstant.serverError))
                }
            } else {
                emit(NetworkResult.Error(AppConstant.serverError))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(NetworkResult.Error(AppConstant.serverError))
        }
    }

    override fun helpSupport(): Flow<NetworkResult<String>> = flow {
        try {
            val response = api.helpSupport()
            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                        val dataObject = respBody.get("data")?.asJsonObject
                        val secondData = dataObject?.get("data")?.asJsonObject
                        val content = secondData?.get("content")?.asString ?: ""
                        emit(NetworkResult.Success(content))
                    } else {
                        emit(NetworkResult.Error(respBody.get("message").asString))
                    }
                } else {
                    emit(NetworkResult.Error(AppConstant.serverError))
                }
            } else {
                emit(NetworkResult.Error(AppConstant.serverError))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(NetworkResult.Error(AppConstant.serverError))
        }
    }

    override fun aboutUs(): Flow<NetworkResult<String>> =flow{
        try {
            val response = api.aboutUs()
            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                        val dataObject = respBody.get("data")?.asJsonObject
                        val secondData = dataObject?.get("data")?.asJsonObject
                        val content = secondData?.get("content")?.asString ?: ""
                        emit(NetworkResult.Success(content))
                    } else {
                        emit(NetworkResult.Error(respBody.get("message").asString))
                    }
                } else {
                    emit(NetworkResult.Error(AppConstant.serverError))
                }
            } else {
                emit(NetworkResult.Error(AppConstant.serverError))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(NetworkResult.Error(AppConstant.serverError))
        }
    }

    override fun getAppointmentType(): Flow<NetworkResult<List<AppointmentTypeModel>>> =
        flow {

            val response = api.getAppointmentType()

            if (response.isSuccessful) {

                val respBody = response.body()

                if (respBody != null) {

                    if (respBody.has("success") && respBody.get("success").asBoolean) {

                        val dataObject = respBody.get("data")?.asJsonObject
                        val dataArray = dataObject?.get("data")?.asJsonArray ?: return@flow

                        val appointmentTypeList = dataArray.map { element ->
                            Gson().fromJson(element, AppointmentTypeModel::class.java)
                        }

                        emit(NetworkResult.Success(appointmentTypeList))

                    } else {
                        emit(NetworkResult.Error(respBody.get("message").asString))
                    }

                } else {
                    emit(NetworkResult.Error(AppConstant.serverError))
                }

            } else {
                emit(NetworkResult.Error(AppConstant.serverError))
            }

        }.catch { e ->

            e.printStackTrace()
            emit(NetworkResult.Error(AppConstant.serverError))

        }

    override fun getFamilyMembersList(): Flow<NetworkResult<List<FamilyModel>>> =
        flow {

            val response = api.getFamilyMembersList()

            if (response.isSuccessful) {

                val respBody = response.body()

                if (respBody != null) {

                    if (respBody.has("success") && respBody.get("success").asBoolean) {

                        val dataObject = respBody.get("data")?.asJsonObject
                        val dataArray = dataObject?.get("people")?.asJsonArray ?: return@flow

                        val familyList = dataArray.map { element ->
                            Gson().fromJson(element, FamilyModel::class.java)
                        }
                        emit(NetworkResult.Success(familyList))
                    } else {
                        emit(NetworkResult.Error(respBody.get("message").asString))
                    }
                } else {
                    emit(NetworkResult.Error(AppConstant.serverError))
                }
            } else {
                emit(NetworkResult.Error(AppConstant.serverError))
            }
        }.catch { e ->
            e.printStackTrace()
            emit(NetworkResult.Error(AppConstant.serverError))

        }

    override fun addScheduleAppointment(
        forWhomeId: String?,
        appointmentTypeId: String,
        description: String,
        date: String,
        time: String,
        preferredDoctor: String,
        preferredClinic: String,
        reminder: String,
        recommendedChatId:String?
    ): Flow<NetworkResult<String>> =  flow {
        try {
            val response = api.addScheduleAppointment(
            forWhomeId,appointmentTypeId,description,date,time,preferredDoctor,preferredClinic,reminder,recommendedChatId)

            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                        val dataObject = respBody.get("message").asString
                        emit(NetworkResult.Success(dataObject))
                    } else {
                        emit(NetworkResult.Error(respBody.get("message").asString))
                    }
                } else {
                    emit(NetworkResult.Error(AppConstant.serverError))
                }
            } else {
                emit(NetworkResult.Error(AppConstant.serverError))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(NetworkResult.Error(AppConstant.serverError))
        }
    }

    override fun getPersonalProfile(): Flow<NetworkResult<User1>> = flow {
        try {

            val response = api.getPersonalProfile()

            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {

                        val obj = respBody.get("data").asJsonObject

                        val profileResponse = User1(
                            id = obj.get("id")?.asInt ?: 0,
                            name = obj.getStringSafe("full_name"),
                            phone = obj.getStringSafe("contact_number"),
                            email = obj.getStringSafe("email_address"),
                            dob = obj.getStringSafe("dob"),
                            gender = obj.getStringSafe("gender"),
                            height = obj.getStringSafe("height"),
                            weight = obj.getStringSafe("weight"),
                            profile_image = obj.getStringSafe("profile_image").takeIf { it.isNotBlank() } ?: obj.getStringSafe("profile_photo"),
                            profile_photo = obj.getStringSafe("profile_image").takeIf { it.isNotBlank() } ?: obj.getStringSafe("profile_photo")
                        )

                         emit(NetworkResult.Success(profileResponse))

                    } else {
                        emit(NetworkResult.Error(respBody.get("message").asString))
                    }
                } else {
                    emit(NetworkResult.Error(AppConstant.serverError))
                }
            } else {
                emit(NetworkResult.Error(AppConstant.serverError))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(NetworkResult.Error(AppConstant.serverError))
        }
    }

    override fun getAllStaticPages(): Flow<NetworkResult<List<com.bussiness.curemegptapp.apimodel.StaticPage>>> = flow {
        try {
            val response = api.getAllStaticPages()
            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                        val dataObj = respBody.getAsJsonObject("data")
                        val listArray = dataObj.getAsJsonArray("data")

                        val type = object : TypeToken<List<com.bussiness.curemegptapp.apimodel.StaticPage>>() {}.type
                        val pagesList: List<com.bussiness.curemegptapp.apimodel.StaticPage> = Gson().fromJson(listArray, type)

                        emit(NetworkResult.Success(pagesList))
                    } else {
                        emit(NetworkResult.Error(respBody.get("message").asString))
                    }
                } else {
                    emit(NetworkResult.Error(AppConstant.serverError))
                }
            } else {
                emit(NetworkResult.Error(AppConstant.serverError))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(NetworkResult.Error(AppConstant.serverError))
        }
    }

    override fun getParticularStaticPage(slug: String): Flow<NetworkResult<com.bussiness.curemegptapp.apimodel.StaticPage>> = flow {
        try {
            val response = api.getParticularStaticPage(slug)
            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                        val dataObj = respBody.getAsJsonObject("data")
                        val pageObj = dataObj.getAsJsonObject("data")

                        val page = Gson().fromJson(pageObj, com.bussiness.curemegptapp.apimodel.StaticPage::class.java)

                        emit(NetworkResult.Success(page))
                    } else {
                        emit(NetworkResult.Error(respBody.get("message").asString))
                    }
                } else {
                    emit(NetworkResult.Error(AppConstant.serverError))
                }
            } else {
                emit(NetworkResult.Error(AppConstant.serverError))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(NetworkResult.Error(AppConstant.serverError))
        }
    }

    override fun updatePersonalProfile(
        name: String,
        phone: String,
        email: String,
        dob: String,
        gender: String,
        height: String,
        weight: String,
        profileImage: MultipartBody.Part?
    ): Flow<NetworkResult<String>> = flow {
        try {

            val nameBody = name.toRequestBody("text/plain".toMediaTypeOrNull())
            val phoneBody = phone.toRequestBody("text/plain".toMediaTypeOrNull())
            val emailBody = email.toRequestBody("text/plain".toMediaTypeOrNull())
            val dobBody = dob.toRequestBody("text/plain".toMediaTypeOrNull())
            val genderBody = gender.toRequestBody("text/plain".toMediaTypeOrNull())
            val heightBody = height.toRequestBody("text/plain".toMediaTypeOrNull())
            val weightBody = weight.toRequestBody("text/plain".toMediaTypeOrNull())

            val response = api.updatePersonalProfile(
                nameBody,phoneBody,emailBody,dobBody,genderBody,heightBody,weightBody,
                profileImage
            )

            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                        emit(NetworkResult.Success("Profile updated successfully"))
                    } else {
                        emit(NetworkResult.Error(respBody.get("message").asString))
                    }
                } else {
                    emit(NetworkResult.Error(AppConstant.serverError))
                }
            } else {
                emit(NetworkResult.Error(AppConstant.serverError))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(NetworkResult.Error(AppConstant.serverError))
        }
    }

    override fun getAppointmentList(): Flow<NetworkResult<List<AppointmentItem>>> = flow {
        try {
            val response = api.getAppointmentList()
            if(response.isSuccessful) {
                val respBody = response.body()

                if (respBody != null) {
                    if(respBody.get("success").asBoolean) {
                        val dataObj = respBody.getAsJsonObject("data")
                        val listArray = dataObj.getAsJsonArray("data")

                        val type = object : TypeToken<List<AppointmentItem>>() {}.type
                        val appointmentList: List<AppointmentItem> =
                            Gson().fromJson(listArray, type)

                        emit(NetworkResult.Success(appointmentList))

                    } else {
                        emit(NetworkResult.Error(respBody.get("message").asString))
                    }

                } else {
                    emit(NetworkResult.Error(AppConstant.serverError))
                }

            } else {
                emit(NetworkResult.Error(AppConstant.serverError))
            }

        } catch (e: Exception) {
            e.printStackTrace()
            emit(NetworkResult.Error(AppConstant.serverError))
        }
    }

    override fun getProfileDocuments(): Flow<NetworkResult<List<String>>> =flow{
        try {
            val response = api.getProfileDocuments()

            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                        var result = listOf<String>()
                        val dataObject = respBody.get("data")?.asJsonObject
                        val dataArray = dataObject?.get("medical_documents")?.asJsonArray ?: return@flow
                        result = dataArray.map {
                            element -> element.asString
                        }
                        emit(NetworkResult.Success(result))
                    } else {
                        emit(NetworkResult.Error(respBody.get("message").asString))
                    }
                } else {
                    emit(NetworkResult.Error(AppConstant.serverError))
                }
            } else {
                emit(NetworkResult.Error(AppConstant.serverError))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(NetworkResult.Error(AppConstant.serverError))
        }
    }

    override suspend fun updateGeneralProfileHistory(
        chronicConditions: String,
        surgicalHistory: String,
        currentMedication: String,
        currentSupplement: String
    ): Flow<NetworkResult<String>>  = flow{
        try {
            val response = api.updateGeneralProfileHistory(
                chronicConditions, surgicalHistory, currentMedication, currentSupplement
            )

            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {

                        emit(NetworkResult.Success("Profile history updated successfully"))

                    } else {
                        emit(NetworkResult.Error(respBody.get("message").asString))
                    }
                } else {
                    emit(NetworkResult.Error(AppConstant.serverError))
                }
            } else {
                emit(NetworkResult.Error(AppConstant.serverError))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(NetworkResult.Error(AppConstant.serverError))
        }
    }

    override fun getGeneralProfileHistory(): Flow<NetworkResult<User1>> = flow {
        try {

            val response = api.getGeneralProfileHistory()

            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                        val dataObj = respBody.getAsJsonObject("data")
                        fun JsonObject.getSafeString(key: String): String {
                            return if (has(key) && !get(key).isJsonNull) {
                                get(key).asString.trim()
                            } else {
                                ""
                            }
                        }

                        fun JsonObject.getSafeStringList(key: String): String {
                            return if (has(key) && get(key).isJsonArray) {
                                getAsJsonArray(key)
                                    .mapNotNull { element ->
                                        if (!element.isJsonNull) element.asString.trim() else null
                                    }
                                    .joinToString(", ")
                            } else {
                                ""
                            }
                        }

                        val profileResponse = User1(
                            blood_group = dataObj.getSafeString("blood_group"),
                            allergies = dataObj.getSafeStringList("known_allergies"),
                            emergency_contact_name = dataObj.getSafeString("emergency_contact_name"),
                            emergency_contact_number = dataObj.getSafeString("emergency_phone_number"),
                            chronic_condition = dataObj.getSafeStringList("chronic_condition"),
                            surgical_history = dataObj.getSafeString("surgical_history"),
                            current_medications = dataObj.getSafeStringList("current_medications"),
                            current_supplements = dataObj.getSafeStringList("current_supplements")
                        )

                        emit(NetworkResult.Success(profileResponse))

                    } else {
                        emit(NetworkResult.Error(respBody.get("message").asString))
                    }
                } else {
                    emit(NetworkResult.Error(AppConstant.serverError))
                }
            } else {
                emit(NetworkResult.Error(AppConstant.serverError))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(NetworkResult.Error(AppConstant.serverError))
        }
    }

    override fun updateGeneralProfile(
        bloodGroup: String,
        allergies: String,
        contactName: String,
        contactNumber: String
    ): Flow<NetworkResult<String>> =flow{
        try {
            val response = api.completeGeneralProfile(
                bloodGroup,allergies,contactName,contactNumber
            )

            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {

                        emit(NetworkResult.Success("General profile completed successfully"))

                    } else {
                        emit(NetworkResult.Error(respBody.get("message").asString))
                    }
                } else {
                    emit(NetworkResult.Error(AppConstant.serverError))
                }
            } else {
                emit(NetworkResult.Error(AppConstant.serverError))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(NetworkResult.Error(AppConstant.serverError))
        }

    }

    override fun getGeneralProfile(): Flow<NetworkResult<User1>>  = flow{
        try {

            val response = api.getGeneralProfile()

            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {

                        val dataObj = respBody.getAsJsonObject("data")

                        val allergiesList = if (dataObj.has("known_allergies") &&
                            dataObj.get("known_allergies").isJsonArray) {

                            dataObj.getAsJsonArray("known_allergies")
                                .mapNotNull { element ->
                                    if (!element.isJsonNull) element.asString.trim() else null
                                }

                        } else {
                            emptyList()
                        }

                        val allergiesString = allergiesList.joinToString(", ")

                        fun getSafeString(obj: JsonObject, key: String): String {
                            return if (obj.has(key) && !obj.get(key).isJsonNull) {
                                try {
                                    obj.get(key).asString.trim()
                                } catch (e: Exception) {
                                    ""
                                }
                            } else {
                                ""
                            }
                        }

                        val profileResponse = User1(
                            blood_group = getSafeString(dataObj, "blood_group"),
                            allergies = allergiesString,
                            emergency_contact_name = getSafeString(dataObj, "emergency_contact_name"),
                            emergency_contact_number = getSafeString(dataObj, "emergency_phone_number")
                        )

                        emit(NetworkResult.Success(profileResponse))
                    } else {
                        emit(NetworkResult.Error(respBody.get("message").asString))
                    }
                } else {
                    emit(NetworkResult.Error(AppConstant.serverError))
                }
            } else {
                emit(NetworkResult.Error(AppConstant.serverError))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(NetworkResult.Error(AppConstant.serverError))
        }
    }

    override fun completeProfileDocuments(files: List<MultipartBody.Part>): Flow<NetworkResult<String>>
            = flow {
        try {
            val response = api.completeProfileDocuments(files)
            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                        emit(NetworkResult.Success("Profile Updated successfully"))
                    } else {
                        emit(NetworkResult.Error(respBody.get("message").asString))
                    }
                } else {
                    emit(NetworkResult.Error(AppConstant.serverError))
                }
            } else {
                emit(NetworkResult.Error(AppConstant.serverError))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(NetworkResult.Error(AppConstant.serverError))
        }
    }

    override fun updateProfileDocuments(files: List<MultipartBody.Part>): Flow<NetworkResult<String>> = flow{
        try {
            val response = api.updateProfileDocuments(files)
            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                        emit(NetworkResult.Success("Profile Updated successfully"))
                    } else {
                        emit(NetworkResult.Error(respBody.get("message").asString))
                    }
                } else {
                    emit(NetworkResult.Error(AppConstant.serverError))
                }
            } else {
                emit(NetworkResult.Error(AppConstant.serverError))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(NetworkResult.Error(AppConstant.serverError))
        }
    }

    override fun getScheduleAppointmentDetails(appointmentId: String): Flow<NetworkResult<AppointmentData>> = flow {
        try {
            val response = api.getScheduleAppointmentDetails(appointmentId)
            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                      //  emit(NetworkResult.Success("Profile Updated successfully"))
                        val dataObj = respBody.getAsJsonObject("data")
                        val innerDataObj = dataObj.getAsJsonObject("data")
                        val appointmentData = Gson().fromJson(innerDataObj, AppointmentData::class.java)
                        emit(NetworkResult.Success(appointmentData))
                    } else {
                        emit(NetworkResult.Error(respBody.get("message").asString))
                    }
                } else {
                    emit(NetworkResult.Error(AppConstant.serverError))
                }
            } else {
                emit(NetworkResult.Error(AppConstant.serverError))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(NetworkResult.Error(AppConstant.serverError))
        }
    }

    override fun rescheduleAppointment(
        appointmentId: Int,
        date: String,
        time: String,
        reminder: String
    ): Flow<NetworkResult<String>> = flow {
        try {
            val response = api.rescheduleAppointment(appointmentId,date,time,reminder)
            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                        emit(NetworkResult.Success("Profile Updated successfully"))
                    } else {
                        emit(NetworkResult.Error(respBody.get("message").asString))
                    }
                } else {
                    emit(NetworkResult.Error(AppConstant.serverError))
                }
            } else {
                emit(NetworkResult.Error(AppConstant.serverError))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(NetworkResult.Error(AppConstant.serverError))
        }
    }

    override fun markAppointmentComplete(id: Int): Flow<NetworkResult<String>> = flow{
        try {
            val response = api.markAppointmentComplete(id)
            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                        emit(NetworkResult.Success("Profile Updated successfully"))
                    } else {
                        emit(NetworkResult.Error(respBody.get("message").asString))
                    }
                } else {
                    emit(NetworkResult.Error(AppConstant.serverError))
                }
            } else {
                emit(NetworkResult.Error(AppConstant.serverError))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(NetworkResult.Error(AppConstant.serverError))
        }
    }

    override fun deleteAppointment(id: Int): Flow<NetworkResult<String>> =flow{
        try {
            val response = api.deleteAppointment(id)
            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                        emit(NetworkResult.Success("Deleted successfully"))
                    } else {
                        emit(NetworkResult.Error(respBody.get("message").asString))
                    }
                } else {
                    emit(NetworkResult.Error(AppConstant.serverError))
                }
            } else {
                emit(NetworkResult.Error(AppConstant.serverError))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(NetworkResult.Error(AppConstant.serverError))
        }
    }

    override fun addMedication(
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
    ): Flow<NetworkResult<String>> = flow {
        try {
            val response = api.addMedication(
                forWhomId,medicationType,medicationName,dosage,frequency,days,startDate,endDate,notes,reminderStatus,
                reminderTimes,prescriptionDocs)
            Log.d("TESTING_ADD_MEDICATION", "addMedication try: $response")
            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                        emit(NetworkResult.Success(respBody.get("message").asString))
                    } else {
                        emit(NetworkResult.Error(respBody.get("message").asString))
                    }
                } else {
                    emit(NetworkResult.Error(AppConstant.serverError))
                }
            } else {
                emit(NetworkResult.Error(AppConstant.serverError))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("TESTING_ADD_MEDICATION", "addMedication catch: ${e}")
            emit(NetworkResult.Error(AppConstant.serverError))
        }
    }

    override fun getMedicationList(): Flow<NetworkResult<MutableList<MedicationItem>>> = flow {

        emit(NetworkResult.Loading())

        try {

            val response = api.getMedicationList()

            if (response.isSuccessful) {
               Log.d("TESTING_MEDICATION","Here inside the medicationlist")
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                        Log.d("TESTING_MEDICATION","Success Here inside the medicationlist")
                        if (respBody.has("data") && !respBody.get("data").isJsonNull) {
                            val dataObject = respBody.getAsJsonObject("data")
                            if (dataObject.has("data") && !dataObject.get("data").isJsonNull) {
                                val dataArray = dataObject.getAsJsonArray("data")
                                val type = object : TypeToken<MutableList<MedicationItem>>() {}.type
                                val medicationList: MutableList<MedicationItem> =
                                    Gson().fromJson(dataArray, type)
                                emit(NetworkResult.Success(medicationList))
                            } else {
                                emit(NetworkResult.Success(mutableListOf()))
                            }
                        } else {
                            emit(NetworkResult.Success(mutableListOf()))
                        }
                    }
                    else {
                        Log.d("TESTING_MEDICATION","Error Here inside the medicationlist")
                        val message = if (respBody.has("message"))
                            respBody.get("message").asString
                        else
                            AppConstant.serverError
                        emit(NetworkResult.Error(message))
                    }
                } else {
                    emit(NetworkResult.Error(AppConstant.serverError))
                }
            } else {
                emit(NetworkResult.Error(AppConstant.serverError))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(NetworkResult.Error(AppConstant.serverError))
        }

    }

    override fun getMedicationDetails(medicationId: Int): Flow<NetworkResult<MedicationItemDetail>> = flow {
        emit(NetworkResult.Loading())

        try {
            val response = api.getMedicationDetails(medicationId)
            if (response.isSuccessful) {
                Log.d("TESTING_MEDICATION","Here inside the medicationlist")

                val respBody = response.body()

                if (respBody != null) {

                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                        Log.d("TESTING_MEDICATION","Success Here inside the medicationlist")

                        if (respBody.has("data") && !respBody.get("data").isJsonNull) {

                            val dataObject = respBody.getAsJsonObject("data")

                            if (dataObject.has("data") && !dataObject.get("data").isJsonNull) {
                                val response = dataObject.get("data").asJsonObject
                                val medicationItem = Gson().fromJson(response, MedicationItemDetail::class.java)
                                emit(NetworkResult.Success(medicationItem))
                            } else {
                                emit(NetworkResult.Error("Details Not Found"))
                            }
                        } else {
                            emit(NetworkResult.Error("Details Not Found"))
                        }

                    }
                    else {
                        Log.d("TESTING_MEDICATION","Error Here inside the medicationlist")

                        val message = if (respBody.has("message"))
                            respBody.get("message").asString
                        else
                            AppConstant.serverError

                        emit(NetworkResult.Error(message))
                    }

                } else {
                    emit(NetworkResult.Error(AppConstant.serverError))
                }

            } else {
                emit(NetworkResult.Error(AppConstant.serverError))
            }

        } catch (e: Exception) {
            e.printStackTrace()
            emit(NetworkResult.Error(AppConstant.serverError))
        }
    }

    override fun deleteMedication(medicationId: Int): Flow<NetworkResult<String>> =flow{
        try {
            val response = api.deleteMedication(medicationId)
            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                        emit(NetworkResult.Success("Deleted successfully"))
                    } else {
                        emit(NetworkResult.Error(respBody.get("message").asString))
                    }
                } else {
                    emit(NetworkResult.Error(AppConstant.serverError))
                }
            } else {
                emit(NetworkResult.Error(AppConstant.serverError))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(NetworkResult.Error(AppConstant.serverError))
        }
    }

    override fun updateMedication(
        medicationId: RequestBody?,
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
    ) : Flow<NetworkResult<String>> = flow{
        try {
            val response = api.updateMedication(medicationId,
                forWhomId,medicationType,medicationName,
                dosage,frequency,days, startDate,endDate,notes,
                reminderStatus, reminderTimes,prescriptionDocs)

            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                        emit(NetworkResult.Success(respBody.get("message").asString))
                    } else {
                        emit(NetworkResult.Error(respBody.get("message").asString))
                    }
                } else {
                    emit(NetworkResult.Error(AppConstant.serverError))
                }
            } else {
                emit(NetworkResult.Error(AppConstant.serverError))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(NetworkResult.Error(AppConstant.serverError))
        }

    }

    override  fun addFamilyMemberPersonal(
        relation: RequestBody,
        nameBody: RequestBody,
        phoneBody: RequestBody,
        emailBody: RequestBody,
        dobBody: RequestBody,
        genderBody: RequestBody,
        heightBody: RequestBody,
        weightBody: RequestBody,
        profile_image: MultipartBody.Part?
    ): Flow<NetworkResult<Int>> =flow{
        try {
            val response = api.addFamilyMemberPersonal(relation, nameBody,phoneBody,emailBody,dobBody,
                genderBody,heightBody,weightBody,profile_image)
            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                        val data = respBody.get("data").asJsonObject
                        val dataInner = data.get("data").asJsonObject
                        val id =   dataInner.get("id").asInt

                        emit(NetworkResult.Success(id))
                    } else {
                        emit(NetworkResult.Error(respBody.get("message").asString))
                    }
                } else {
                    emit(NetworkResult.Error(AppConstant.serverError))
                }
            } else {
                emit(NetworkResult.Error(AppConstant.serverError))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(NetworkResult.Error(AppConstant.serverError))
        }
    }

    override fun addFamilyMemberGeneral(
        familyMemberId: Int,
        bloodGroup: String,
        knownAllergies: String,
        contactName: String,
        contactNumber: String
    ): Flow<NetworkResult<String>> = flow{
        try {

            val response = api.addFamilyMemberGeneral(
                familyMemberId, bloodGroup, knownAllergies,
                contactName, contactNumber
                )

            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                        emit(NetworkResult.Success(respBody.get("message").asString))
                    } else {
                        emit(NetworkResult.Error(respBody.get("message").asString))
                    }
                } else {
                    emit(NetworkResult.Error(AppConstant.serverError))
                }
            } else {
                emit(NetworkResult.Error(AppConstant.serverError))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(NetworkResult.Error(AppConstant.serverError))
        }
    }

    override fun addFamilyMemberHistory(
        familyMemeberId: Int,
        chronicCondition: String,
        surgicalHistory: String,
        currentMedication: String,
        currentSupplements: String
    ): Flow<NetworkResult<String>> =flow{
        try {
            val response = api.addFamilyMemberHistory(
                familyMemeberId,chronicCondition,surgicalHistory,currentMedication,currentSupplements
            )

            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                        emit(NetworkResult.Success(respBody.get("message").asString))
                    } else {
                        emit(NetworkResult.Error(respBody.get("message").asString))
                    }
                } else {
                    emit(NetworkResult.Error(AppConstant.serverError))
                }
            } else {
                emit(NetworkResult.Error(AppConstant.serverError))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(NetworkResult.Error(AppConstant.serverError))
        }
    }

    override fun addFamilyMemberMedicalDocuments(
        familyMemeberId: RequestBody,
        profile_image: MutableList<MultipartBody.Part?>
    ): Flow<NetworkResult<String>> = flow {
        try {
            val response = api.addFamilyMemberMedicalDocuments(familyMemeberId,profile_image)

            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                        emit(NetworkResult.Success(respBody.get("message").asString))
                    } else {
                        emit(NetworkResult.Error(respBody.get("message").asString))
                    }
                } else {
                    emit(NetworkResult.Error(AppConstant.serverError))
                }
            } else {
                emit(NetworkResult.Error(AppConstant.serverError))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(NetworkResult.Error(AppConstant.serverError))
        }
    }

    override fun getFamilyMemberProfile(id: Int): Flow<NetworkResult<ProfileData>> =flow{
        try {
            val response = api.getFamilyMemberProfile(id)
            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                        val data1 = respBody.getAsJsonObject("data")
                        val data = data1.get("data").asJsonObject
                        val uriList: List<Uri> = data.getAsJsonArray("medical_documents")
                            ?.mapNotNull { element ->
                                element.takeIf { !it.isJsonNull }?.asString
                            }
                            ?.mapNotNull { runCatching { Uri.parse(it) }.getOrNull() }
                            ?: emptyList()
                        val profile = ProfileData(
                            id = data.get("id")?.takeIf { !it.isJsonNull }?.asInt ?: 0,

                            fullName = data.get("full_name")?.takeIf { !it.isJsonNull }?.asString ?: "",

                            contactNumber = data.get("contact_number")?.takeIf { !it.isJsonNull }?.asString ?: "",

                            email = data.get("email")?.takeIf { !it.isJsonNull }?.asString ?: "",

                            dateOfBirth = data.get("dob")?.takeIf { !it.isJsonNull }?.asString ?: "",

                            relation = data.get("relation")?.takeIf { !it.isJsonNull }?.asString ?: "father",

                            gender = data.get("gender")?.takeIf { !it.isJsonNull }?.asString ?: "Select",

                            height = data.get("height")?.takeIf { !it.isJsonNull }?.asString ?: "",

                            weight = data.get("weight")?.takeIf { !it.isJsonNull }?.asString ?: "",

                            profileImage = (data.get("profile_image")?.takeIf { !it.isJsonNull }?.asString
                                 ?: data.get("profile_photo")?.takeIf { !it.isJsonNull }?.asString) ?: "",

                            bloodGroup = data.get("blood_group")?.takeIf { !it.isJsonNull }?.asString ?: "Select",

                            allergies = data.getAsJsonArray("allergies")
                                ?.mapNotNull { it.takeIf { !it.isJsonNull }?.asString?.trim() }
                                ?: emptyList(),

                            emergencyContactName = data.get("emergency_contact_name")
                                ?.takeIf { !it.isJsonNull }?.asString ?: "",

                            emergencyContactPhone = data.get("emergency_contact_number")
                                ?.takeIf { !it.isJsonNull }?.asString ?: "",

                            chronicConditions = data.getAsJsonArray("chronic_condition")
                                ?.mapNotNull { it.takeIf { !it.isJsonNull }?.asString?.trim() }
                                ?: emptyList(),

                            surgicalHistory = data.get("surgical_history")
                                ?.takeIf { !it.isJsonNull }?.asString ?: "",

                            currentMedications = data.getAsJsonArray("current_medications")
                                ?.mapNotNull { it.takeIf { !it.isJsonNull }?.asString?.trim() }
                                ?: emptyList(),

                            currentSupplements = data.getAsJsonArray("current_supplements")
                                ?.mapNotNull { it.takeIf { !it.isJsonNull }?.asString?.trim() }
                                ?: emptyList(),

                            uploadedDocument = data.getAsJsonArray("medical_documents")
                                ?.mapNotNull { it.takeIf { !it.isJsonNull }?.asString }
                                ?: emptyList(),
                            uploadedFiles = uriList
                        )

                        emit(NetworkResult.Success(profile))
                    }
                    else {
                        emit(NetworkResult.Error(respBody.get("message").asString))
                    }
                } else {
                    emit(NetworkResult.Error(AppConstant.serverError))
                }
            } else {
                emit(NetworkResult.Error(AppConstant.serverError))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(NetworkResult.Error(AppConstant.serverError))
        }
    }

   override fun updateFamilyPersonalProfile(
        @Part("family_member_id") familyMemberBody : RequestBody,
        @Part("fullName") nameBody: RequestBody,
        @Part("contactNumber") phoneBody: RequestBody,
        @Part("emailAddress") emailBody: RequestBody,
        @Part("dateOfBirth") dobBody: RequestBody,
        @Part("gender") genderBody: RequestBody,
        @Part("height") heightBody: RequestBody,
        @Part("weight") weightBody: RequestBody,
        @Part profile_image:  MultipartBody.Part?
    ) : Flow<NetworkResult<String>> =flow{
        try {
            val response = api.updateFamilyPersonalProfile(
            familyMemberBody,nameBody,phoneBody,emailBody,dobBody,genderBody,heightBody,weightBody,profile_image
            )

            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                        emit(NetworkResult.Success(respBody.get("message").asString))
                    } else {
                        emit(NetworkResult.Error(respBody.get("message").asString))
                    }
                } else {
                    emit(NetworkResult.Error(AppConstant.serverError))
                }
            } else {
                emit(NetworkResult.Error(AppConstant.serverError))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(NetworkResult.Error(AppConstant.serverError))
        }
    }

    override fun updateFamilyGeneralProfile(
        familyMemberId: Int,
        bloodGroup: String,
        knownAllergies: String,
        contactName: String,
        contactNumber: String
    ): Flow<NetworkResult<String>> =flow{
        try {

            val response = api.updateFamilyGeneralProfile(
                familyMemberId, bloodGroup, knownAllergies,
                contactName, contactNumber
            )

            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                        emit(NetworkResult.Success(respBody.get("message").asString))
                    } else {
                        emit(NetworkResult.Error(respBody.get("message").asString))
                    }
                } else {
                    emit(NetworkResult.Error(AppConstant.serverError))
                }
            } else {
                emit(NetworkResult.Error(AppConstant.serverError))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(NetworkResult.Error(AppConstant.serverError))
        }
    }

   override fun updateFamilyHistoryProfile(
        familyMemberId :Int,
        chronicCondition :String,
        surgicalHistory :String,
        currentMediation :String,
        supplement :String
    ): Flow<NetworkResult<String>> = flow{
       try {
           val response = api.updateFamilyHistoryProfile(
               familyMemberId, chronicCondition, surgicalHistory,
               currentMediation, supplement
           )
           if (response.isSuccessful) {
               val respBody = response.body()
               if (respBody != null) {
                   if (respBody.has("success") && respBody.get("success").asBoolean) {
                       emit(NetworkResult.Success(respBody.get("message").asString))
                   } else {
                       emit(NetworkResult.Error(respBody.get("message").asString))
                   }
               } else {
                   emit(NetworkResult.Error(AppConstant.serverError))
               }
           } else {
               emit(NetworkResult.Error(AppConstant.serverError))
           }
       } catch (e: Exception) {
           e.printStackTrace()
           emit(NetworkResult.Error(AppConstant.serverError))
       }
   }

    override fun updateFamilyMedicalDocuments(
        id: RequestBody,
        profile_image: MutableList<MultipartBody.Part?>
    ) : Flow<NetworkResult<String>> = flow {
      try {
          val response = api.updateFamilyMedicalDocuments(id,profile_image)
          if (response.isSuccessful) {
              val respBody = response.body()
              if (respBody != null) {
                  if (respBody.has("success") && respBody.get("success").asBoolean) {
                      emit(NetworkResult.Success(respBody.get("message").asString))
                  } else {
                      emit(NetworkResult.Error(respBody.get("message").asString))
                  }
              } else {
                  emit(NetworkResult.Error(AppConstant.serverError))
              }
          } else {
              emit(NetworkResult.Error(AppConstant.serverError))
          }
      }
      catch (e: Exception){
          e.printStackTrace()
          emit(NetworkResult.Error(AppConstant.serverError))
      }

    }

    override suspend fun familyMemberList(): Flow<NetworkResult<FamilyMemberResponse>> = flow{
        try {
            val response = api.familyMemberList()
            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                           val data = respBody.get("data").asJsonObject
                        val familyProfile = Gson().fromJson(data, FamilyMemberResponse::class.java)

                        emit(NetworkResult.Success(familyProfile))
                    } else {
                        emit(NetworkResult.Error(respBody.get("message").asString))
                    }
                } else {
                    emit(NetworkResult.Error(AppConstant.serverError))
                }
            } else {
                emit(NetworkResult.Error(AppConstant.serverError))
            }
        }catch (e: Exception){
            e.printStackTrace()
            emit(NetworkResult.Error(AppConstant.serverError))
        }
    }

    override suspend fun getFamilyMemberProfileDetails(familyMemberId: Int): Flow<NetworkResult<FamilyMember>> = flow {
        try{
            val response = api.getFamilyMemberProfileDetails(familyMemberId)
            if(response.isSuccessful){
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                        val data1 = respBody.get("data").asJsonObject
                        val json = data1.get("data").asJsonObject
                        val allergies = json.getArraySafe("allergies")
                        val chronic = json.getArraySafe("chronic_condition")
                        val meds = json.getArraySafe("current_medications")
                        val supplements = json.getArraySafe("current_supplements")
                        val docs = json.getArraySafe("medical_documents")
                        val member = FamilyMember(
                            id = json.getStringSafe("id"),
                            name = json.getStringSafe("full_name"),
                            profileImage = (json.getStringSafe("profile_image").takeIf { it.isNotEmpty() }
                                ?: json.getStringSafe("profile_photo")).let {
                                if (!it.isNullOrEmpty()) AppConstant.IMAGE_BASE_URL+it else ""
                            },
                            contactNumber = json.getStringSafe("contact_number", "--"),
                            email = json.getStringSafe("email", "--"),
                            relation = json.getStringSafe("relationship", "--")
                                .replaceFirstChar { it.uppercase() },
                            dateOfBirth = json.getStringSafe("dob"),
                            gender = json.getStringSafe("gender", "--"),
                            height = json.getStringSafe("height"),
                            weight = json.getStringSafe("weight"),
                            bloodGroup = json.getStringSafe("blood_group", "--"),
                            allergies = allergies.toSafeString(),
                            emergencyContact = json.getStringSafe("emergency_contact_name", "--"),
                            emergencyPhone = json.getStringSafe("emergency_contact_number", "--"),
                            chronicConditions = chronic.toSafeString(),
                            surgicalHistory = json.getStringSafe("surgical_history", "--"),
                            currentMedications = meds.toSafeList(),
                            currentSupplements = supplements.toSafeList(),
                            documents = docs.toSafeDocuments()
                        )
                        emit(NetworkResult.Success(member))
                    } else {
                        emit(NetworkResult.Error(respBody.get("message").asString))
                    }
                } else {
                    emit(NetworkResult.Error(AppConstant.serverError))
                }
            }else{
                emit(NetworkResult.Error(AppConstant.serverError))
            }
        }catch (e: Exception){
            e.printStackTrace()
            emit(NetworkResult.Error(AppConstant.serverError))
        }
    }

    override suspend fun deleteFamilyMember(familyMemberId: Int): Flow<NetworkResult<String>> = flow {
        try {
            val response = api.deleteFamilyMember(familyMemberId)
            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                        emit(NetworkResult.Success(respBody.get("message").asString))
                    } else {
                        emit(NetworkResult.Error(respBody.get("message").asString))
                    }
                } else {
                    emit(NetworkResult.Error(AppConstant.serverError))
                }
            } else {
                emit(NetworkResult.Error(AppConstant.serverError))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(NetworkResult.Error(AppConstant.serverError))
        }
    }

    override suspend fun deleteFamilyProfilePhoto(familyMemberId: Int): Flow<NetworkResult<String>> = flow {
        try {
            val response = api.deleteFamilyProfilePhoto(familyMemberId)
            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                        emit(NetworkResult.Success(respBody.get("message").asString))
                    } else {
                        emit(NetworkResult.Error(respBody.get("message").asString))
                    }
                } else {
                    emit(NetworkResult.Error(AppConstant.serverError))
                }
            } else {
                emit(NetworkResult.Error(AppConstant.serverError))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(NetworkResult.Error(AppConstant.serverError))
        }
    }

    override suspend fun updateFamilyProfilePhoto(
        familyMemberId: RequestBody,
        profileImage: MultipartBody.Part
    ): Flow<NetworkResult<String>> = flow {
        try {
            Log.d("UPDATE_FAMILY_PHOTO", "Repository: starting updateFamilyProfilePhoto upload...")
            val response = api.updateFamilyProfilePhoto(familyMemberId, profileImage)
            Log.d("UPDATE_FAMILY_PHOTO", "Repository: HTTP response code: ${response.code()}")
            if (response.isSuccessful) {
                val respBody = response.body()
                Log.d("UPDATE_FAMILY_PHOTO", "Repository: response body: $respBody")
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                        emit(NetworkResult.Success("Profile picture updated successfully"))
                    } else {
                        val errorMsg = if (respBody.has("message")) respBody.get("message").asString else "Unknown error from response"
                        Log.e("UPDATE_FAMILY_PHOTO", "Repository: API success is false. Message: $errorMsg")
                        emit(NetworkResult.Error(errorMsg))
                    }
                } else {
                    Log.e("UPDATE_FAMILY_PHOTO", "Repository: Response body is null")
                    emit(NetworkResult.Error(AppConstant.serverError))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("UPDATE_FAMILY_PHOTO", "Repository: HTTP error body: $errorBody")
                emit(NetworkResult.Error(AppConstant.serverError))
            }
        } catch (e: Exception) {
            Log.e("UPDATE_FAMILY_PHOTO", "Repository: Exception occurred: ${e.message}", e)
            e.printStackTrace()
            emit(NetworkResult.Error(AppConstant.serverError))
        }
    }



    override suspend fun getUserWithFamilyDetails(): Flow<NetworkResult<MutableList<FamilyModel>>>  = flow{
        try {
            val response = api.getUserWithFamilyDetails()
            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                        val list = mutableListOf<FamilyModel>()
                        val data = respBody.getAsJsonObject("data")
                        // 🔹 USER (Myself)
                        val userObj = if (data.has("userDetails") && data.get("userDetails").isJsonObject) {
                            data.getAsJsonObject("userDetails")
                        } else null

                        userObj?.let {
                            list.add(
                                FamilyModel(
                                    id = it.get("id")?.asInt ?: 0,
                                    name = it.getStringSafe("name"),
                                    relationship = "Myself",
                                    profilePhoto = ""
                                )
                            )
                        }

                        // 🔹 FAMILY LIST
                        val familyArray = data?.getArraySafe("familyDetails")

                        familyArray?.forEach { element ->
                            if (element.isJsonNull) return@forEach
                            val obj = element.asJsonObject
                            list.add(
                                FamilyModel(
                                    id = obj.get("id")?.asInt ?: 0,
                                    name = obj.getStringSafe("name"),
                                    relationship = obj.getStringSafe("relationship"),
                                    profilePhoto = obj.getStringSafe("profile_photo")
                                )
                            )
                        }

                        emit(NetworkResult.Success(list))
                    } else {
                        emit(NetworkResult.Error(respBody.get("message").asString))
                    }
                } else {
                    emit(NetworkResult.Error(AppConstant.serverError))
                }
            } else {
                emit(NetworkResult.Error(AppConstant.serverError))
            }
        }
        catch (e: Exception) {
            e.printStackTrace()
            emit(NetworkResult.Error(AppConstant.serverError))
        }
    }

    override suspend fun getChatResponse(
        familyMemberId: RequestBody?,
        message: RequestBody,
        type: RequestBody,
        chatId: RequestBody?,
        profile_image: MultipartBody.Part?
    ): Flow<NetworkResult<ChatMessage>> = flow {

        try {
            Log.d("ChatAPI", "getChatResponse Request -> " +
                "familyMemberId: ${familyMemberId.readString()}, " +
                "message: ${message.readString()}, " +
                "type: ${type.readString()}, " +
                "chatId: ${chatId.readString()}, " +
                "hasProfileImage: ${profile_image != null}"
            )
            val response = api.getChatResponse(
                familyMemberId,
                message,
                type,
                chatId,
                profile_image
            )
            Log.d("ChatAPI", "getChatResponse Response [code=${response.code()}] -> isSuccessful: ${response.isSuccessful}, body: ${response.body() ?: response.errorBody()?.string()}")

            if (response.isSuccessful) {

                val respBody = response.body()

                if (respBody != null) {

                    if (respBody.has("success") && respBody.get("success").asBoolean) {

                        val dataObj = respBody.getAsJsonObject("data")

                        if (dataObj != null) {

                            val messageId = dataObj.get("message_id")?.asString
                                ?: java.util.UUID.randomUUID().toString()

                            val chatIdValue = dataObj.get("chat_id")?.asInt ?: 0

                            val messageText = dataObj.get("message")?.asString

                            val metaObj = dataObj.getAsJsonObject("meta")
                            val isSeverityHigh = metaObj?.let {
                                it.has("severity") &&
                                        !it.get("severity").isJsonNull &&
                                        it.get("severity").asString.equals("high", ignoreCase = true)
                            } ?: false

                            val chatMessage = ChatMessage(
                                id = messageId,
                                chatId = chatIdValue,
                                text = messageText,
                                severity = isSeverityHigh,
                                isUser = false
                            )

                            emit(NetworkResult.Success(chatMessage))

                        } else {
                            emit(NetworkResult.Error("Data object not found"))
                        }
                    } else {
                        val errorMsg = respBody.get("message")?.asString
                            ?: AppConstant.serverError
                        emit(NetworkResult.Error(errorMsg))
                    }

                } else {
                    emit(NetworkResult.Error(AppConstant.serverError))
                }

            } else {
                emit(NetworkResult.Error(AppConstant.serverError))
            }

        } catch (e: Exception) {
            e.printStackTrace()
            emit(NetworkResult.Error(AppConstant.serverError))
        }
    }

    override suspend fun getPromptQuestions(): Flow<NetworkResult<PromptQuestionResponse>> = flow {
        try {
            Log.d("ChatAPI", "getPromptQuestions Request")
            val response = api.getPromptQuestions()
            Log.d("ChatAPI", "getPromptQuestions Response [code=${response.code()}] -> isSuccessful: ${response.isSuccessful}, body: ${response.body() ?: response.errorBody()?.string()}")
            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    AppConstant.IMAGE_BASE_URL +
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                        val dataObj = respBody.getAsJsonObject("data")
                        if (dataObj != null ) {
                            val promptQuestion = Gson().fromJson(dataObj, PromptQuestionResponse::class.java)
                            val userDetails = dataObj.get("userDetails").asJsonObject
                            val photo = (userDetails.getStringSafe("profile_image").takeIf { it.isNotEmpty() }
                                ?: userDetails.getStringSafe("profile_photo")).takeIf { it.isNotEmpty() }
                            val family = FamilyDetails(
                                id = userDetails.get("id").asInt,
                                name = userDetails.get("name").asString,
                                relationship = "Myself",
                                profile_photo = photo
                            )
                            val updatedFamilyList = listOf(family) + promptQuestion.family_details
                            val updatedResponse = promptQuestion.copy(
                                family_details = updatedFamilyList
                            )
                            emit(NetworkResult.Success(updatedResponse))
                        }
                        else {
                              emit(NetworkResult.Error("Message not found in data"))
                        }

                        //  emit(NetworkResult.Success(respBody.get("message").asString))
                    } else {
                        emit(NetworkResult.Error(respBody.get("message").asString))
                    }
                } else {
                    emit(NetworkResult.Error(AppConstant.serverError))
                }
            } else {
                emit(NetworkResult.Error(AppConstant.serverError))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(NetworkResult.Error(AppConstant.serverError))
        }
    }

    override suspend fun getUserChatHistoryList(): Flow<NetworkResult<MutableList<ChatHistoryItem>>> = flow {
        try {
            Log.d("ChatAPI", "getUserChatHistoryList Request")
            val response = api.getUserChatList()
            Log.d("ChatAPI", "getUserChatHistoryList Response [code=${response.code()}] -> isSuccessful: ${response.isSuccessful}, body: ${response.body() ?: response.errorBody()?.string()}")
            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                        val dataObj = respBody.getAsJsonObject("data")
                        if (dataObj != null ) {
                            val chatHistoryList = dataObj.getAsJsonArray("data")
                             val historyList = mutableListOf<ChatHistoryItem>()

                            chatHistoryList.forEach { element ->
                                try {
                                    val obj = element.asJsonObject
                                    val item = Gson().fromJson(obj, ChatHistoryItem::class.java)

                                    if (item != null) {
                                        historyList.add(item)
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        historyList.reverse()

                            emit(NetworkResult.Success(historyList))
                        }
                        else {
                            emit(NetworkResult.Error("Message not found in data"))
                        }

                        //  emit(NetworkResult.Success(respBody.get("message").asString))
                    } else {
                        emit(NetworkResult.Error(respBody.get("message").asString))
                    }
                } else {
                    emit(NetworkResult.Error(AppConstant.serverError))
                }
            } else {
                emit(NetworkResult.Error(AppConstant.serverError))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(NetworkResult.Error(AppConstant.serverError))
        }
    }

    override suspend fun renameChat(
        chatId: Int,
        title: String
    ) : Flow<NetworkResult<String>> = flow {
        try {
            Log.d("ChatAPI", "renameChat Request -> chatId: $chatId, title: $title")
            val response = api.renameChat(chatId,title)
            Log.d("ChatAPI", "renameChat Response [code=${response.code()}] -> isSuccessful: ${response.isSuccessful}, body: ${response.body() ?: response.errorBody()?.string()}")
            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                        emit(NetworkResult.Success(respBody.get("message").asString))
                    } else {
                        emit(NetworkResult.Error(respBody.get("message").asString))
                    }
                } else {
                    emit(NetworkResult.Error(AppConstant.serverError))
                }
            } else {
                emit(NetworkResult.Error(AppConstant.serverError))
            }
        }
        catch (e: Exception){
            e.printStackTrace()
            emit(NetworkResult.Error(AppConstant.serverError))
        }
    }

    override suspend fun deleteChat(chatId: Int): Flow<NetworkResult<String>> = flow{
        try {
            Log.d("ChatAPI", "deleteChat Request -> chatId: $chatId")
            val response = api.deleteChat(chatId)
            Log.d("ChatAPI", "deleteChat Response [code=${response.code()}] -> isSuccessful: ${response.isSuccessful}, body: ${response.body() ?: response.errorBody()?.string()}")
            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                        emit(NetworkResult.Success(respBody.get("message").asString))
                    } else {
                        emit(NetworkResult.Error(respBody.get("message").asString))
                    }
                } else {
                    emit(NetworkResult.Error(AppConstant.serverError))
                }
            } else {
                emit(NetworkResult.Error(AppConstant.serverError))
            }
        }
        catch (e: Exception){
            e.printStackTrace()
            emit(NetworkResult.Error(AppConstant.serverError))
        }
    }

    override suspend fun getUserFamilyMemberChatList(id:Int): Flow<NetworkResult<MutableList<ChatHistoryItem>>> = flow {
        try {
            Log.d("ChatAPI", "getUserFamilyMemberChatList Request -> familyMemberId: $id")
            val response = api.userFamilyChatList(id)
            Log.d("ChatAPI", "getUserFamilyMemberChatList Response [code=${response.code()}] -> isSuccessful: ${response.isSuccessful}, body: ${response.body() ?: response.errorBody()?.string()}")
            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                        val dataObj = respBody.getAsJsonObject("data")
                        if (dataObj != null ) {
                            val chatHistoryList = dataObj.getAsJsonArray("data")
                            val historyList = mutableListOf<ChatHistoryItem>()

                            chatHistoryList.forEach { element ->
                                try {
                                    val obj = element.asJsonObject
                                    val item = Gson().fromJson(obj, ChatHistoryItem::class.java)

                                    if (item != null) {
                                        historyList.add(item)
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                            historyList.reverse()

                            emit(NetworkResult.Success(historyList))
                        }
                        else {
                            emit(NetworkResult.Error("Message not found in data"))
                        }

                        //  emit(NetworkResult.Success(respBody.get("message").asString))
                    } else {
                        emit(NetworkResult.Error(respBody.get("message").asString))
                    }
                } else {
                    emit(NetworkResult.Error(AppConstant.serverError))
                }
            } else {
                emit(NetworkResult.Error(AppConstant.serverError))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(NetworkResult.Error(AppConstant.serverError))
        }
    }

    override suspend fun getChatMessage(id:Int): Flow<NetworkResult<MutableList<ChatMessage>>> =flow{
        try {
            Log.d("ChatAPI", "getChatMessage Request -> chatId: $id")
            val response = api.getChatMessages(id)
            Log.d("ChatAPI", "getChatMessage Response [code=${response.code()}] -> isSuccessful: ${response.isSuccessful}, body: ${response.body() ?: response.errorBody()?.string()}")
            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                          val data = respBody.get("data").asJsonObject
                           if(data != null){
                               val dataArray = data.getAsJsonArray("data")
                               val messages = mutableListOf<ChatMessage>()

                               dataArray.forEach { item ->

                                   val obj = item.asJsonObject

                                   val meta = if (obj.has("meta") && !obj.get("meta").isJsonNull) obj.getAsJsonObject("meta")
                                       else null

                                   val docsPath =
                                       if (obj.has("docs_path") && !obj.get("docs_path").isJsonNull)
                                           obj.get("docs_path").asString
                                       else null

                                   val docsType =
                                       if (obj.has("docs_type") && !obj.get("docs_type").isJsonNull)
                                           obj.get("docs_type").asString.lowercase()
                                       else ""

                                   val imageList = mutableListOf<Uri>()
                                   val pdfList = mutableListOf<PdfData>()

                                   docsPath?.let { path ->

                                       val fileUrl = AppConstant.IMAGE_BASE_URL + path

                                       when (docsType) {

                                           "jpg", "jpeg", "png", "webp" -> {
                                               imageList.add(Uri.parse(fileUrl))
                                           }

                                           "pdf" -> {
                                               pdfList.add(
                                                   PdfData(
                                                       name = path.substringAfterLast("/"),
                                                       uri = Uri.parse(fileUrl)
                                                   )
                                               )
                                           }
                                       }
                                   }

                                   // Sirf HIGH par true
                                   val severity = meta?.let {
                                       it.has("severity") &&
                                               !it.get("severity").isJsonNull &&
                                               it.get("severity").asString.equals("high", ignoreCase = true)
                                   } ?: false

                                   val likeDislikeStatus = when {
                                       obj.has("like_dislike_status") && !obj.get("like_dislike_status").isJsonNull -> {
                                           val status = obj.get("like_dislike_status").asString
                                           if (status == "1") 1 else if (status == "2") 2 else 2
                                       }
                                       obj.has("rating") && !obj.get("rating").isJsonNull -> {
                                           obj.get("rating").asInt
                                       }
                                       else -> 0
                                   }

                                   val chatMessage = ChatMessage(
                                       id = obj.get("id").asString,
                                       text = obj.get("message").asString,
                                       isUser = if (meta != null) {
                                           false
                                       } else {
                                           obj.get("role").asString.equals("user", true)
                                       },
                                       images = imageList,
                                       pdfs = pdfList,
                                        severity = severity,
                                       chatId = obj.get("chat_id").asInt,
                                       rating = likeDislikeStatus,
                                       isRated = likeDislikeStatus != 0
                                   )

                                   messages.add(chatMessage)
                               }

                               emit(NetworkResult.Success(messages))
                           }
                    } else {
                        emit(NetworkResult.Error(respBody.get("message").asString))
                    }
                } else {
                    emit(NetworkResult.Error(AppConstant.serverError))
                }
            } else {
                emit(NetworkResult.Error(AppConstant.serverError))
            }
        }
        catch (e: Exception){
            e.printStackTrace()
            emit(NetworkResult.Error(AppConstant.serverError))
        }
    }

    override suspend fun downloadHealthReportPdf(chatId: Int): Flow<NetworkResult<String>> =flow{
        try {
            Log.d("ChatAPI", "downloadHealthReportPdf Request -> chatId: $chatId")
            val response = api.downloadHealthReportPdf(chatId)
            Log.d("ChatAPI", "downloadHealthReportPdf Response [code=${response.code()}] -> isSuccessful: ${response.isSuccessful}, body: ${response.body() ?: response.errorBody()?.string()}")
            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                        val data = respBody.get("data").asJsonObject
                        if(data != null){
                         val pdfUrl = if(data.has("pdf_url") &&
                                     data.get("pdf_url").isJsonNull == false
                             ){
                             data.get("pdf_url").asString
                         } else ""

                            emit(NetworkResult.Success(pdfUrl))
                        }
                    } else {
                        emit(NetworkResult.Error(respBody.get("message").asString))
                    }
                } else {
                    emit(NetworkResult.Error(AppConstant.serverError))
                }
            } else {
                emit(NetworkResult.Error(AppConstant.serverError))
            }
        }
        catch (e: Exception){
            e.printStackTrace()
            emit(NetworkResult.Error(AppConstant.serverError))
        }
    }

    override suspend fun switchChat(chatId: Int): Flow<NetworkResult<String>> = flow {
        try {
            Log.d("ChatAPI", "switchChat Request -> chatId: $chatId")
            val response = api.switchChat(chatId)
            Log.d("ChatAPI", "switchChat Response [code=${response.code()}] -> isSuccessful: ${response.isSuccessful}, body: ${response.body() ?: response.errorBody()?.string()}")
            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                         emit(NetworkResult.Success(respBody.get("message").asString))
                    } else {
                        emit(NetworkResult.Error(respBody.get("message").asString))
                    }
                } else {
                    emit(NetworkResult.Error(AppConstant.serverError))
                }
            } else {
                emit(NetworkResult.Error(AppConstant.serverError))
            }
        }
        catch (e: Exception){
            e.printStackTrace()
            emit(NetworkResult.Error(AppConstant.serverError))
        }
    }

    override fun deleteProfilePhoto(): Flow<NetworkResult<String>> = flow {
        try {
            val response = api.deleteProfilePhoto()
            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                        emit(NetworkResult.Success("Profile photo deleted successfully."))
                    } else {
                        emit(NetworkResult.Error(respBody.get("message").asString))
                    }
                } else {
                    emit(NetworkResult.Error(AppConstant.serverError))
                }
            } else {
                emit(NetworkResult.Error(AppConstant.serverError))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(NetworkResult.Error(AppConstant.serverError))
        }
    }.flowOn(Dispatchers.IO)

    override fun responseLikeDislike(
        chatMessageId: String,
        likeDislikeStatus: String
    ): Flow<NetworkResult<String>> = flow {
        try {
            val response = api.responseLikeDislikeRequest(chatMessageId, likeDislikeStatus)
            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                        val message = if (respBody.has("message")) respBody.get("message").asString else "Success"
                        emit(NetworkResult.Success(message))
                    } else {
                        val message = if (respBody.has("message")) respBody.get("message").asString else AppConstant.serverError
                        emit(NetworkResult.Error(message))
                    }
                } else {
                    emit(NetworkResult.Error(AppConstant.serverError))
                }
            } else {
                emit(NetworkResult.Error(AppConstant.serverError))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(NetworkResult.Error(AppConstant.serverError))
        }
    }.flowOn(Dispatchers.IO)

    override fun viewSummary(chatId: Int): Flow<NetworkResult<com.bussiness.curemegptapp.apimodel.chatModel.SummaryData?>> = flow {
        emit(NetworkResult.Loading())
        try {
            val response = api.viewSummaryRequest(chatId.toString())
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    emit(NetworkResult.Success(body.data))
                } else {
                    emit(NetworkResult.Error(body?.message ?: AppConstant.serverError))
                }
            } else {
                emit(NetworkResult.Error(AppConstant.serverError))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(NetworkResult.Error(AppConstant.serverError))
        }
    }.flowOn(Dispatchers.IO)

    override fun deleteAccount(feedback: String): Flow<NetworkResult<String>> = flow {
        emit(NetworkResult.Loading())
        try {
            val response = api.deleteAccountRequest(feedback)
            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                        val message = if (respBody.has("message")) respBody.get("message").asString else "Account deleted successfully."
                        emit(NetworkResult.Success(message))
                    } else {
                        val message = if (respBody.has("message")) respBody.get("message").asString else AppConstant.serverError
                        emit(NetworkResult.Error(message))
                    }
                } else {
                    emit(NetworkResult.Error(AppConstant.serverError))
                }
            } else {
                emit(NetworkResult.Error(AppConstant.serverError))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(NetworkResult.Error(AppConstant.serverError))
        }
    }.flowOn(Dispatchers.IO)


    fun JsonObject.getStringSafe(key: String, default: String = ""): String {
        return if (has(key) && !get(key).isJsonNull) {
            get(key).asString
        } else default
    }

    fun JsonObject.getArraySafe(key: String): JsonArray? {
        return if (has(key) && get(key).isJsonArray) {
            getAsJsonArray(key)
        } else null
    }

    fun JsonArray?.toSafeString(): String {
        if (this == null || size() == 0) return "--"

        return mapNotNull {
            if (!it.isJsonNull) it.asString else null
        }.takeIf { it.isNotEmpty() }
            ?.joinToString(", ")
            ?: "--"
    }

    fun JsonArray?.toSafeList(): List<String> {
        if (this == null || size() == 0) return listOf("--")

        val list = mapNotNull {
            if (!it.isJsonNull) it.asString else null
        }

        return if (list.isEmpty()) listOf("--") else list
    }

    fun JsonArray?.toSafeDocuments(): List<Document> {
        if (this == null) return emptyList()

        return mapIndexedNotNull { index, element ->
            if (element.isJsonNull) return@mapIndexedNotNull null

            val rawUrl = element.asString
            // Construct absolute URL: if the server already returns a full URL, use it as-is;
            // otherwise prepend the base URL.
            val absoluteUrl = if (rawUrl.startsWith("http://") || rawUrl.startsWith("https://")) {
                rawUrl
            } else {
                AppConstant.IMAGE_BASE_URL + rawUrl
            }

            Document(
                id = "doc$index",
                fileName = rawUrl.substringAfterLast("/", "document_$index"),
                fileUrl = absoluteUrl,
                fileType = rawUrl.substringAfterLast(".", "pdf")
            )
        }
    }

    private suspend fun <T : BaseResponse> safeApiCall(
        apiCall: suspend () -> Response<T>
    ): Resource<T> {
        return try {
            val response = apiCall()
            when {
                !response.isSuccessful ->
                    Resource.Error("Server error ${response.code()}")
                response.body() == null ->
                    Resource.Error(Messages.RESPONSE_BODY_ERROR)
                response.body()?.success == false ->
                    Resource.Error(response.body()?.message ?: "Operation failed")
                else ->
                    Resource.Success(response.body()!!)
            }
        } catch (e: Exception) {
            Log.e("API_ERROR", "Network call failed", e)
            Resource.Error(e.localizedMessage ?: "Something went wrong")
        }
    }

    private fun RequestBody?.readString(): String {
        if (this == null) return "null"
        return try {
            val buffer = okio.Buffer()
            this.writeTo(buffer)
            buffer.readUtf8()
        } catch (e: Exception) {
            "error"
        }
    }

}



