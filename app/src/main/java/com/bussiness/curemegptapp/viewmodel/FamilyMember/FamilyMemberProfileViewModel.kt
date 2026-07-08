package com.bussiness.curemegptapp.viewmodel.FamilyMember

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bussiness.curemegptapp.apimodel.familyProfile.FamilyMember
import com.bussiness.curemegptapp.apimodel.familyProfile.FamilyMemberUi
import com.bussiness.curemegptapp.apimodel.familyProfile.FamilyUiState
import com.bussiness.curemegptapp.repository.NetworkResult
import com.bussiness.curemegptapp.repository.Repository
import com.bussiness.curemegptapp.util.AppConstant
import com.bussiness.curemegptapp.util.LoaderManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlin.collections.map

@HiltViewModel
class FamilyMemberProfileViewModel @Inject constructor(private val repository: Repository) :
    ViewModel() {

    private val _members = MutableStateFlow<List<FamilyMemberUi>>(emptyList())
    val members: StateFlow<List<FamilyMemberUi>> = _members

    fun String?.clean(): String? {
        return this
            ?.takeIf { it.isNotBlank() && !it.equals("NaN!", true) && !it.equals("Nan!", true) }
    }

    fun calculateAge(dob: String?): Int {
        return try {

            val cleanDob = dob.clean() ?: return 0

            val formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy")

            val birthDate = LocalDate.parse(cleanDob, formatter)

            val today = LocalDate.now()

            if (birthDate.isAfter(today)) return 0

            Period.between(birthDate, today).years

        } catch (e: Exception) {
            0
        }
    }


    private val _uiState = MutableStateFlow(FamilyUiState())
    val uiState: StateFlow<FamilyUiState> = _uiState.asStateFlow()

    fun updateFamilyData(
        members: Int,
        medicationCount: Int,
        appointmentCount: Int
    ) {
        _uiState.value = FamilyUiState(
            totalFamilyMembers = members,
            totalFamilyMedicationCount = medicationCount,
            totalFamilyAppointmentCount = appointmentCount
        )
    }

    fun FamilyMember.toUi(): FamilyMemberUi {
        return FamilyMemberUi(
            id = id,
            name = fullName.clean() ?: "Unknown",
            age = calculateAge(dob),
            relationship = relationship.clean() ?: "Unknown",
            appointments = "$completedAppointmentCount/$appointmentCount",
            medications = "$completedMedicationCount/$medicationCount",
            imageUrl = if (!profileImage.isNullOrBlank()) {
                val cleaned = profileImage.clean() ?: ""
                val url = if (cleaned.startsWith("http://") || cleaned.startsWith("https://")) {
                    cleaned
                } else {
                    val base = AppConstant.IMAGE_BASE_URL.removeSuffix("/")
                    val path = if (cleaned.startsWith("/")) cleaned else "/$cleaned"
                    base + path
                }
                if (url == AppConstant.IMAGE_BASE_URL || url == "${AppConstant.IMAGE_BASE_URL}/") null else url
            } else {
                null
            }
        )
    }


    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedFilter = MutableStateFlow("All Members")
    val selectedFilter: StateFlow<String> = _selectedFilter

    // 🔥 Combined filtered list
    val filteredMembers: StateFlow<List<FamilyMemberUi>> =
        combine(_members, _searchQuery, _selectedFilter) { members, query, filter ->

            members.filter { member ->

                val matchesSearch = query.isBlank() ||
                        member.name.contains(query, true) ||
                        member.relationship.contains(query, true)

                val matchesFilter = when (filter) {
                    "All Members" -> true
                    "Children" -> (member.age ?: 0) < 18
                    "Adults" -> (member.age ?: 0) in 18..64
                    "Seniors" -> (member.age ?: 0) >= 65
                    "Friends" -> member.relationship == "Friend"
                    "Relatives" -> member.relationship in listOf(
                        "Son", "Daughter", "Mother", "Father", "Spouse"
                    )

                    else -> true
                }

                matchesSearch && matchesFilter
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    fun updateSearch(query: String) {
        _searchQuery.value = query
    }

    fun updateFilter(filter: String) {
        _selectedFilter.value = filter
    }

    // 🔥 Call API here
    fun loadMembers(apiList: List<FamilyMember>) {
        _members.value = apiList.map { it.toUi() }
    }


    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        fetchFamilyMembers()
    }

    fun fetchFamilyMembers() {
        viewModelScope.launch {
            _isLoading.value = true
            viewModelScope.launch {
                Log.d(
                    "FamilyData",
                    "Inside calling api of familyData"
                )
                LoaderManager.show()
                repository.familyMemberList().collectLatest {
                    when (it) {
                        is NetworkResult.Success -> {
                            LoaderManager.hide()

                            it.data?.let {

                                Log.d(
                                    "FamilyData",
                                    "Members: ${it.totalFamilyMembers}, Medications: ${it.totalFamilyMedicationCount}, Appointments: ${it.totalFamilyAppointmentCount}"
                                )

                                updateFamilyData(
                                    it.totalFamilyMembers,
                                    it.totalFamilyMedicationCount,
                                    it.totalFamilyAppointmentCount
                                )
                            }

                            loadMembers(it.data?.familyMembers ?: emptyList())
                            _isLoading.value = false
                        }

                        is NetworkResult.Error -> {
                            LoaderManager.hide()
                            Log.d(
                                "FamilyData",
                                "Inside error of familyData"
                            )
                            _isLoading.value = false

                        }

                        else -> {

                        }
                    }
                }
            }
        }
    }

    fun deleteProfile(id:Int,onSucess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            LoaderManager.show()
            repository.deleteFamilyMember(id).collectLatest {
                when(it){
                    is NetworkResult.Success ->{
                        LoaderManager.hide()
                        _members.update { list ->
                            list.filter { it.id != id }
                        }
                        onSucess()
                    }
                    is NetworkResult.Error ->{
                        LoaderManager.hide()
                        onError(it.message ?: "Unknown error")
                    }
                    else ->{
                    }
                }
            }
        }

    }


}