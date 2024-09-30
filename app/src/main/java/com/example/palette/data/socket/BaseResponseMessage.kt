package com.example.palette.data.socket

// WebSocket으로 들어오는 데이터 타입들
sealed class BaseResponseMessage(val type: MessageType) {
    data class ChatMessage(
        val data: ChatResponse
    ) : BaseResponseMessage(MessageType.NEW_CHAT)

    data class ErrorMessage(
        val kind: String,
        val message: String
    ) : BaseResponseMessage(MessageType.ERROR)
}

enum class MessageType {
    ERROR, NEW_CHAT
}

enum class PromptType {
    USER_INPUT,
    SELECTABLE,
    GRID
}

enum class ChatResource {
    CHAT,
    IMAGE,
    PROMPT_USER_INPUT,
    PROMPT_SELECTABLE,
    PROMPT_GRID,

    INTERNAL_IMAGE_LOADING,
    INTERNAL_CHAT_LOADING,
}

data class ChatResponse(
    val action: String,
    val message: MessageResponse?,
)

data class MessageResponse(
    val id: String,
    val message: String,
    val resource: ChatResource,
    val datetime: String,
    val roomId: Int,
    val userId: Int,
    val isAi: Boolean,
    val data: PromptData?
)

sealed class PromptData(val type: PromptType) {
    data class Selectable(val choice: List<String>) : PromptData(PromptType.SELECTABLE)
    data class Grid(val xSize: Int, val ySize: Int) : PromptData(PromptType.GRID)
    data object UserInput : PromptData(PromptType.USER_INPUT)
}
