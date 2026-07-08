package com.bussiness.curemegptapp.viewmodel.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bussiness.curemegptapp.repository.NetworkResult
import com.bussiness.curemegptapp.repository.Repository
import com.bussiness.curemegptapp.util.LoaderManager
import com.bussiness.curemegptapp.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AboutUsViewModel  @Inject constructor(private val repository: Repository,
                                            private val sessionManager: SessionManager) : ViewModel(){



    val _privacyPolicy = MutableStateFlow<String>("")
    val privacyPolicy = _privacyPolicy

    init {
        getPrivacyPolicy()
    }

    fun getPrivacyPolicy() {
        viewModelScope.launch {
            LoaderManager.show()
            repository.getAccountPrivacyPolicy().collectLatest {
                when (it) {
                    is NetworkResult.Success -> {
                        LoaderManager.hide()
                        _privacyPolicy.value = it.data.toString()

                    }

                    is NetworkResult.Error -> {
                        LoaderManager.hide()

                    }

                    else -> {

                    }
                }
            }
        }
    }
}