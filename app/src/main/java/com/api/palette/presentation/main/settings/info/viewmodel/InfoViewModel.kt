package com.api.palette.presentation.main.settings.info.viewmodel

import androidx.lifecycle.*
import com.api.palette.data.info.InfoRepository
import com.api.palette.data.info.data.ProfileData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class InfoViewModel @Inject constructor(
    private val infoRepository: InfoRepository
) : ViewModel() {

    private val _profileInfo = MutableLiveData<ProfileData>()
    val profileInfo: LiveData<ProfileData> get() = _profileInfo

    /** 프로필 정보 불러오기 */
    fun loadProfileInfo(
        token: String,
        onError: (Throwable) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = infoRepository.getProfileInfo(token)
                if (response.isSuccessful) {
                    _profileInfo.value = response.body()?.data
                } else {
                    onError(HttpException(response))
                }
            } catch (e: Exception) {
                onError(e)
            }
        }
    }

    /** 사용자 이름 변경 */
    fun changeName(
        token: String,
        username: String,
        onResult: (Result<Unit>) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = infoRepository.changeName(token, username)
                if (response.isSuccessful) {
                    onResult(Result.success(Unit))
                } else {
                    onResult(Result.failure(HttpException(response)))
                }
            } catch (e: Exception) {
                onResult(Result.failure(e))
            }
        }
    }

    /** 생년월일 변경 */
    fun changeBirthDate(
        token: String,
        birthDate: String,
        onResult: (Result<Unit>) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = infoRepository.changeBirthDate(token, birthDate)
                if (response.isSuccessful) {
                    onResult(Result.success(Unit))
                } else {
                    onResult(Result.failure(HttpException(response)))
                }
            } catch (e: Exception) {
                onResult(Result.failure(e))
            }
        }
    }
}
