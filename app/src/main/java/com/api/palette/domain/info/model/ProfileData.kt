package com.api.palette.domain.info.model

import kotlinx.serialization.Serializable

@Serializable
data class ProfileData(
    val id: String,
    val name: String,
    val email: String,
    val birthDate: String
)
