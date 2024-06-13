package com.example.palette.data.auth

import com.example.palette.application.PaletteApplication
import com.example.palette.data.base.BaseVoidResponse
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AuthRequestManager {
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

    suspend fun logoutRequest(): Response<BaseVoidResponse> {
        val response = authService.logout(PaletteApplication.prefs.token)
        if (!response.isSuccessful)
            throw HttpException(response)

        return response
    }

    suspend fun session(): Response<BaseVoidResponse> {
        val response = authService.session(PaletteApplication.prefs.token)
        if (!response.isSuccessful)
            throw HttpException(response)

        return response
    }
}