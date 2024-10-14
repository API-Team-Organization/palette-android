package com.example.palette.data.socket

import kotlinx.datetime.Instant
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

// WebSocket으로 들어오는 데이터 타입들
@Serializable
sealed class BaseResponseMessage(val type: MessageType) {
    @Serializable
    data class ChatMessage(
        val id: String,
        val message: String,
        val resource: ChatResource,
        val datetime: Instant,
        val roomId: Int,
        val userId: Int,
        val isAi: Boolean,
        val promptId: String?
    ) : BaseResponseMessage(MessageType.NEW_CHAT)

    @Serializable
    data class ErrorMessage(
        val kind: String,
        val message: String
    ) : BaseResponseMessage(MessageType.ERROR) // 에러일 때 처리할 로직 필요
}

enum class MessageType {
    NEW_CHAT, ERROR
}

enum class PromptType {
    USER_INPUT,
    SELECTABLE,
    GRID
}

enum class ChatResource {
    CHAT,
    IMAGE,
    PROMPT,

    INTERNAL_IMAGE_LOADING,
    INTERNAL_CHAT_LOADING,
}

@Serializable
data class MessageResponse(
    val id: String,
    val message: String,
    val resource: ChatResource,
    val datetime: Instant,
    val roomId: Int,
    val userId: Int,
    val isAi: Boolean,
    val promptId: String?
)
