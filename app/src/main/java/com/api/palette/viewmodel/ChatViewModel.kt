package com.api.palette.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.api.palette.application.PaletteApplication
import com.api.palette.data.chat.qna.ChatAnswer
import com.api.palette.data.chat.qna.PromptData
import com.api.palette.data.chat.qna.QnABody
import com.api.palette.data.socket.BaseResponseMessage
import com.api.palette.data.socket.MessageResponse
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import retrofit2.HttpException

class ChatViewModel : ViewModel() {
    private val _chatList = MutableLiveData<MutableList<MessageResponse>>(mutableListOf())
    val chatList: LiveData<MutableList<MessageResponse>> get() = _chatList

    private val _qnaList = MutableLiveData<List<PromptData>>(listOf())
    val qnaList: LiveData<List<PromptData>> get() = _qnaList

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    var currentRoomId: Int = 0

    fun loadChatList(before: String? = Clock.System.now().toString(), size: Int = 10) {
        viewModelScope.launch {
            try {
                val response = PaletteApplication.appRepository.chatRepository.getChatList(
                    PaletteApplication.prefs.token, currentRoomId, before, size
                )
                if (response.isSuccessful) {
                    val chats = response.body()?.data ?: mutableListOf()
                    _chatList.value = chats.reversed().toMutableList()
                } else {
                    _errorMessage.value = "채팅 로드에 실패했습니다."
                }
            } catch (e: HttpException) {
                _errorMessage.value = "채팅 로드에 실패했습니다."
            } catch (e: Exception) {
                _errorMessage.value = "알 수 없는 오류가 발생했습니다."
            }
        }
    }

    fun loadQnAList(roomId: Int) {
        viewModelScope.launch {
            try {
                val response = PaletteApplication.appRepository.chatRepository.getQnAList(PaletteApplication.prefs.token, roomId)
                if (response.isSuccessful) {
                    @Suppress("UNCHECKED_CAST")
                    val qna = response.body()?.data as? List<PromptData> ?: listOf()
                    _qnaList.value = qna
                } else {
                    _errorMessage.value = "QnA 로드에 실패했습니다."
                }
            } catch (e: HttpException) {
                _errorMessage.value = "QnA 로드에 실패했습니다."
            } catch (e: Exception) {
                _errorMessage.value = "알 수 없는 오류가 발생했습니다."
            }
        }
    }

    fun sendChatMessage(roomId: Int, chat: ChatAnswer) {
        viewModelScope.launch {
            try {
                PaletteApplication.appRepository.chatRepository.sendChat(
                    PaletteApplication.prefs.token, roomId, QnABody(chat)
                )
            } catch (e: Exception) {
                _errorMessage.value = "채팅 전송에 실패했습니다."
            }
        }
    }

    fun addSocketMessage(message: BaseResponseMessage) {
        viewModelScope.launch {
            when (message) {
                is BaseResponseMessage.ChatMessage -> {
                    val current = _chatList.value ?: mutableListOf()
                    current.add(
                        MessageResponse(
                            id = message.id,
                            promptId = message.promptId,
                            message = message.message,
                            roomId = message.roomId,
                            userId = message.userId,
                            datetime = message.datetime,
                            resource = message.resource,
                            regenScope = message.regenScope,
                            isAi = message.isAi
                        )
                    )
                    _chatList.value = current
                }
                else -> { }
            }
        }
    }

    fun loadMoreChats(before: String) {
        viewModelScope.launch {
            try {
                val response = PaletteApplication.appRepository.chatRepository.getChatList(
                    PaletteApplication.prefs.token, currentRoomId, before, 10
                )
                if (response.isSuccessful) {
                    val newChats = response.body()?.data?.reversed() ?: listOf()
                    val current = _chatList.value ?: mutableListOf()
                    _chatList.value = (newChats + current).toMutableList()
                }
            } catch (e: Exception) {
                _errorMessage.value = "추가 채팅 로드 실패"
            }
        }
    }
}
