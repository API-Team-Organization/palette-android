package com.example.palette.data.base

import com.example.palette.common.json
import com.example.palette.data.error.CustomException
import com.example.palette.ui.util.log
import com.example.palette.ui.util.logE
import retrofit2.HttpException
import retrofit2.Response

object ErrorHandler {
    fun handleError(response: Response<*>) {
        if (response.isSuccessful)
            return
        log("코드: ${response.code()}, 메시지: ${response.message()}")
        val error = response.errorBody()
            ?.string()
            ?.let { json.decodeFromString<ErrorResponse>(it) }
            ?: throw HttpException(response)

        logE("상세 오류: ${error.kind}: ${error.message}")
        throw CustomException(error)
    }
}