package com.bussiness.curemegptapp.viewmodel.reportviewmodel

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bussiness.curemegptapp.apimodel.personalmodel.User
import com.bussiness.curemegptapp.apimodel.reportdetailmodel.DataReportDetail
import com.bussiness.curemegptapp.apimodel.reportmodel.Data
import com.bussiness.curemegptapp.apimodel.scheduleAppointment.FamilyModel
import com.bussiness.curemegptapp.repository.NetworkResult
import com.bussiness.curemegptapp.repository.Repository
import com.bussiness.curemegptapp.repository.Resource
import com.bussiness.curemegptapp.ui.uistate.PersonalUiState
import com.bussiness.curemegptapp.util.DownloadUtils
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
class ReportViewModel @Inject constructor(private val repository: Repository,
                                          private val sessionManager: SessionManager) : ViewModel() {


    private val _uiState = MutableStateFlow<List<Data>>(emptyList())
    val uiState = _uiState.asStateFlow()

    private val _memberOptions = MutableStateFlow<List<String>>(emptyList())
    val memberOptions = _memberOptions.asStateFlow()

    init {
        getFamilyMembers()
    }

    fun getFamilyMembers() {
        viewModelScope.launch {
            repository.getFamilyMembersList().collectLatest { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        val data = result.data ?: emptyList()
                        val familyNames = data.mapNotNull { it.name }.toMutableList()
                        if (!familyNames.contains("My Self")) {
                            familyNames.add(0, "My Self")
                        }
                        _memberOptions.value = familyNames
                    }
                    is NetworkResult.Error -> {
                        _memberOptions.value = listOf("My Self")
                    }
                    else -> Unit
                }
            }
        }
    }

    fun getUserName(): String {
        return sessionManager.getUserName()
    }

    fun getReportListRequest() {
        viewModelScope.launch {
            repository.reportListRequest()
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


    // This api and UiState only for HealthReportDetailsScreen
    private val _uiStateDetails =
        MutableStateFlow<DataReportDetail?>(null)

    val uiStateDetails = _uiStateDetails.asStateFlow()
    fun getReportDetailsRequest(id: String) {
        viewModelScope.launch {
            repository.getReportDetailsRequest(id)
                .collectLatest { result ->
                    when (result) {
                        is Resource.Loading -> {
                            LoaderManager.show()
                        }
                        is Resource.Success -> {
                            LoaderManager.hide()
                            val response = result.data.data
                            response?.let {
                                _uiStateDetails.value = it
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


    fun downloadReportPdf(context : Context, id:Int, error: (String) -> Unit) {
        viewModelScope.launch {
            LoaderManager.show()
            repository.downloadHealthReportPdf(id).collectLatest { result->
                when(result){
                    is NetworkResult.Success ->{
                        LoaderManager.hide()

                        val rawUrl = result.data ?: ""
                        val absolutePdfUrl = if (rawUrl.startsWith("http://") || rawUrl.startsWith("https://")) {
                            rawUrl
                        } else {
                            com.bussiness.curemegptapp.util.AppConstant.IMAGE_BASE_URL + rawUrl
                        }
                        val fileName = "Health_Report_${id}.pdf"
                        DownloadUtils.downloadFile(
                            context,
                            absolutePdfUrl,
                            fileName)
                    }
                    is NetworkResult.Error ->{
                        LoaderManager.hide()
                        error(result.message ?: "Unknown error")
                    }
                    else ->{

                    }
                }
            }
        }
    }

    fun shareReportPdf(context: Context, id: Int, error: (String) -> Unit) {
        viewModelScope.launch {
            LoaderManager.show()
            repository.downloadHealthReportPdf(id).collectLatest { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        LoaderManager.hide()
                        val rawUrl = result.data ?: ""
                        if (rawUrl.isNotEmpty()) {
                            val absolutePdfUrl = if (rawUrl.startsWith("http://") || rawUrl.startsWith("https://")) {
                                rawUrl
                            } else {
                                com.bussiness.curemegptapp.util.AppConstant.IMAGE_BASE_URL + rawUrl
                            }
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, absolutePdfUrl)
                            }
                            context.startActivity(Intent.createChooser(intent, "Share Report via"))
                        } else {
                            error("Downloadable link is empty")
                        }
                    }
                    is NetworkResult.Error -> {
                        LoaderManager.hide()
                        error(result.message ?: "Failed to get shareable link")
                    }
                    else -> {}
                }
            }
        }
    }

}