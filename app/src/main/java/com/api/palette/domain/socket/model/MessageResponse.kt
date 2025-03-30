package com.api.palette.domain.socket.model

import com.api.palette.data.socket.data.ChatResource
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class MessageResponse(
    val id: String,
    val message: String,
    val resource: ChatResource,
    val datetime: Instant,
    val roomId: String,
    val userId: String,
    val isAi: Boolean,
    val promptId: String?,
    val regenScope: Boolean = false
)
