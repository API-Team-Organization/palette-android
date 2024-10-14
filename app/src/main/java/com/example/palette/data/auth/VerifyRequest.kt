package com.example.palette.data.auth

import kotlinx.serialization.Serializable

@Serializable
data class VerifyRequest(val code: String)
