package com.example.palette.data.info

import kotlinx.serialization.Serializable

@Serializable
data class ChangeInfoRequest(val username: String?, val birthDate: String?)
