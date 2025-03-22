package com.api.palette.data.socket

import kotlinx.serialization.Serializable

@Serializable
enum class PromptType {
    USER_INPUT,
    SELECTABLE,
    GRID
}
