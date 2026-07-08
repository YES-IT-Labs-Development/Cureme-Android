package com.bussiness.curemegptapp.viewmodel.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bussiness.curemegptapp.apimodel.QuestionAnswer
import com.bussiness.curemegptapp.data.model.ExpandableItem
import com.bussiness.curemegptapp.repository.NetworkResult
import com.bussiness.curemegptapp.repository.Repository
import com.bussiness.curemegptapp.util.LoaderManager
import com.bussiness.curemegptapp.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FaqViewModel @Inject constructor(private val repository: Repository,
                                       private val sessionManager: SessionManager) : ViewModel(){

                                           init {
                                               getFAQ()
                                           }
    private val _faqList = MutableStateFlow<List<ExpandableItem>>(emptyList())
    val faqList: StateFlow<List<ExpandableItem>> = _faqList

    fun getFAQ(){
        viewModelScope.launch {
            LoaderManager.show()
            repository.getFAQ().collect { result ->
                when(result){
                    is NetworkResult.Loading -> {
                        // Handle loading state
                    }
                    is NetworkResult.Success -> {
                        LoaderManager.hide()
                        val faqList: List<QuestionAnswer> = result.data ?: emptyList()
                         setFaqFromApi(faqList)
                        // Handle success state with faqList
                    }
                    is NetworkResult.Error -> {
                        LoaderManager.hide()
                        val errorMessage: String = result.message ?: "Unknown error"
                        // Handle error state with errorMessage
                    }
                }
            }
        }

    }


    fun setFaqFromApi(apiList: List<QuestionAnswer>) {

        _faqList.value = apiList.mapIndexed { index, item ->

            ExpandableItem(
                id = item.id ?: index,
                title = item.question ?: "",
                content = item.answer ?: "",
                isExpanded = index == 0   // first item expanded if you want
            )

        }
    }

    fun toggleItem(toggledItem: ExpandableItem) {

        _faqList.value = _faqList.value.map { currentItem ->
            if (currentItem.id == toggledItem.id) {
                currentItem.copy(isExpanded = !currentItem.isExpanded)
            } else {
                currentItem.copy(isExpanded = false)
            }
        }

    }

}