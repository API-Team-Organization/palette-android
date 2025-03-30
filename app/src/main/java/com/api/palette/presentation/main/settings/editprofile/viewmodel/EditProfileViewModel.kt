package com.api.palette.presentation.main.settings.editprofile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.api.palette.application.PaletteApplication
import com.api.palette.data.info.data.ChangeInfoRequest
import com.api.palette.domain.info.usecase.ChangeBirthDateUseCase
import com.api.palette.domain.info.usecase.ChangeNameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val changeNameUseCase: ChangeNameUseCase,
    private val changeBirthDateUseCase: ChangeBirthDateUseCase
) : ViewModel() {

    fun changeName(username: String, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            runCatching {
                changeNameUseCase(
                    PaletteApplication.prefs.token,
                    ChangeInfoRequest(username = username)
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

    fun changeBirthDate(birthDate: String, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            runCatching {
                changeBirthDateUseCase(
                    PaletteApplication.prefs.token,
                    ChangeInfoRequest(birthDate = birthDate)
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
