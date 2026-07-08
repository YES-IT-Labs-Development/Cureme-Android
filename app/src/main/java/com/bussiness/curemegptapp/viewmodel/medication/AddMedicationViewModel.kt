package com.bussiness.curemegptapp.viewmodel.medication

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bussiness.curemegptapp.apimodel.scheduleAppointment.FamilyModel
import com.bussiness.curemegptapp.repository.NetworkResult
import com.bussiness.curemegptapp.repository.Repository
import com.bussiness.curemegptapp.util.LoaderManager
import com.bussiness.curemegptapp.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class AddMedicationViewModel @Inject constructor(
    private val repository: Repository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private var _memberOption = MutableStateFlow(listOf<String>())
    val memberOption = _memberOption
    private var memberRealData = mutableListOf<FamilyModel>()
    var selectedId :String? =null


    init {
        getFamilyMembers()
    }

    fun updateForWhomId(value: String) {
        memberRealData.forEach {
            Log.d("TESTING_VIEW_Model",it.name.trim()+" "+value.trim())
            if (it.name.trim() == value.trim()) {
              selectedId = if(!it.name.toString().equals("My Self"))it.id.toString() else null
                return@forEach
            }
        }

    }


    fun getFamilyMembers() {
            viewModelScope.launch {
                repository.getFamilyMembersList().collect { result ->
                    when (result) {
                        is com.bussiness.curemegptapp.repository.NetworkResult.Success -> {
                            val data = result.data ?: emptyList()


                            val familyList = data?.toMutableList()
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

                        is com.bussiness.curemegptapp.repository.NetworkResult.Error -> {
                            // Handle error
                        }

                        else -> {
                            // Handle loading or other states if necessary
                        }
                    }
                }
            }
        }

    fun getSelectedMemberId(selectedName: String): Int? {
            return memberRealData.find { it.name == selectedName }?.id
    }


    fun addMedication(
        forWhomId: RequestBody?, medicationType: RequestBody, medicationName: RequestBody,
        dosage: RequestBody, frequency: RequestBody, days: RequestBody?, startDate: RequestBody,
        endDate: RequestBody, notes: RequestBody, reminderStatus: RequestBody, reminderTimes: List<MultipartBody.Part>,
        prescriptionDocs: MultipartBody.Part?,
        onSuccess :()->Unit,
        onError :(msg:String)->Unit
    ) {
        viewModelScope.launch {
            LoaderManager.show()
            repository.addMedication(
                forWhomId, medicationType, medicationName, dosage, frequency, days, startDate,
                endDate, notes, reminderStatus, reminderTimes, prescriptionDocs
            ).collectLatest {
                when (it) {
                    is NetworkResult.Success -> {
                        LoaderManager.hide()
                        Log.d("TESTING_ADD_MEDICATION", it.data.toString())
                        onSuccess()
                    }

                    is NetworkResult.Error -> {
                        LoaderManager.hide()
                        Log.d("TESTING_ADD_MEDICATION", "Error: " + it.message.toString())
                        onError(it.message.toString())
                    }

                    else -> {
                        Log.d("TESTING_ADD_MEDICATION", "Loading...")
                    }
                }
            }
        }
    }

}