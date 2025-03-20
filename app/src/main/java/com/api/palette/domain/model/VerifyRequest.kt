package com.api.palette.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class VerifyRequest(val code: String)
