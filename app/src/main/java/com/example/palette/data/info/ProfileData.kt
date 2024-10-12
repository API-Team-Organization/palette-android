package com.example.palette.data.info

import kotlinx.serialization.Serializable

@Serializable
data class ProfileData(
    val id: Int,
    val name: String,
    val email: String,
    val birthDate: String
)
