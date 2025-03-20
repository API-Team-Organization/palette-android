package com.api.palette.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ProfileData(
    val id: Int,
    val name: String,
    val email: String,
    val birthDate: String
)
