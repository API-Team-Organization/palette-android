package com.example.palette.data.auth

import com.example.palette.data.base.BaseVoidResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object LoginRequestManager {
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://standard.alcl.cloud:24136")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val authService: AuthService = retrofit.create(AuthService::class.java)

    suspend fun loginRequest(loginData: LoginRequest): Response<BaseVoidResponse> {
        val response = authService.login(loginData)
        if (!response.isSuccessful)
            throw HttpException(response)

        return response
    }
}