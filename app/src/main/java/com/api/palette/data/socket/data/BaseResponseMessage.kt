package com.api.palette.data.socket.data

import com.api.palette.data.socket.serializer.BaseResponseMessageSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable(with = BaseResponseMessageSerializer::class)
sealed class BaseResponseMessage {

    @Serializable
    data class ChatMessage(
        val id: String,
        val message: String,
        val resource: ChatResource,
        val datetime: Instant,
        val roomId: String,
        val userId: String,
        val isAi: Boolean,
        val regenScope: Boolean,
        val promptId: String?
    ) : BaseResponseMessage()

    @Serializable
    data class GenerateStatusMessage(
        val position: Int,
        val generating: Boolean
    ) : BaseResponseMessage()

    @Serializable
    data class ImageProgressMessage(
        val value: Int,
        val max: Int
    ) : BaseResponseMessage()

    @Serializable
    data class ErrorMessage(
        val kind: String,
        val message: String
    ) : BaseResponseMessage()
}
