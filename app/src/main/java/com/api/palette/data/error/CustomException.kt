package com.api.palette.data.error

import com.api.palette.domain.model.ErrorResponse

class CustomException(val errorResponse: ErrorResponse) : RuntimeException(errorResponse.message)
