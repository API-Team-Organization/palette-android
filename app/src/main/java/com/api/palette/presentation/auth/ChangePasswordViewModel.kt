package com.api.palette.presentation.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.api.palette.domain.usecase.AuthUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val authUseCases: AuthUseCases
) : ViewModel() {

    private val _updateSuccess = MutableLiveData<Boolean>()
    val updateSuccess: LiveData<Boolean> get() = _updateSuccess

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun changePassword(token: String, beforePassword: String, afterPassword: String) {
        viewModelScope.launch {
            try {
                authUseCases.changePassword(token, beforePassword, afterPassword)
                _updateSuccess.postValue(true)
            } catch (e: Exception) {
                _errorMessage.postValue(e.message ?: "비밀번호 변경 오류")
            }
        }
    }
}
