package com.api.palette.data.chat.qna

import kotlinx.serialization.Serializable

@Serializable
data class QnABody(
    val data: ChatAnswer
)