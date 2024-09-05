package com.example.palette.data.auth

import com.example.palette.data.base.BaseVoidResponse
import okhttp3.OkHttpClient
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RegisterRequestManager {
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)  // 연결 타임아웃
        .writeTimeout(30, TimeUnit.SECONDS)    // 쓰기 타임아웃
        .readTimeout(30, TimeUnit.SECONDS)     // 읽기 타임아웃
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://paletteapp.xyz/backend/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val authService: AuthService = retrofit.create(AuthService::class.java)

    suspend fun registerRequest(registerData: RegisterRequest): Response<BaseVoidResponse> {
        val response = authService.register(registerData)
        if (!response.isSuccessful)
            throw HttpException(response)

        return response
    }
}