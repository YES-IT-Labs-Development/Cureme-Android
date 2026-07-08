package com.bussiness.curemegptapp.viewmodel.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bussiness.curemegptapp.apimodel.StaticPage
import com.bussiness.curemegptapp.repository.NetworkResult
import com.bussiness.curemegptapp.repository.Repository
import com.bussiness.curemegptapp.util.LoaderManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    private val _staticPages = MutableStateFlow<List<StaticPage>>(emptyList())
    val staticPages: StateFlow<List<StaticPage>> = _staticPages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        getAllStaticPages()
    }

    fun getAllStaticPages() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            LoaderManager.show()
            repository.getAllStaticPages().collectLatest {
                when (it) {
                    is NetworkResult.Success -> {
                        LoaderManager.hide()
                        _staticPages.value = it.data ?: emptyList()
                        _isLoading.value = false
                    }
                    is NetworkResult.Error -> {
                        LoaderManager.hide()
                        _errorMessage.value = it.message ?: "Failed to fetch pages"
                        _isLoading.value = false
                    }
                    is NetworkResult.Loading -> {
                        // Optional loading handling
                    }
                    else -> {}
                }
            }
        }
    }

    fun getParticularStaticPage(slug: String, onSuccess: (StaticPage) -> Unit) {
        viewModelScope.launch {
            LoaderManager.show()
            repository.getParticularStaticPage(slug).collectLatest { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        LoaderManager.hide()
                        result.data?.let { onSuccess(it) }
                    }
                    is NetworkResult.Error -> {
                        LoaderManager.hide()
                        _errorMessage.value = result.message ?: "Failed to fetch page data"
                    }
                    is NetworkResult.Loading -> {
                        // Managed by manual LoaderManager.show()
                    }
                    else -> {}
                }
            }
        }
    }
}
