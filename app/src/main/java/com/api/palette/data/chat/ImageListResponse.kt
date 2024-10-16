package com.api.palette.data.chat

import kotlinx.serialization.Serializable

@Serializable
data class ImageListResponse(
    val images: List<String>
)