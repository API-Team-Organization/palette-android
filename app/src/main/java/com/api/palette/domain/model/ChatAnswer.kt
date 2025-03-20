package com.api.palette.domain.model

import kotlinx.serialization.Serializable

@Serializable
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
