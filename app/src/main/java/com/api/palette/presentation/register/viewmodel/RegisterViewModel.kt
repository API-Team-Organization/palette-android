package com.api.palette.presentation.register.viewmodel

import androidx.lifecycle.*
import com.api.palette.data.auth.request.RegisterRequest
import com.api.palette.domain.auth.usecase.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _email = MutableLiveData<String>()
    val email: LiveData<String> get() = _email

    private val _password = MutableLiveData<String>()
    val password: LiveData<String> get() = _password

    private val _birthdate = MutableLiveData<String>()
    val birthdate: LiveData<String> get() = _birthdate

    private val _username = MutableLiveData<String>()
    val username: LiveData<String> get() = _username

    fun setEmail(value: String) = _email.postValue(value)
    fun setPassword(value: String) = _password.postValue(value)
    fun setBirthdate(value: String) = _birthdate.postValue(value)
    fun setUsername(value: String) = _username.postValue(value)

    private fun toRegisterRequest(): RegisterRequest? {
        val email = _email.value
        val password = _password.value
        val birthdate = _birthdate.value
        val username = _username.value

        return if (email != null && password != null && birthdate != null && username != null) {
            RegisterRequest(email, password, birthdate, username)
        } else {
            null
        }
    }

    fun register(onResult: (Result<String>) -> Unit) {
        val request = toRegisterRequest() ?: return
        viewModelScope.launch {
            runCatching { registerUseCase(request) }
                .onSuccess { response ->
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
