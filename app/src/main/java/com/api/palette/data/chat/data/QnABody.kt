package com.api.palette.data.chat.data

import com.api.palette.domain.chat.model.ChatAnswer
import kotlinx.serialization.Serializable

@Serializable
data class QnABody(val data: ChatAnswer)
