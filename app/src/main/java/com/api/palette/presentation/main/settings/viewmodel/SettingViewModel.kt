package com.api.palette.presentation.main.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.api.palette.data.base.VoidResponse
import com.api.palette.data.info.data.ProfileData
import com.api.palette.domain.auth.usecase.LogoutUseCase
import com.api.palette.domain.auth.usecase.ResignUseCase
import com.api.palette.domain.info.usecase.GetProfileInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase,
    private val resignUseCase: ResignUseCase,
    private val getProfileInfoUseCase: GetProfileInfoUseCase
) : ViewModel() {

    fun logout(token: String) {
        viewModelScope.launch {
            logoutUseCase(token)
        }
    }

    suspend fun resign(token: String): Response<VoidResponse> {
        return resignUseCase(token)
    }

    fun getProfile(token: String, callback: (ProfileData?) -> Unit) {
        viewModelScope.launch {
            val response = getProfileInfoUseCase(token)
            if (response.isSuccessful) {
                callback(response.body()?.data)
            } else {
                callback(null)
            }
        }
    }
}
