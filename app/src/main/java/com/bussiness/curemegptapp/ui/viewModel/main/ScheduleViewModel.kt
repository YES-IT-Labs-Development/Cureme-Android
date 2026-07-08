package com.bussiness.curemegptapp.ui.viewModel.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bussiness.curemegptapp.R
import com.bussiness.curemegptapp.apimodel.getAppointmentList.AppointmentItem
import com.bussiness.curemegptapp.apimodel.medication.Medication
import com.bussiness.curemegptapp.apimodel.medication.MedicationItem
import com.bussiness.curemegptapp.apimodel.medication.MedicationTime
import com.bussiness.curemegptapp.apimodel.scheduleAppointment.FamilyModel
import com.bussiness.curemegptapp.repository.NetworkResult
import com.bussiness.curemegptapp.repository.Repository

import com.bussiness.curemegptapp.util.LoaderManager
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    var uiState by mutableStateOf(ScheduleUiState())
        private set

    private var _memberOption = MutableStateFlow<List<String>>(emptyList())

    val memberOption = _memberOption

    private var memberRealData = mutableListOf<FamilyModel>()


    private var loadingCount = 0

    private fun startLoading() {
        loadingCount++
        uiState = uiState.copy(isLoading = true)
    }

    private fun stopLoading() {
        loadingCount--
        if (loadingCount <= 0) {
            loadingCount = 0
            uiState = uiState.copy(isLoading = false)
        }
    }



    fun loadAllData() {
        loadAppointments()
        loadMedications()
        getFamilyMembers()
    }


    fun loadAppointments() {
        viewModelScope.launch {
            repository.getAppointmentList().collectLatest { result ->

                when (result) {

                    is NetworkResult.Loading -> startLoading()

                    is NetworkResult.Success -> {
                        val list = result.data?.map { it.toUIModel() } ?: emptyList()

                        uiState = uiState.copy(
                            appointmentList = list
                        )
                        stopLoading()
                    }

                    is NetworkResult.Error -> {
                        uiState = uiState.copy(error = result.message)
                        stopLoading()
                    }
                }
            }
        }
    }


    fun loadMedications() {
        viewModelScope.launch {
            repository.getMedicationList().collectLatest { result ->

                when (result) {
                    is NetworkResult.Loading -> startLoading()
                    is NetworkResult.Success -> {
                        val list = result.data?.map { it.toMedicationUI() } ?: emptyList()
                        uiState = uiState.copy(
                            medicationList = list
                        )
                        stopLoading()
                    }

                    is NetworkResult.Error -> {
                        uiState = uiState.copy(error = result.message)
                        stopLoading()
                    }
                }
            }
        }
    }


    public fun getFamilyMembers() {
        viewModelScope.launch {
            repository.getUserWithFamilyDetails().collectLatest { result ->

                when (result) {

                    is NetworkResult.Loading -> startLoading()

                    is NetworkResult.Success -> {
                        val data = result.data ?: emptyList()
                        val familyList = data.toMutableList()
                        memberRealData = familyList
                        _memberOption.value = familyList.map { it.name }
                        stopLoading()
                    }

                    is NetworkResult.Error -> {
                        uiState = uiState.copy(error = result.message)
                        stopLoading()
                    }
                }
            }
        }
    }

    fun markAppointmentComplete(
        id: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {

            startLoading()

            repository.markAppointmentComplete(id).collectLatest { result ->

                when (result) {

                    is NetworkResult.Success -> {
                        onSuccess()
                        loadAppointments() // refresh
                        stopLoading()
                    }

                    is NetworkResult.Error -> {
                        onError(result.message ?: "Unknown error")
                        stopLoading()
                    }

                    else -> {}
                }
            }
        }
    }


    fun deleteMedication(
        medicationId: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {

            startLoading()

            repository.deleteMedication(medicationId).collectLatest { result ->

                when (result) {

                    is NetworkResult.Success -> {

                        val updatedList =
                            uiState.medicationList.filter { it.id != medicationId }

                        uiState = uiState.copy(
                            medicationList = updatedList
                        )

                        onSuccess()
                        stopLoading()
                    }

                    is NetworkResult.Error -> {
                        onError(result.message ?: "Error")
                        stopLoading()
                    }

                    else -> {}
                }
            }
        }
    }


    fun deleteAppointment(
        id: Int,
        onError: (String) -> Unit,
        onSuccess: () -> Unit

    ) {
        viewModelScope.launch {

            startLoading()

            repository.deleteAppointment(id).collectLatest { result ->

                when (result) {

                    is NetworkResult.Success -> {

                        val updatedList =
                            uiState.appointmentList.filter { it.id != id }

                        uiState = uiState.copy(
                            appointmentList = updatedList
                        )

                        onSuccess()
                        stopLoading()
                    }

                    is NetworkResult.Error -> {
                        onError(result.message ?: "Error")
                        stopLoading()
                    }

                    else -> {}
                }
            }
        }
    }

    fun getSummary(
        chatId: Int,
        onSuccess: (com.bussiness.curemegptapp.apimodel.chatModel.SummaryData?) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            repository.viewSummary(chatId).collectLatest { result ->
                when (result) {
                    is NetworkResult.Loading -> {
                        LoaderManager.show()
                    }
                    is NetworkResult.Success -> {
                        LoaderManager.hide()
                        result.data?.let { onSuccess(it) }
                    }
                    is NetworkResult.Error -> {
                        LoaderManager.hide()
                        onError(result.message ?: "Failed to get summary")
                    }
                }
            }
        }
    }


    private fun MedicationItem.toMedicationUI(): Medication {

        val patient = if (medication_for_whom == "self") {
            user?.name ?: ""
        } else {
            family_member?.full_name ?: ""
        }

        val timesList = reminder_time?.map {
            MedicationTime(time = it, isTaken = false)
        } ?: emptyList()

        return Medication(
            id = id,
            title = "$medication_name ($dosage)",
            patientName = patient,
            medicationType = medication_type,
            frequency = frequency,
            days = days,
            times = timesList,
            startDate = start_date,
            endDate = end_date,
            instructions = notes ?: "",
            isVisibleItem = reminder_status == 1
        )
    }

}
data class AppointmentUIModel(
    val title: String,
    val id :Int,
    val doctor: String,
    val patientName: String,
    val date: String,
    val time: String,
    val location: String,
    val description: String,
    val icon: Int,
    val list : MutableList<String> = mutableListOf(),
    val clinicalName :String,
    val isVisibleItem: Boolean = true,
    val completeStatus :Int =0,
    val chatId: Int? = null
)

data class ScheduleUiState(
    val isLoading: Boolean = false,
    val appointmentList: List<AppointmentUIModel> = emptyList(),
    val medicationList: List<Medication> = emptyList(),
    val error: String? = null
)



fun AppointmentItem.toUIModel(): AppointmentUIModel {

        return AppointmentUIModel(
            title = appointment_type?.name ?: "Appointment",
            doctor = preferred_doctor,
            patientName = if (appointment_for_whom.equals("self",true)  ) {
                user?.name ?: ""
            } else {
                family_member?.full_name ?: ""
            },
            date = date,
            time = time,
            location = preferred_clinic,
            description = description,
            icon = R.drawable.ic_medical_bag_icon,
            isVisibleItem =recommended_chat_id?.let {
                if(it > 0) true else false
            }?: false ,
            id = id,
            clinicalName = preferred_clinic,
            completeStatus = complete_status,
            chatId = recommended_chat_id
        )

}