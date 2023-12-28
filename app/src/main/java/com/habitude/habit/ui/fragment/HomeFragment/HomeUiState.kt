package com.habitude.habit.ui.fragment.HomeFragment

sealed class HomeUiState{
    data class HomeData(val homeUiData: com.habitude.habit.data.network.model.UiModels.HomePageModels.HomeData?):HomeUiState()
    object Loading:HomeUiState()
    data class Error(val err:String):HomeUiState()
    object Nothing:HomeUiState()
}