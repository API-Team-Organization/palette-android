package com.api.palette.presentation.register.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.api.palette.data.auth.AuthRepository
import com.api.palette.data.auth.request.RegisterRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _registerResult = MutableLiveData<Result<String>>()
    val registerResult: LiveData<Result<String>> get() = _registerResult

    private val _email = MutableLiveData<String>()
    val email: LiveData<String> get() = _email

    private val _password = MutableLiveData<String>()
    val password: LiveData<String> get() = _password

    private val _birthdate = MutableLiveData<String>()
    val birthdate: LiveData<String> get() = _birthdate

    private val _username = MutableLiveData<String>()
    val username: LiveData<String> get() = _username

    fun setEmail(value: String) { _email.value = value }
    fun setPassword(value: String) { _password.value = value }
    fun setBirthdate(value: String) { _birthdate.value = value }
    fun setUsername(value: String) { _username.value = value }

    /**
     * RegisterRequest 데이터 객체 생성
     */
    fun toRegisterRequest(): RegisterRequest? {
        val email = _email.value
        val password = _password.value
        val birthdate = _birthdate.value
        val username = _username.value

        return if (email != null && password != null && birthdate != null && username != null) {
            RegisterRequest(email, password, birthdate, username)
        } else null
    }

    /**
     * 회원가입 요청
     */
    fun register(onResult: (Result<String>) -> Unit) {
        val request = toRegisterRequest() ?: return

        viewModelScope.launch {
            runCatching {
                authRepository.register(request)
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    val token = response.headers()["X-AUTH-Token"] ?: ""
                    onResult(Result.success(token))
                } else {
                    onResult(Result.failure(Exception("회원가입 실패: ${response.code()}")))
                }
            }.onFailure { e ->
                onResult(Result.failure(e))
            }
        }
    }
}
