package com.api.palette.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.api.palette.data.auth.AuthRepository
import com.api.palette.data.auth.request.LoginRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.SocketTimeoutException
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    /**
     * 로그인 요청을 보내고 결과로 받은 토큰을 콜백으로 전달합니다.
     */
    fun login(email: String, password: String, onResult: (Result<String>) -> Unit) {
        viewModelScope.launch {
            try {
                val response = authRepository.login(LoginRequest(email, password))
                if (response.isSuccessful) {
                    val token = response.headers()["X-AUTH-Token"].orEmpty()
                    onResult(Result.success(token))
                } else {
                    onResult(Result.failure(HttpException(response)))
                }
            } catch (e: SocketTimeoutException) {
                onResult(Result.failure(e))
            } catch (e: Exception) {
                onResult(Result.failure(e))
            }
        }
    }
}
