package com.api.palette.data.model

import com.api.palette.domain.model.ChatAnswer
import kotlinx.serialization.Serializable

@Serializable
data class QnABody(
    val roomId: String,
    val data: ChatAnswer
)
