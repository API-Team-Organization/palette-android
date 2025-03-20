package com.api.palette.presentation.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.api.palette.domain.model.LoginRequest
import com.api.palette.domain.usecase.AuthUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authUseCases: AuthUseCases
) : ViewModel() {

    private val _loginSuccess = MutableLiveData<String>()
    val loginSuccess: LiveData<String> get() = _loginSuccess

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val (response, token) = authUseCases.login(LoginRequest(email, password))
                _loginSuccess.postValue(token ?: "")
            } catch (e: Exception) {
                _errorMessage.postValue(e.message ?: "알 수 없는 에러")
            }
        }
    }
}
