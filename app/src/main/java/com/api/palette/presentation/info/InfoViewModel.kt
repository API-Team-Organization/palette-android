package com.api.palette.presentation.info

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.api.palette.domain.model.ProfileData
import com.api.palette.domain.usecase.InfoUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InfoViewModel @Inject constructor(
    private val infoUseCases: InfoUseCases
) : ViewModel() {

    private val _profileData = MutableLiveData<ProfileData>()
    val profileData: LiveData<ProfileData> get() = _profileData

    private val _updateSuccess = MutableLiveData<Boolean>()
    val updateSuccess: LiveData<Boolean> get() = _updateSuccess

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun loadProfile(token: String) {
        viewModelScope.launch {
            try {
                val profile = infoUseCases.profileInfo(token)
                _profileData.postValue(profile)
            } catch (e: Exception) {
                _errorMessage.postValue(e.message ?: "프로필 로딩 오류")
            }
        }
    }

    fun changeName(token: String, username: String) {
        viewModelScope.launch {
            try {
                infoUseCases.changeName(token, username)
                _updateSuccess.postValue(true)
                loadProfile(token)
            } catch (e: Exception) {
                _errorMessage.postValue(e.message ?: "이름 변경 오류")
            }
        }
    }

    fun changeBirthDate(token: String, birthDate: String) {
        viewModelScope.launch {
            try {
                infoUseCases.changeBirthDate(token, birthDate)
                _updateSuccess.postValue(true)
                loadProfile(token)
            } catch (e: Exception) {
                _errorMessage.postValue(e.message ?: "생년월일 변경 오류")
            }
        }
    }
}
