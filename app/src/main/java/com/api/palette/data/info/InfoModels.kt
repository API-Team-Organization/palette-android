package com.api.palette.data.info

import kotlinx.serialization.Serializable

@Serializable
data class ProfileData(
    val id: Int,
    val name: String,
    val email: String,
    val birthDate: String
)

@Serializable
data class ChangeInfoRequest(val username: String?, val birthDate: String?)
