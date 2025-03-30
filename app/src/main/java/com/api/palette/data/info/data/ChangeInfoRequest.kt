package com.api.palette.data.info.data

import kotlinx.serialization.Serializable

@Serializable
data class ChangeInfoRequest(
    val username: String? = null,
    val birthDate: String? = null
)
