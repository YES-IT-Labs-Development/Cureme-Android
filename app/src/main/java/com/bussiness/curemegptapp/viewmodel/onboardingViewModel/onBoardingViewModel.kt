package com.bussiness.curemegptapp.viewmodel.onboardingViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bussiness.curemegptapp.apimodel.OnBoardingModel.OnboardingItem
import com.bussiness.curemegptapp.apimodel.OnBoardingModel.OnboardingPage
import com.bussiness.curemegptapp.apimodel.personalmodel.User
import com.bussiness.curemegptapp.repository.NetworkResult
import com.bussiness.curemegptapp.repository.Repository
import com.bussiness.curemegptapp.repository.Resource
import com.bussiness.curemegptapp.util.LoaderManager
import com.bussiness.curemegptapp.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class onBoardingViewModel @Inject constructor(
    private val repository: Repository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _pages = MutableStateFlow<List<OnboardingPage>>(emptyList())
    val pages: StateFlow<List<OnboardingPage>> = _pages

    init {
        loadPages()
    }

    private fun loadPages() {
        viewModelScope.launch {
            try {
                 repository.onBoardingData() .collectLatest { result ->
                     when (result) {
                         is Resource.Loading -> {
                             LoaderManager.show()

                         }
                         is Resource.Success -> {


                             Log.d("OnBoarding", "Success: ${result.data}")

                             LoaderManager.hide()
                             result.data.data?.data?.let {
                                 setData(it)
                             }

                         }

                         is Resource.Error -> {
                             LoaderManager.hide()
                             Log.e("OnBoarding", "Error: ${result.message ?: "Unknown error"}")
                         }
                         Resource.Idle -> Unit
                     }



                 }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun setData(items: List<OnboardingItem>) {

        _pages.value = items.map { item ->
            OnboardingPage(
                imageUrl = item.image,
                title = item.heading ?: "",
                description = item.description ?: ""
            )
        }

    }


}