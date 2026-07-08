package com.bussiness.curemegptapp.viewmodel.alertviewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bussiness.curemegptapp.apimodel.alertmodel.Data
import com.bussiness.curemegptapp.repository.Repository
import com.bussiness.curemegptapp.repository.Resource
import com.bussiness.curemegptapp.util.LoaderManager
import com.bussiness.curemegptapp.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.toList


@HiltViewModel
class AlertViewModel @Inject constructor(private val repository: Repository,
                                         private val sessionManager: SessionManager) : ViewModel() {

    // This api and UiState only for HealthReportScreen
    private val _uiState = MutableStateFlow<List<Data>>(emptyList())
    val uiState = _uiState.asStateFlow()

    fun getAlertListRequest() {
        viewModelScope.launch {
            repository.getAlertListRequest()
                .collectLatest { result ->
                    when (result) {
                        is Resource.Loading -> {
                            LoaderManager.show()
                        }
                        is Resource.Success -> {
                            LoaderManager.hide()
                            result.data.data?.let { data ->
                                _uiState.value = data.toList()
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


    fun updateReadStatus(id: String) {
        viewModelScope.launch {
            repository.updateAlertReadStatus(id)
                .collectLatest { result ->
                    when (result) {
                        is Resource.Loading -> {
                            LoaderManager.show()
                        }
                        is Resource.Success -> {
                            LoaderManager.hide()
                            // Update local ui state
                            _uiState.value = _uiState.value.map { item ->
                                if (item.reference_id.toString() == id) {
                                    item.copy(
                                        appointment_complete_status =
                                            if (item.appointment_complete_status == "pending") {
                                                "complete"
                                            } else {
                                                "pending"
                                            }
                                    )
                                } else {
                                    item
                                }
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


}