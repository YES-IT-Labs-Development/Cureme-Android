package com.bussiness.curemegptapp.viewmodel.appointmentViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bussiness.curemegptapp.apimodel.getAppointmentList.AppointmentData
import com.bussiness.curemegptapp.apimodel.scheduleAppointment.AppointmentTypeModel
import com.bussiness.curemegptapp.apimodel.scheduleAppointment.FamilyModel
import com.bussiness.curemegptapp.repository.NetworkResult
import com.bussiness.curemegptapp.repository.Repository
import com.bussiness.curemegptapp.util.CommonUtils
import com.bussiness.curemegptapp.util.LoaderManager
import com.bussiness.curemegptapp.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RescheduleViewModel @Inject constructor(
    private val repository: Repository,
    private val sessionManager: SessionManager
) : ViewModel() {



    private var _memberOption = MutableStateFlow(listOf<String>())
    val memberOption = _memberOption

    private var appointmentTypeRealData = mutableListOf<AppointmentTypeModel>()
    private var memberRealData = mutableListOf<FamilyModel>()

    private var _appointmentTypeOption = MutableStateFlow(listOf<String>())
    val appointmentTypeOption = _appointmentTypeOption

    private var _appointmentData = MutableStateFlow(AppointmentData())
    val appointmentData = _appointmentData

    fun getAppointmentDetails(appointmentId: Int) {

        viewModelScope.launch {
            LoaderManager.show()
            repository.getScheduleAppointmentDetails(appointmentId.toString()).collectLatest {
               when(it){
                   is NetworkResult.Success ->{
                       LoaderManager.hide()
                       val result = it.data
                       val time = result?.time?.let {
                           CommonUtils.convertToAmPm(it)
                       }?:""

                       _appointmentData.value =
                           result?.copy(time = time) ?: AppointmentData()
                   }
                   is NetworkResult.Error ->{
                       LoaderManager.hide()

                   }
                   else -> {

                   }
               }
            }
        }

    }

    fun rescheduleAppointment(
        appointmentId: Int,
        date: String,
        time: String,
        reminder: String,
        onSuccess :()->Unit,
        onError :(msg:String) ->Unit
    ){
        val timeConverted = CommonUtils.convertTo24HourFormat(time)
        Log.d("Testing_Time","Time Converted is"+timeConverted)
        viewModelScope.launch {
            LoaderManager.show()
            repository.rescheduleAppointment(
                appointmentId,date, timeConverted,reminder
            ).collectLatest {
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


    fun loadInitialData(appointmentId: Int) {
        viewModelScope.launch {
            Log.d("TESTING_ID", "Appointment ID: $appointmentId")
            LoaderManager.show()
            try {

                val appointmentResult = repository.getAppointmentType().first()

                if (appointmentResult is NetworkResult.Success) {

                    val appointmentList = appointmentResult.data
                    appointmentTypeRealData = (appointmentList ?: emptyList()).toMutableList()

                    val appointmentNames = appointmentList?.map { it.name } ?: emptyList()
                    _appointmentTypeOption.value = appointmentNames
                }

                val familyResult = repository.getFamilyMembersList().first()

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
                }

                getAppointmentDetails(appointmentId)

            } catch (e: Exception) {
                Log.e("TESTING_VIEWMODEL", "Error: ${e.message}")
            } finally {
                LoaderManager.hide()
            }
        }
    }



}