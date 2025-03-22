package com.api.palette.data.socket

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
enum class ChatResource {
    CHAT, IMAGE, PROMPT, INTERNAL_IMAGE_LOADING
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
