package com.api.palette.presentation.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.api.palette.domain.model.RegisterRequest
import com.api.palette.domain.usecase.AuthUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authUseCases: AuthUseCases
) : ViewModel() {
    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val birthDate = MutableLiveData<String>()
    val username = MutableLiveData<String>()

    private val _registerSuccess = MutableLiveData<Boolean>()
    val registerSuccess: LiveData<Boolean> get() = _registerSuccess

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun register() {
        viewModelScope.launch {
            try {
                val req = RegisterRequest(
                    email = email.value ?: "",
                    password = password.value ?: "",
                    birthDate = birthDate.value ?: "",
                    username = username.value ?: ""
                )
                authUseCases.register(req)
                _registerSuccess.postValue(true)
            } catch (e: Exception) {
                _errorMessage.postValue(e.message ?: "회원가입 오류")
                _registerSuccess.postValue(false)
            }
        }
    }
}
