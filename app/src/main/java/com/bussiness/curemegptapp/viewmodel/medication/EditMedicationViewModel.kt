package com.bussiness.curemegptapp.viewmodel.medication

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bussiness.curemegptapp.apimodel.medication.MedicationDetailUiState
import com.bussiness.curemegptapp.apimodel.medication.MedicationItemDetail
import com.bussiness.curemegptapp.apimodel.scheduleAppointment.FamilyModel
import com.bussiness.curemegptapp.repository.NetworkResult
import com.bussiness.curemegptapp.repository.Repository
import com.bussiness.curemegptapp.util.LoaderManager
import com.bussiness.curemegptapp.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class EditMedicationViewModel @Inject constructor(
    private val repository: Repository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(MedicationDetailUiState())
    val uiState: StateFlow<MedicationDetailUiState> = _uiState

    private var _memberOption = MutableStateFlow(listOf<String>())
    val memberOption = _memberOption
    private var memberRealData = mutableListOf<FamilyModel>()
    var selectedId :String? =null

    fun getMedicationDetail(medicationId: Int) {

        viewModelScope.launch {

            repository.getMedicationDetails(medicationId).collectLatest { result ->

                when (result) {

                    is NetworkResult.Loading -> {
                        _uiState.value = _uiState.value.copy(
                            isMedicationLoading = true,
                            error = null
                        )
                    }

                    is NetworkResult.Success -> {

                        _uiState.value = _uiState.value.copy(
                            isMedicationLoading = false,
                            medication = result.data,
                            error = null
                        )
                    }

                    is NetworkResult.Error -> {

                        _uiState.value = _uiState.value.copy(
                            isMedicationLoading = false,
                            error = result.message
                        )
                    }
                }
            }
        }
    }

    fun getFamilyMembers() {
        viewModelScope.launch {
            repository.getFamilyMembersList().collect { result ->
                when (result) {

                    is NetworkResult.Loading ->{
                        _uiState.value = _uiState.value.copy(
                            isFamilyLoading = true,
                            error = null
                        )
                    }

                    is NetworkResult.Success -> {
                        val data = result.data ?: emptyList()
                        val familyList = data.toMutableList()
                        val dummy = FamilyModel(
                            id = -1,
                            name = "My Self",
                            relationship = "Myself",
                            profilePhoto = "https://example.com/father.jpg"
                        )
                        familyList.add(dummy)
                        memberRealData = (familyList ?: emptyList()).toMutableList()
                        val familyNames = familyList?.map { it.name } ?: emptyList()
                        _memberOption.value = familyNames
                        _uiState.value = _uiState.value.copy(
                            isFamilyLoading = false,
                            error = null
                        )
                    }

                    is com.bussiness.curemegptapp.repository.NetworkResult.Error -> {
                        // Handle error

                        _uiState.value = _uiState.value.copy(
                            isFamilyLoading = false,
                            error = null
                        )
                    }


                    else -> {
                        // Handle loading or other states if necessary
                    }
                }
            }
        }
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


    fun updateMedication(
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
        prescriptionDocs: MultipartBody.Part?,
        onSuccess :()->Unit,
        onError :(msg:String) ->Unit
    ){
        viewModelScope.launch {
            LoaderManager.show()
            repository.updateMedication(
                medicationId,forWhomId,medicationType,medicationName,dosage,frequency,days, startDate,endDate,notes,
                reminderStatus,reminderTimes,prescriptionDocs
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

}

