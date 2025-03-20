package com.api.palette.data.chat.qna

import kotlinx.serialization.Serializable

@Serializable
sealed class ChatQuestion {
    @Serializable
    data class SelectableQuestion(val choices: List<PromptData.Selectable.Choice>) : ChatQuestion()

    @Serializable
    data class GridQuestion(val xSize: Int, val ySize: Int, val maxCount: Int) : ChatQuestion()

    @Serializable
    data object UserInputQuestion : ChatQuestion()
}
