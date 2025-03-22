package com.api.palette.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.api.palette.application.PaletteApplication
import kotlinx.coroutines.launch
import retrofit2.HttpException

class AuthViewModel : ViewModel() {
    private val _passwordChangeResponse = MutableLiveData<Boolean>()
    val passwordChangeResponse: LiveData<Boolean> get() = _passwordChangeResponse

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun changePassword(beforePassword: String, afterPassword: String) {
        viewModelScope.launch {
            try {
                val response = PaletteApplication.appRepository.authRepository.changePassword(
                    PaletteApplication.prefs.token, beforePassword, afterPassword
                )
                if (response.isSuccessful) {
                    _passwordChangeResponse.value = true
                } else {
                    _errorMessage.value = "비밀번호 변경에 실패했습니다."
                }
            } catch (e: HttpException) {
                _errorMessage.value = "서버 오류가 발생했습니다."
            } catch (e: Exception) {
                _errorMessage.value = "네트워크 오류가 발생했습니다."
            }
        }
    }
}
