package com.api.palette.presentation.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.api.palette.data.auth.request.LoginRequest
import com.api.palette.domain.auth.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.SocketTimeoutException
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    fun login(email: String, password: String, onResult: (Result<String>) -> Unit) {
        viewModelScope.launch {
            try {
                val response = loginUseCase(LoginRequest(email, password))
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
