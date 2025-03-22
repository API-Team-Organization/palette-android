package com.api.palette.data.chat.qna

import com.api.palette.data.socket.PromptType
import kotlinx.serialization.Serializable

@Suppress("unused")
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

@Suppress("unused")
@Serializable
sealed class ChatQuestion(val type: PromptType) {
    @Serializable
    data class SelectableQuestion(val choices: List<PromptData.Selectable.Choice>) : ChatQuestion(PromptType.SELECTABLE)

    @Serializable
    data class GridQuestion(val xSize: Int, val ySize: Int, val maxCount: Int) : ChatQuestion(PromptType.GRID)

    @Serializable
    data object UserInputQuestion : ChatQuestion(PromptType.USER_INPUT)
}

@Serializable
data class QnABody(
    val data: ChatAnswer
)
