package com.api.palette.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.api.palette.application.PaletteApplication
import com.api.palette.data.room.RoomData
import com.api.palette.data.room.TitleData
import kotlinx.coroutines.launch
import retrofit2.HttpException

class RoomViewModel : ViewModel() {
    private val _roomList = MutableLiveData<List<RoomData>>()
    val roomList: LiveData<List<RoomData>> get() = _roomList

    private val _createRoomResponse = MutableLiveData<RoomData?>()
    val createRoomResponse: LiveData<RoomData?> get() = _createRoomResponse

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun createRoom() {
        viewModelScope.launch {
            try {
                val response = PaletteApplication.appRepository.roomRepository.createRoom(PaletteApplication.prefs.token)
                if (response.isSuccessful) {
                    _createRoomResponse.value = response.body()?.data
                } else {
                    _errorMessage.value = "룸 생성 실패"
                }
            } catch (e: HttpException) {
                _errorMessage.value = "룸 생성 실패"
            } catch (e: Exception) {
                _errorMessage.value = "알 수 없는 오류 발생"
            }
        }
    }

    fun getRoomList() {
        viewModelScope.launch {
            try {
                val roomListResponse = PaletteApplication.appRepository.roomRepository.getRoomList(PaletteApplication.prefs.token)
                _roomList.value = roomListResponse.data
            } catch (e: Exception) {
                _errorMessage.value = "룸 목록 로드 실패"
            }
        }
    }

    fun deleteRoom(roomId: Int) {
        viewModelScope.launch {
            try {
                val response = PaletteApplication.appRepository.roomRepository.deleteRoom(PaletteApplication.prefs.token, roomId)
                if (response.isSuccessful) {
                    _roomList.value = _roomList.value?.filter { it.id != roomId }
                } else {
                    _errorMessage.value = "룸 삭제 실패: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "룸 삭제 실패"
            }
        }
    }

    fun setRoomTitle(roomId: Int, newTitle: String) {
        viewModelScope.launch {
            try {
                val response = PaletteApplication.appRepository.roomRepository.setRoomTitle(
                    PaletteApplication.prefs.token, roomId, TitleData(newTitle)
                )
                if (!response.isSuccessful) {
                    _errorMessage.value = "룸 제목 변경 실패"
                }
            } catch (e: Exception) {
                _errorMessage.value = "룸 제목 변경 실패"
            }
        }
    }

    fun regenRoom(roomId: Int) {
        viewModelScope.launch {
            try {
                val response = PaletteApplication.appRepository.roomRepository.regenRoom(PaletteApplication.prefs.token, roomId)
                if (!response.isSuccessful) {
                    _errorMessage.value = "재생성 실패"
                }
            } catch (e: Exception) {
                _errorMessage.value = "재생성 실패"
            }
        }
    }
}
