package com.api.palette.data.base

import com.api.palette.common.json
import com.api.palette.domain.model.ErrorResponse
import com.api.palette.data.error.CustomException
import com.api.palette.ui.util.logE
import retrofit2.HttpException
import retrofit2.Response

object ErrorHandler {
    fun handleError(response: Response<*>) {
        if (response.isSuccessful) return
        logE("Error code: ${response.code()}, message: ${response.message()}")
        val errorBody = response.errorBody()?.string()
        val errorResponse = if (!errorBody.isNullOrEmpty()) {
            try {
                json.decodeFromString(ErrorResponse.serializer(), errorBody)
            } catch (e: Exception) {
                null
            }
        } else null
        throw CustomException(errorResponse ?: throw HttpException(response))
    }
}
