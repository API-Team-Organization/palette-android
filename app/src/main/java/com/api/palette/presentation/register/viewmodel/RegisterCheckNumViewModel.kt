package com.api.palette.presentation.register.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.api.palette.data.auth.request.VerifyRequest
import com.api.palette.domain.auth.usecase.ResendUseCase
import com.api.palette.domain.auth.usecase.VerifyUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterCheckNumViewModel @Inject constructor(
    private val verifyUseCase: VerifyUseCase,
    private val resendUseCase: ResendUseCase
) : ViewModel() {

    private val _verifyResult = MutableLiveData<Result<Unit>>()
    val verifyResult: LiveData<Result<Unit>> = _verifyResult

    private val _resendResult = MutableLiveData<Result<Unit>>()
    val resendResult: LiveData<Result<Unit>> = _resendResult

    fun verifyCode(token: String, code: String) {
        viewModelScope.launch {
            runCatching {
                val response = verifyUseCase(token, VerifyRequest(code))
                if (!response.isSuccessful) throw Exception("인증 실패: ${response.code()}")
            }.fold(
                onSuccess = { _verifyResult.value = Result.success(Unit) },
                onFailure = { _verifyResult.value = Result.failure(it) }
            )
        }
    }

    fun resendCode(token: String) {
        viewModelScope.launch {
            runCatching {
                val response = resendUseCase(token)
                if (!response.isSuccessful) throw Exception("재전송 실패: ${response.code()}")
            }.fold(
                onSuccess = { _resendResult.value = Result.success(Unit) },
                onFailure = { _resendResult.value = Result.failure(it) }
            )
        }
    }
}
