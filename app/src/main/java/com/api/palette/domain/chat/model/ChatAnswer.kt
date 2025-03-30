package com.api.palette.domain.chat.model

import com.api.palette.data.chat.serializer.ChatAnswerSerializer
import kotlinx.serialization.Serializable

@Serializable(with = ChatAnswerSerializer::class)
sealed class ChatAnswer {

    @Serializable
    data class SelectableAnswer(
        val choiceId: String,
        val type: String
    ) : ChatAnswer()

    @Serializable
    data class GridAnswer(
        val choice: List<Int>,
        val type: String
    ) : ChatAnswer()

    @Serializable
    data class UserInputAnswer(
        val input: String,
        val type: String
    ) : ChatAnswer()
}
