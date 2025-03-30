package com.api.palette.data.info.request

import kotlinx.serialization.Serializable

@Serializable
data class ChangeInfoRequest(
    val username: String? = null,
    val birthDate: String? = null
)
