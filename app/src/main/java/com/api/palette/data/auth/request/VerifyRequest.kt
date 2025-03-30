package com.api.palette.data.auth.request

import kotlinx.serialization.Serializable

@Serializable
data class VerifyRequest(val code: String)
