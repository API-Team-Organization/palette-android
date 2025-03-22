package com.api.palette.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.api.palette.application.PaletteApplication
import com.api.palette.data.auth.LoginRequest
import com.api.palette.data.base.VoidResponse
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.SocketTimeoutException

class LoginViewModel : ViewModel() {

    private val _loginResponse = MutableLiveData<VoidResponse?>()
    val loginResponse: LiveData<VoidResponse?> get() = _loginResponse

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = PaletteApplication.appRepository.authRepository.login(LoginRequest(email, password))
                if (response.isSuccessful) {
                    _loginResponse.value = response.body()
                    val token = response.headers()["X-AUTH-Token"] ?: ""
                    PaletteApplication.prefs.token = token
                } else {
                    _errorMessage.value = "이메일과 비밀번호를 다시 확인해주세요"
                }
            } catch (e: HttpException) {
                _errorMessage.value = "이메일과 비밀번호를 다시 확인해주세요"
            } catch (e: SocketTimeoutException) {
                _errorMessage.value = "네트워크 연결이 불안정합니다. 다시 시도해주세요."
            } catch (e: Exception) {
                _errorMessage.value = "알 수 없는 에러"
            }
        }
    }
}
