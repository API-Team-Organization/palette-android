package com.example.palette.data.chat.qna

import com.example.palette.data.socket.PromptType
import kotlinx.serialization.Serializable

@Serializable
sealed class ChatQuestion(val type: PromptType) {
    @Serializable
    data class SelectableQuestion(val choices: List<PromptData.Selectable.Choice>) : ChatQuestion(
        PromptType.SELECTABLE)

    @Serializable
    data class GridQuestion(val xSize: Int, val ySize: Int) : ChatQuestion(PromptType.GRID)

    @Serializable
    data object UserInputQuestion : ChatQuestion(PromptType.USER_INPUT)
}

