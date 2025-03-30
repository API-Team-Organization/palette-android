package com.api.palette.data.error

import com.api.palette.data.base.DataResponse
import com.api.palette.data.base.ErrorResponse
import com.api.palette.presentation.util.logE
import retrofit2.HttpException

fun <T> ErrorHandler.handleErrorReturn(response: DataResponse<T>): Nothing {
    logE("DataResponse code: ${response.code}, message: ${response.message}")
    val error = ErrorResponse(
        code = response.code,
        message = response.message,
        kind = "DATA_RESPONSE_ERROR"
    )
    throw CustomException(error)
}

@Throws(HttpException::class)
fun <T> ErrorHandler.handleErrorReturnAsHttpException(response: DataResponse<T>): Nothing {
    logE("DataResponse code: ${response.code}, message: ${response.message}")
    throw HttpException(
        retrofit2.Response.error<T>(
            response.code,
            okhttp3.ResponseBody.create(null, response.message)
        )
    )
}
