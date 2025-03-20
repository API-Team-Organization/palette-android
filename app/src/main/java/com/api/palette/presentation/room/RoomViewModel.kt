package com.api.palette.presentation.room

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.api.palette.domain.model.RoomData
import com.api.palette.domain.model.TitleData
import com.api.palette.domain.usecase.RoomUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoomViewModel @Inject constructor(
    private val roomUseCases: RoomUseCases
) : ViewModel() {

    private val _roomList = MutableLiveData<List<RoomData>>()
    val roomList: LiveData<List<RoomData>> get() = _roomList

    private val _updateSuccess = MutableLiveData<Boolean>()
    val updateSuccess: LiveData<Boolean> get() = _updateSuccess

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun createRoom(token: String) {
        viewModelScope.launch {
            try {
                roomUseCases.createRoom(token)
                loadRoomList(token)
            } catch (e: Exception) {
                _errorMessage.postValue(e.message ?: "룸 생성 오류")
            }
        }
    }

    fun loadRoomList(token: String) {
        viewModelScope.launch {
            try {
                val rooms = roomUseCases.getRoomList(token)
                _roomList.postValue(rooms)
            } catch (e: Exception) {
                _errorMessage.postValue(e.message ?: "룸 목록 로딩 오류")
            }
        }
    }

    fun deleteRoom(token: String, roomId: Int) {
        viewModelScope.launch {
            try {
                roomUseCases.deleteRoom(token, roomId)
                loadRoomList(token)
            } catch (e: Exception) {
                _errorMessage.postValue(e.message ?: "룸 삭제 오류")
            }
        }
    }

    fun setRoomTitle(token: String, title: String, roomId: Int) {
        viewModelScope.launch {
            try {
                roomUseCases.setRoomTitle(token, TitleData(title), roomId)
                _updateSuccess.postValue(true)
                loadRoomList(token)
            } catch (e: Exception) {
                _errorMessage.postValue(e.message ?: "룸 제목 변경 오류")
            }
        }
    }

    fun regenRoom(token: String, roomId: Int) {
        viewModelScope.launch {
            try {
                roomUseCases.regenRoom(token, roomId)
            } catch (e: Exception) {
                _errorMessage.postValue(e.message ?: "룸 재생성 오류")
            }
        }
    }
}
