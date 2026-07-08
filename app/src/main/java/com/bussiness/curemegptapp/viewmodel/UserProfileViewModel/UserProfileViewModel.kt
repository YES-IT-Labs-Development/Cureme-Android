package com.bussiness.curemegptapp.viewmodel.UserProfileViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bussiness.curemegptapp.apimodel.profilemodel.Data
import com.bussiness.curemegptapp.repository.Repository
import com.bussiness.curemegptapp.ui.viewModel.main.FamilyMember
import com.bussiness.curemegptapp.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val repository: Repository,
    private val sessionManager: SessionManager
) : ViewModel(){




}
