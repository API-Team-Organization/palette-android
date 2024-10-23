package com.api.palette.data.socket

import kotlinx.datetime.Instant
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

// WebSocket으로 들어오는 데이터 타입들
@Serializable(with = BaseResponseMessageSerializer::class)
sealed class BaseResponseMessage {
    @Serializable
    data class ChatMessage(
        val id: String,
        val message: String,
        val resource: ChatResource,
        val datetime: Instant,
        val roomId: Int,
        val userId: Int,
        val isAi: Boolean,
        val promptId: String?,
    ) : BaseResponseMessage()

    @Serializable
    data class GenerateStatusMessage(
        val position: Int,
        val generating: Boolean
    ) : BaseResponseMessage()

    @Serializable
    data class ErrorMessage(
        val kind: String,
        val message: String
    ) : BaseResponseMessage() // 에러일 때 처리할 로직 필요
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
    val promptId: String?,
    val regenScope: Boolean = false
)

class BaseResponseMessageSerializer : JsonContentPolymorphicSerializer<BaseResponseMessage>(BaseResponseMessage::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<out BaseResponseMessage> {
        return when (element.jsonObject["type"]?.jsonPrimitive?.content) {
            "NEW_CHAT" -> BaseResponseMessage.ChatMessage.serializer()
            "ERROR" -> BaseResponseMessage.ErrorMessage.serializer()
            "GENERATE_STATUS" -> BaseResponseMessage.GenerateStatusMessage.serializer()
            else -> throw SerializationException("Unknown type: ${element.jsonObject["type"]}")
        }
    }
}