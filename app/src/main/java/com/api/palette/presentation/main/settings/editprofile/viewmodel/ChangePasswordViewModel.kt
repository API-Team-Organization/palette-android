package com.api.palette.presentation.main.settings.editprofile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.api.palette.application.PaletteApplication
import com.api.palette.data.auth.request.ChangePasswordRequest
import com.api.palette.domain.auth.usecase.ChangePasswordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val changePasswordUseCase: ChangePasswordUseCase
) : ViewModel() {

    fun changePassword(beforePassword: String, afterPassword: String, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            runCatching {
                changePasswordUseCase(
                    PaletteApplication.prefs.token,
                    ChangePasswordRequest(beforePassword, afterPassword)
                )
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    onResult(Result.success(Unit))
                } else {
                    onResult(Result.failure(HttpException(response)))
                }
            }.onFailure {
                onResult(Result.failure(it))
            }
        }
    }
}
