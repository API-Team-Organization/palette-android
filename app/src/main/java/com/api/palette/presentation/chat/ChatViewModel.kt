package com.api.palette.presentation.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.api.palette.domain.model.ChatAnswer
import com.api.palette.domain.model.MessageResponse
import com.api.palette.domain.model.PromptData
import com.api.palette.domain.usecase.ChatUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatUseCases: ChatUseCases
) : ViewModel() {

    private val _chatList = MutableLiveData<List<MessageResponse>>()
    val chatList: LiveData<List<MessageResponse>> get() = _chatList

    private val _qnaList = MutableLiveData<List<PromptData>>()
    val qnaList: LiveData<List<PromptData>> get() = _qnaList

    private val _imageList = MutableLiveData<List<String>>()
    val imageList: LiveData<List<String>> get() = _imageList

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun createChat(token: String, chat: ChatAnswer, roomId: Int) {
        viewModelScope.launch {
            try {
                chatUseCases.createChat(token, chat, roomId)
                loadChatList(token, roomId)
            } catch (e: Exception) {
                _errorMessage.postValue(e.message ?: "메시지 전송 오류")
            }
        }
    }

    fun loadChatList(token: String, roomId: Int, before: String? = null) {
        viewModelScope.launch {
            try {
                val chats = chatUseCases.getChatList(token, roomId, before)
                _chatList.postValue(chats)
            } catch (e: Exception) {
                _errorMessage.postValue(e.message ?: "채팅 목록 로딩 오류")
            }
        }
    }

    fun loadQnAList(token: String, roomId: Int) {
        viewModelScope.launch {
            try {
                val qnas = chatUseCases.getQnAList(token, roomId)
                _qnaList.postValue(qnas)
            } catch (e: Exception) {
                _errorMessage.postValue(e.message ?: "QnA 목록 로딩 오류")
            }
        }
    }

    fun loadImageList(token: String, page: Int, size: Int) {
        viewModelScope.launch {
            try {
                val images = chatUseCases.getImageList(token, page, size)
                _imageList.postValue(images)
            } catch (e: Exception) {
                _errorMessage.postValue(e.message ?: "이미지 목록 로딩 오류")
            }
        }
    }
}
