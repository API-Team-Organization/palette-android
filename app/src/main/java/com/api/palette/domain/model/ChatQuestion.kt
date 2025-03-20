package com.api.palette.domain.model

import kotlinx.serialization.Serializable

@Serializable
sealed class ChatQuestion {
    @Serializable
    data class SelectableQuestion(val choices: List<PromptDataSelectableChoice>) : ChatQuestion()

    @Serializable
    data class GridQuestion(val xSize: Int, val ySize: Int, val maxCount: Int) : ChatQuestion()

    @Serializable
    object UserInputQuestion : ChatQuestion()
}

@Serializable
data class PromptDataSelectableChoice(val id: String, val displayName: String)
