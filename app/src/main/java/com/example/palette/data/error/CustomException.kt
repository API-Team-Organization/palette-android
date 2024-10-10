package com.example.palette.data.error

import com.example.palette.data.base.ErrorResponse

class CustomException(val errorResponse: ErrorResponse) : RuntimeException(errorResponse.message)