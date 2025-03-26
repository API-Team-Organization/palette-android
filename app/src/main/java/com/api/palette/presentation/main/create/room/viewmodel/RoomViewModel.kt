package com.api.palette.presentation.main.create.room.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.api.palette.data.room.RoomRepository
import com.api.palette.data.room.data.RoomData
import com.api.palette.data.room.data.TitleData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class RoomViewModel @Inject constructor(
    private val roomRepository: RoomRepository
) : ViewModel() {

    private val _roomList = MutableLiveData<List<RoomData>>()
    val roomList: LiveData<List<RoomData>> get() = _roomList

    fun createRoom(token: String, onResult: (Result<RoomData>) -> Unit) {
        viewModelScope.launch {
            runCatching {
                roomRepository.createRoom(token)
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    response.body()?.data?.let {
                        onResult(Result.success(it))
                    } ?: onResult(Result.failure(Exception("Room data is null")))
                } else {
                    onResult(Result.failure(HttpException(response)))
                }
            }.onFailure {
                onResult(Result.failure(it))
            }
        }
    }

    fun loadRoomList(token: String, onError: (Throwable) -> Unit) {
        viewModelScope.launch {
            runCatching {
                roomRepository.getRoomList(token)
            }.onSuccess { response ->
                _roomList.value = response.data
            }.onFailure {
                onError(it)
            }
        }
    }

    fun deleteRoom(token: String, roomId: String, onResult: (Result<Unit>) -> Unit) {
        handleUnitResult { roomRepository.deleteRoom(token, roomId) }.invoke(onResult)
    }

    fun setRoomTitle(token: String, title: TitleData, roomId: String, onResult: (Result<Unit>) -> Unit) {
        handleUnitResult { roomRepository.setRoomTitle(token, title, roomId) }.invoke(onResult)
    }

    fun regenRoom(token: String, roomId: String, onResult: (Result<Unit>) -> Unit) {
        handleUnitResult { roomRepository.regenRoom(token, roomId) }.invoke(onResult)
    }

    /**
     * 공통된 Unit 리턴 API 처리
     */
    private fun handleUnitResult(
        request: suspend () -> retrofit2.Response<*>
    ): (onResult: (Result<Unit>) -> Unit) -> Unit = { onResult ->
        viewModelScope.launch {
            runCatching {
                request()
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
