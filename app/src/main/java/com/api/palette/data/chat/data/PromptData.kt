package com.api.palette.data.chat.data

import com.api.palette.data.chat.serializer.PromptDataSerializer
import com.api.palette.data.socket.data.PromptType
import kotlinx.serialization.Serializable

@Serializable(with = PromptDataSerializer::class)
sealed interface PromptData {
    val type: PromptType
    val question: ChatQuestion
    val answer: ChatAnswer?
    val promptName: String
    val id: String

    @Serializable
    data class Selectable(
        override val id: String,
        override val promptName: String,
        override val question: ChatQuestion.SelectableQuestion,
        override val answer: ChatAnswer.SelectableAnswer? = null
    ) : PromptData {
        override val type: PromptType = PromptType.SELECTABLE

        @Serializable
        data class Choice(val id: String, val displayName: String)
    }

    @Serializable
    data class Grid(
        override val id: String,
        override val promptName: String,
        override val question: ChatQuestion.GridQuestion,
        override val answer: ChatAnswer.GridAnswer? = null
    ) : PromptData {
        override val type: PromptType = PromptType.GRID
    }

    @Serializable
    data class UserInput(
        override val id: String,
        override val promptName: String,
        override val answer: ChatAnswer.UserInputAnswer? = null
    ) : PromptData {
        override val question = ChatQuestion.UserInputQuestion
        override val type: PromptType = PromptType.USER_INPUT
    }
}
