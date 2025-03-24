package com.api.palette.data.error

import com.api.palette.data.base.ErrorResponse

class CustomException(errorResponse: ErrorResponse) : RuntimeException(errorResponse.message)
