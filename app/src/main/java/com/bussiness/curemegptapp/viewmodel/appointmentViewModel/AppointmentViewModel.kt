package com.bussiness.curemegptapp.viewmodel.appointmentViewModel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bussiness.curemegptapp.apimodel.scheduleAppointment.AppointmentRequest
import com.bussiness.curemegptapp.apimodel.scheduleAppointment.AppointmentTypeModel
import com.bussiness.curemegptapp.apimodel.scheduleAppointment.FamilyModel
import com.bussiness.curemegptapp.repository.NetworkResult
import com.bussiness.curemegptapp.repository.Repository
import com.bussiness.curemegptapp.util.CommonUtils.convertTo24HourFormat
import com.bussiness.curemegptapp.util.LoaderManager
import com.bussiness.curemegptapp.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class AppointmentViewModel  @Inject constructor(
    private val repository: Repository,
    private val sessionManager: SessionManager
) : ViewModel() {

    init {
        loadInitialData()
    }

    private var _memberOption = MutableStateFlow(listOf<String>())
    val memberOption = _memberOption
    private var memberRealData = mutableListOf<FamilyModel>()

    private var appointmentTypeRealData = mutableListOf<AppointmentTypeModel>()


    private var _appointmentTypeOption = MutableStateFlow(listOf<String>())
    val appointmentTypeOption = _appointmentTypeOption

    private val _appointmentRequest = MutableStateFlow(AppointmentRequest())
    val appointmentRequest: StateFlow<AppointmentRequest> = _appointmentRequest

    fun updateForWhomId(value: String) {
        memberRealData.forEach {
            Log.d("TESTING_VIEW_Model",it.name.trim()+" "+value.trim())
            if (it.name.trim() == value.trim()) {
                _appointmentRequest.value =
                    _appointmentRequest.value.copy(forWhomId = if(!it.name.toString().equals("My Self"))it.id.toString() else null)
                return@forEach
            }
        }

    }

    fun updateAppointmentTypeId(value: String) {
        appointmentTypeRealData.forEach {
            if (it.name == value) {
                _appointmentRequest.value =
                    _appointmentRequest.value.copy(appointmentTypeId = it.id.toString())
                return@forEach
            }
        }

    }

    fun updateDescription(value: String) {
        _appointmentRequest.value = _appointmentRequest.value.copy(description = value)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateDate(value: String) {
        val inputFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy")
        val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val date = LocalDate.parse(value, inputFormatter)
        val formattedDate = date.format(outputFormatter)
        _appointmentRequest.value = _appointmentRequest.value.copy(date = value)
    }

    fun updateTime(value: String) {
        _appointmentRequest.value = _appointmentRequest.value.copy(time =  convertTo24HourFormat(value))
    }

    fun updatePreferredDoctor(value: String) {
        _appointmentRequest.value = _appointmentRequest.value.copy(preferredDoctor = value)
    }

    fun updatePreferredClinic(value: String) {
        _appointmentRequest.value = _appointmentRequest.value.copy(preferredClinic = value)
    }

    fun updateAppointmentReminder(value: String) {
        _appointmentRequest.value = _appointmentRequest.value.copy(appointmentReminder = value)
    }

    private var recommendedChatId: String? = null

    fun updateRecommendedChatId(value: String?) {
        recommendedChatId = value
    }

    fun addScheduleAppointment(
       onSuccess :()-> Unit,
       onError : (message:String)->Unit
    ){
        viewModelScope.launch {
            LoaderManager.show()
            Log.d("TESTING_VIEW_Model", "whomid is "+appointmentRequest.value.forWhomId)
            Log.d("TESTING_VIEW_Model", "recommendedChatId inside addScheduleAppointment is $recommendedChatId")
            repository.addScheduleAppointment(
                appointmentRequest.value.forWhomId,
                             appointmentRequest.value.appointmentTypeId,
                             appointmentRequest.value.description,
                             appointmentRequest.value.date,
                             appointmentRequest.value.time,
                             appointmentRequest.value.preferredDoctor,
                             appointmentRequest.value.preferredClinic,
                             appointmentRequest.value.appointmentReminder,
                             recommendedChatId).collectLatest {
                when(it){
                    is NetworkResult.Success ->{
                        LoaderManager.hide()
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

    fun loadInitialData() {

        viewModelScope.launch {
            LoaderManager.show()
            try {
                val appointmentResult = repository.getAppointmentType().first()
                Log.d("TESTING_VIEWMODEL", "Appointment Result: $appointmentResult")
                if (appointmentResult is NetworkResult.Success) {

                    val appointmentList = appointmentResult.data

                    appointmentTypeRealData =
                        (appointmentList ?: emptyList()).toMutableList()

                    val appointmentNames =
                        appointmentList?.map { it.name } ?: emptyList()

                    _appointmentTypeOption.value = appointmentNames

                    Log.d("TESTING_VIEWMODEL", "Appointment Size ${appointmentTypeRealData.size}")
                }

                val familyResult = repository.getFamilyMembersList().first()
                Log.d("TESTING_VIEWMODEL", "Family Result: $familyResult")
                if (familyResult is NetworkResult.Success) {

                    val familyList = familyResult.data?.toMutableList()
                    val dummy = FamilyModel(
                        id = -1,
                        name = "My Self",
                        relationship = "Myself",
                        profilePhoto = "https://example.com/father.jpg"
                    )
                    familyList?.add(dummy)

                    memberRealData = (familyList ?: emptyList()).toMutableList()

                    val familyNames = familyList?.map { it.name } ?: emptyList()

                    _memberOption.value = familyNames

                    Log.d(
                        "TESTING_VIEWMODEL",
                        "Family Size ${memberRealData.size}"
                    )
                }else{
                    val familyList = familyResult.data?.toMutableList()
                    val dummy = FamilyModel(
                        id = -1, name = "My Self", relationship = "Myself",
                        profilePhoto = "https://example.com/father.jpg")
                    Log.d("TESTING_VIEWMODEL", "Family Size ${memberRealData.size}")

                    familyList?.add(dummy)

                    memberRealData = (familyList ?: emptyList()).toMutableList()

                    val familyNames = familyList?.map { it.name } ?: emptyList()

                    _memberOption.value = familyNames
                }

            } catch (e: Exception) {
                Log.e("TESTING_VIEWMODEL", "Error: ${e.message}")
            } finally {
                LoaderManager.hide()
            }
        }
    }
}

//    fun loadInitialData() {
//
//        viewModelScope.launch {
//            LoaderManager.show()
//            combine(
//                repository.getAppointmentType(),
//                repository.getFamilyMembersList()
//            ) {
//                appointmentResult, familyResult ->
//                Pair(appointmentResult, familyResult)
//            }.collect { result ->
//
//                    Log.d("TESTING_VIEWMODEL","HERE INSIDE INT RESULT")
//                val appointmentResult = result.first
//                val familyResult = result.second
//
//
//                Log.d("TESTING_VIEWMODEL", "Appointment Result: $appointmentResult")
//                Log.d("TESTING_VIEWMODEL", "Family Result: $familyResult")
//
//                when {
//                    appointmentResult is NetworkResult.Success &&
//                            familyResult is NetworkResult.Success -> {
//                        LoaderManager.hide()
//                        val appointmentList = appointmentResult.data
//                        val familyList = familyResult.data
//                        appointmentTypeRealData = (appointmentList ?: emptyList()).toMutableList()
//                        memberRealData = (familyList ?: emptyList()).toMutableList()
//                        val appointmentNames = appointmentList?.map { it.name } ?: emptyList()
//                        Log.d("TESTING_VIEWMODEL","Appointment Size is"+appointmentTypeRealData.size+" "+"name is"+appointmentNames.size)
//
//                        _appointmentTypeOption.value = appointmentNames
//                        val familyNames = familyList?.map { it.name } ?: emptyList()
//                        _memberOption.value = familyNames
//
//                        Log.d("TESTING_VIEWMODEL","Family Size is"+memberRealData.size+" "+"name is"+familyNames.size)
//
//
//
//                    }
//                    appointmentResult is NetworkResult.Error -> {
//                        LoaderManager.hide()
//                    }
//
//                    familyResult is NetworkResult.Error -> {
//                        LoaderManager.hide()
//                    }
//                }
//            }
//        }
//    }

