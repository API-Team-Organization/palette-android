package com.api.palette.presentation.main.create.chat.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.api.palette.data.chat.data.ChatAnswer
import com.api.palette.data.chat.data.PromptData
import com.api.palette.data.socket.data.MessageResponse
import com.api.palette.domain.chat.usecase.CreateChatUseCase
import com.api.palette.domain.chat.usecase.GetChatListUseCase
import com.api.palette.domain.chat.usecase.GetQnAListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val getChatListUseCase: GetChatListUseCase,
    private val getQnAListUseCase: GetQnAListUseCase,
    private val createChatUseCase: CreateChatUseCase
) : ViewModel() {

    private val _chatList = MutableLiveData<List<MessageResponse>>()
    val chatList: LiveData<List<MessageResponse>> get() = _chatList

    private val _qnaList = MutableLiveData<List<PromptData>>()
    val qnaList: LiveData<List<PromptData>> get() = _qnaList

    fun loadChatList(
        token: String,
        roomId: String,
        before: String? = null,
        size: Int = 10,
        onError: (Throwable) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = getChatListUseCase(token, roomId, before, size)
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

    fun loadQnAList(
        token: String,
        roomId: String,
        onError: (Throwable) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = getQnAListUseCase(token, roomId)
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

    fun sendChat(
        token: String,
        roomId: String,
        chat: ChatAnswer,
        onResult: (Result<Unit>) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = createChatUseCase(token, chat, roomId)
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
