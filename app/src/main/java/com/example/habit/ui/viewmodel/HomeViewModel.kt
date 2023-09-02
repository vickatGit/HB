package com.example.habit.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habit.domain.UseCases.UiUseCases.HomeUseCase
import com.example.habit.ui.fragment.HomeFragment.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeUseCase: HomeUseCase
) : ViewModel() {
    private var _uiState= MutableStateFlow<HomeUiState>(HomeUiState.Nothing)
    val uiState = _uiState.asStateFlow()

    fun getHomeData(){
        try {
            _uiState.update { HomeUiState.Loading }
            viewModelScope.launch {
                _uiState.update { HomeUiState.HomeData(homeUseCase()) }
            }
        }catch (e:Exception){
            Log.e("TAG", "getHomeData: ${e.printStackTrace()}", )
            _uiState.update { HomeUiState.Error("$e.message") }
        }
    }
}