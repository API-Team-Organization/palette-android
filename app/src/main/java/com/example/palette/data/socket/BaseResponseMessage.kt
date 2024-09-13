package com.example.palette.data.socket

// WebSocket으로 들어오는 데이터 타입들
sealed interface BaseResponseMessage {
    val type: MessageType

    data class ChatMessage(
        val type: String,
        val data: ChatResponse?
    )

    data class ErrorMessage(
        val kind: String,
        val message: String
    ) : BaseResponseMessage {
        override val type: MessageType = MessageType.ERROR
    }
}

enum class MessageType {
    ERROR, NEW_CHAT
}

data class ChatResponse(
    val action: String,
    val message: MessageResponse,
)

data class MessageResponse(
    val id: Int?,
    val message: String?,
    val timestamp: String?,
    val roomId: Int?,
    val userId: Int?,
    val resource: String?
)
