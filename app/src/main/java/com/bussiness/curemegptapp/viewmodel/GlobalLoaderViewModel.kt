package com.bussiness.curemegptapp.viewmodel

import androidx.lifecycle.ViewModel
import com.bussiness.curemegptapp.util.LoaderManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GlobalLoaderViewModel @Inject constructor() : ViewModel() {
    val isLoading = LoaderManager.isLoading
}
