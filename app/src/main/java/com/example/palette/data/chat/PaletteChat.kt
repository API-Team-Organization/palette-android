package com.example.palette.data.chat

import kotlinx.serialization.Serializable

@Serializable
data class PaletteChat(
    val received: List<Received>
)