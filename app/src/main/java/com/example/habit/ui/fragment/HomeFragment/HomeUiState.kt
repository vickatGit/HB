package com.example.habit.ui.fragment.HomeFragment

sealed class HomeUiState{
    data class HomeData(val homeUiData: com.example.habit.data.network.model.UiModels.HomePageModels.HomeData?):HomeUiState()
    object Loading:HomeUiState()
    data class Error(val err:String):HomeUiState()
    object Nothing:HomeUiState()
}