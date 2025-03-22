package com.api.palette.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.api.palette.application.PaletteApplication
import com.api.palette.data.info.ChangeInfoRequest
import com.api.palette.data.info.ProfileData
import kotlinx.coroutines.launch
import retrofit2.HttpException

class InfoViewModel : ViewModel() {
    private val _profileData = MutableLiveData<ProfileData?>()
    val profileData: LiveData<ProfileData?> get() = _profileData

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun loadProfileInfo() {
        viewModelScope.launch {
            try {
                val response = PaletteApplication.appRepository.infoRepository.getProfileInfo(PaletteApplication.prefs.token)
                if (response.isSuccessful) {
                    _profileData.value = response.body()?.data
                    response.body()?.data?.let { data ->
                        PaletteApplication.prefs.userId = data.email
                        PaletteApplication.prefs.username = data.name
                        PaletteApplication.prefs.userBirthDate = data.birthDate
                    }
                } else {
                    _errorMessage.value = "프로필 정보를 가져오지 못했습니다."
                }
            } catch (e: HttpException) {
                _errorMessage.value = "프로필 정보를 가져오지 못했습니다."
            } catch (e: Exception) {
                _errorMessage.value = "알 수 없는 오류가 발생했습니다."
            }
        }
    }

    fun changeName(newName: String) {
        viewModelScope.launch {
            try {
                val response = PaletteApplication.appRepository.infoRepository.changeInfo(
                    PaletteApplication.prefs.token, ChangeInfoRequest(username = newName, birthDate = null)
                )
                if (!response.isSuccessful) {
                    _errorMessage.value = "이름 변경 실패"
                }
            } catch (e: Exception) {
                _errorMessage.value = "이름 변경 실패"
            }
        }
    }

    fun changeBirthDate(newBirthDate: String) {
        viewModelScope.launch {
            try {
                val response = PaletteApplication.appRepository.infoRepository.changeInfo(
                    PaletteApplication.prefs.token, ChangeInfoRequest(username = null, birthDate = newBirthDate)
                )
                if (!response.isSuccessful) {
                    _errorMessage.value = "생년월일 변경 실패"
                }
            } catch (e: Exception) {
                _errorMessage.value = "생년월일 변경 실패"
            }
        }
    }
}
