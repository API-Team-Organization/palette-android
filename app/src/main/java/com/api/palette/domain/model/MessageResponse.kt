package com.api.palette.domain.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

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
