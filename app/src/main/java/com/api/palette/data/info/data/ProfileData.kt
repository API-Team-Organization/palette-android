package com.api.palette.data.info.data

import kotlinx.serialization.Serializable

@Serializable
data class ProfileData(
    val id: String,
    val name: String,
    val email: String,
    val birthDate: String
)
