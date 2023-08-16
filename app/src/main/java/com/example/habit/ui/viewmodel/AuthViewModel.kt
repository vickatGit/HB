package com.example.habit.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habit.data.network.model.SignupModel.SignupView
import com.example.habit.domain.UseCases.AuthUseCase.LoginUseCase
import com.example.habit.domain.UseCases.AuthUseCase.SignupUseCase
import com.example.habit.domain.models.Login.LoginView
import com.example.habit.ui.activity.LoginActivity.LoginUiState
import com.example.habit.ui.activity.SignupActivity.SignupUiState
import com.example.habit.ui.mapper.AuthMapper.LoginMapper
import com.example.habit.ui.mapper.AuthMapper.SignupMapper
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginMapper: LoginMapper,
    private val signupMapper: SignupMapper,
    private val loginUseCase: LoginUseCase,
    private val signupUseCase: SignupUseCase
) : ViewModel() {

    private val TAG: String?="Auth"

    private var _login = MutableStateFlow<LoginUiState>(LoginUiState.Nothing)
    val login=_login.asStateFlow()

    private var _signup = MutableStateFlow<SignupUiState>(SignupUiState.Nothing)
    val signup=_signup.asStateFlow()

    fun doLogin(loginView: LoginView){
        viewModelScope.launch {
            _login.update { LoginUiState.Loading }
            try {
                loginUseCase(loginMapper.fromLogin(loginView)).collect{ login->
                    Log.e(TAG, "doLogin: ${Gson().toJson(login)}" )
                    if(login.success){
                        _login.update { LoginUiState.Success(login.message) }
                    }else{
                        _login.update { LoginUiState.Error(login.message) }
                    }
                }
            }catch (e:Exception){
                Log.e(TAG, "doLogin: Exception ${Gson().toJson(e)}" )
                _login.update { LoginUiState.Error(e.message+"") }
            }

        }
    }

    fun doSignup(signupView: SignupView){
        viewModelScope.launch {
            _signup.update { SignupUiState.Loading }
            try {
                signupUseCase(signupMapper.fromSignup(signupView)).collect{ login->
                    Log.e(TAG, "doSignup: ${Gson().toJson(login)}" )
                    if(login.success){
                        _signup.update { SignupUiState.Success(login.message) }
                    }else{
                        _signup.update { SignupUiState.Error(login.message) }
                    }
                }
            }catch (e:Exception){
                Log.e(TAG, "doLogin: Exception ${Gson().toJson(e)}" )
                _signup.update { SignupUiState.Error(e.message+"") }
            }

        }
    }

}