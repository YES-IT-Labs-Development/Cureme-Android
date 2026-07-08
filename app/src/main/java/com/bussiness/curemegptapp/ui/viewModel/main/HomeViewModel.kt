package com.bussiness.curemegptapp.ui.viewModel.main

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bussiness.curemegptapp.apimodel.homemodel.Data
import com.bussiness.curemegptapp.apimodel.homemodel.Family
import com.bussiness.curemegptapp.apimodel.homemodel.FamilyX
import com.bussiness.curemegptapp.repository.Repository
import com.bussiness.curemegptapp.repository.Resource
import com.bussiness.curemegptapp.util.LoaderManager
import com.bussiness.curemegptapp.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: Repository,
                                        private val sessionManager: SessionManager) : ViewModel() {



    private val _uiStateHome = MutableStateFlow<Data?>(null)
    val uiStateHome = _uiStateHome.asStateFlow()

    private var originalRecommendedSteps: List<String> = emptyList()

    private fun updateRecommendedStepsForSelectedProfile(data: Data) {
        val selectedProfile = data.healthList?.firstOrNull { it.isSelected == true }
            ?: data.healthList?.firstOrNull()

        val steps = mutableListOf<String>()

        // 1. Add all medications of the selected profile
        selectedProfile?.active_alerts?.medications?.forEach { med ->
            val name = med.medication_name
            if (!name.isNullOrBlank()) {
                steps.add("$name medication reminder")
            } else {
                steps.add("Medication reminder")
            }
        }

        // 2. Add all appointments of the selected profile
        selectedProfile?.active_alerts?.appointments?.forEach { appt ->
            val desc = appt.description
            if (!desc.isNullOrBlank()) {
                steps.add(desc.removeSuffix("."))
            } else {
                steps.add("Appointment reminder")
            }
        }

        // 3. Add any original recommended steps from the API
        originalRecommendedSteps.forEach { step ->
            if (!steps.contains(step)) {
                steps.add(step)
            }
        }

        data.recommended_next_steps = steps
    }

    fun getHomeRequest() {
        viewModelScope.launch {
            repository.getHomeRequest()
                .collectLatest { result ->
                    when (result) {
                        is Resource.Loading -> {
                            LoaderManager.show()
                        }
                        is Resource.Success -> {
                            LoaderManager.hide()
                            val healthList: MutableList<Family>  = mutableListOf()
                            val needAttentionList: MutableList<FamilyX>  = mutableListOf()
                            healthList.clear()
                            needAttentionList.clear()
                            result.data.data?.let { data ->
                                data.things_need_attention?.myself?.forEach { symptom ->
                                    needAttentionList.add(
                                        FamilyX(
                                            name = sessionManager.getUserName(),
                                            symptoms = mutableListOf(symptom)
                                        )
                                    )
                                }
                                data.things_need_attention?.family?.let { family ->
                                    needAttentionList.addAll(family)
                                }
                                // Merge custom alerts carried from Case Chat
                                val chatAlerts = sessionManager.getChatAlerts()
                                chatAlerts.forEach { alert ->
                                    if (needAttentionList.none { it.symptoms?.contains(alert) == true }) {
                                        needAttentionList.add(
                                            FamilyX(
                                                name = sessionManager.getUserName(),
                                                symptoms = mutableListOf(alert)
                                            )
                                        )
                                    }
                                }
                                data.members_details?.myself?.let { myself ->
                                    healthList.add(Family(
                                        id = 0,
                                        name = (myself.name?:"" )+ " (Myself)",
                                        dob = myself.dob?:"",
                                        isSelected =true,
                                        profile_image = myself.profile_image?:"",
                                        active_alerts = myself.active_alerts,
                                        last_appointment_days_ago = myself.last_appointment_days_ago))
                                }
                                data.members_details?.family?.let { familyList->
                                    healthList.addAll(familyList)
                                }
                                data.healthList= healthList
                                data.needAttentionList= needAttentionList
                                
                                // Store original recommended steps and update based on default (myself) profile
                                originalRecommendedSteps = data.recommended_next_steps ?: emptyList()
                                updateRecommendedStepsForSelectedProfile(data)

                                _uiStateHome.value = data
                            }
                        }
                        is Resource.Error -> {
                            LoaderManager.hide()
                        }
                        Resource.Idle -> Unit
                    }
                }
        }
    }

    fun onProfileSelected(selectedFamily: Family) {
        val currentData = _uiStateHome.value ?: return
        val updatedHealthList = currentData.healthList?.map { family ->
            family.copy(
                isSelected = family.id == selectedFamily.id
            )
        }?.toMutableList()

        val updatedData = currentData.copy(
            healthList = updatedHealthList
        )
        updateRecommendedStepsForSelectedProfile(updatedData)
        _uiStateHome.value = updatedData
    }







    // UI State
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    // For mood selection
    private val _selectedMood = mutableStateOf("")
    val selectedMood: State<String> = _selectedMood

    // Initial hardcoded data (API aane ke baad ye remove/replace hoga)
    private val initialMedications = listOf("Lisinopril 10mg", "Vitamin D")
    private val initialAllergies = listOf("Penicillin", "Shellfish")
    private val initialSteps = listOf(
        "Set a reminder for your blood pressure medication",
        "Schedule your annual checkup",
        "Complete emergency contact information."
    )
    private val initialAlerts = listOf(
        "Blood pressure medication reminder",
        "Annual checkup due"
    )

    private   val attentionItems = listOf(
        Triple("Tooth Pain Symptoms Detected", "For: James Logan",true),
        Triple("Overdue Dental Cleaning", "For: Rosy Logan",true)


    )

    // Things needing attention data
    private val initialAttentionItems = listOf(
        AttentionItem(
            id = 1,
            title = "Tooth Pain Symptoms Detected",
            subtitle = "For: James Logan",
            isUrgent = true,
            forPerson = "James Logan"
        ),
        AttentionItem(
            id = 2,
            title = "Overdue Dental Cleaning",
            subtitle = "For: Rosy Logan",
            isUrgent = true,
            forPerson = "Rosy Logan"
        ),
        AttentionItem(
            id = 3,
            title = "Annual Physical Due",
            subtitle = "For: James Logan",
            isUrgent = false,
            forPerson = "James Logan"
        )
    )

    init {
        // Load initial data
        loadHomeData()
    }

    private fun loadHomeData() {
        _uiState.value = HomeUiState(
            userName = "James",
            profileCompletion = 0.5f,
            medications = initialMedications,
            allergies = initialAllergies,
            recommendedSteps = initialSteps,
            alerts = initialAlerts,
           // attentionItems = attentionItems,
            attentionItems = initialAttentionItems
        )
    }

    fun updateMood(mood: String) {
        _selectedMood.value = mood
        // Yahan API call kar sakte hain mood update karne ke liye
    }

    fun removeLocalAlert(symptom: String) {
        sessionManager.removeChatAlert(symptom)
        getHomeRequest()
    }

    fun scheduleAttentionItem(itemId: Int) {
        viewModelScope.launch {
            // Future API implementation for scheduling
            // repository.scheduleAttentionItem(itemId)

            // For now, just update UI state
            val currentItems = _uiState.value.attentionItems
            val updatedItems = currentItems.map { item ->
                if (item.id == itemId) {
                    item.copy(isScheduled = true)
                } else {
                    item
                }
            }

            _uiState.value = _uiState.value.copy(
                attentionItems = updatedItems
            )
        }
    }

    fun markAttentionItemAsResolved(itemId: Int) {
        viewModelScope.launch {
            // Future API implementation
            // repository.markAsResolved(itemId)

            // Update UI state
            val currentItems = _uiState.value.attentionItems
            val updatedItems = currentItems.filter { it.id != itemId }

            _uiState.value = _uiState.value.copy(
                attentionItems = updatedItems
            )
        }
    }

    fun addNewAttentionItem(title: String, subtitle: String, isUrgent: Boolean, forPerson: String) {
        val newItem = AttentionItem(
            id = (_uiState.value.attentionItems.maxOfOrNull { it.id } ?: 0) + 1,
            title = title,
            subtitle = subtitle,
            isUrgent = isUrgent,
            forPerson = forPerson
        )

        val updatedItems = _uiState.value.attentionItems + newItem

        _uiState.value = _uiState.value.copy(
            attentionItems = updatedItems
        )
    }

    fun refreshData() {
        viewModelScope.launch {
            // Future API implementation:
            // try {
            //     val homeData = repository.getHomeData()
            //     _uiState.value = homeData
            // } catch (e: Exception) {
            //     // Handle error
            // }
        }
    }
}

// Data class for Home Screen UI State
data class HomeUiState(
    val userGreating: String = "Hello",
    val userName: String = "James",
    val profileCompletion: Float = 0f,
    val medications: List<String> = emptyList(),
    val allergies: List<String> = emptyList(),
    val recommendedSteps: List<String> = emptyList(),
    val alerts: List<String> = emptyList(),
    val attentionItems: List<AttentionItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

// Data class for attention items
data class AttentionItem(
    val id: Int,
    val title: String,
    val subtitle: String,
    val isUrgent: Boolean,
    val forPerson: String,
    val isScheduled: Boolean = false,
    val createdAt: String = "", // For future API integration
    val dueDate: String = "" // For future API integration
)


