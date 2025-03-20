package com.api.palette.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ImageListResponse(
    val images: List<String>
)
