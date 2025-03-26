package com.api.palette.presentation.main.create.chat.viewmodel

import androidx.lifecycle.*
import com.api.palette.data.chat.ChatRepository
import com.api.palette.data.chat.data.ChatAnswer
import com.api.palette.data.chat.data.PromptData
import com.api.palette.data.socket.data.MessageResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _chatList = MutableLiveData<List<MessageResponse>>()
    val chatList: LiveData<List<MessageResponse>> get() = _chatList

    private val _qnaList = MutableLiveData<List<PromptData>>()
    val qnaList: LiveData<List<PromptData>> get() = _qnaList

    /**
     * 채팅 리스트 불러오기
     */
    fun loadChatList(
        token: String,
        roomId: String,
        before: String? = null,
        size: Int = 10,
        onError: (Throwable) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = chatRepository.getChatList(token, roomId, before, size)
                if (response.isSuccessful) {
                    _chatList.value = response.body()?.data.orEmpty()
                } else {
                    onError(HttpException(response))
                }
            } catch (e: Exception) {
                onError(e)
            }
        }
    }

    /**
     * QnA 리스트 불러오기
     */
    fun loadQnAList(
        token: String,
        roomId: String,
        onError: (Throwable) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = chatRepository.getQnAList(token, roomId)
                if (response.isSuccessful) {
                    _qnaList.value = response.body()?.data.orEmpty()
                } else {
                    onError(HttpException(response))
                }
            } catch (e: Exception) {
                onError(e)
            }
        }
    }

    /**
     * 채팅 전송
     */
    fun sendChat(
        token: String,
        roomId: String,
        chat: ChatAnswer,
        onResult: (Result<Unit>) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = chatRepository.createChat(token, chat, roomId)
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
