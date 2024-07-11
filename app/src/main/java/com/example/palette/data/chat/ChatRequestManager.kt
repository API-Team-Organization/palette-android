package com.example.palette.data.chat

import android.util.Log
import com.example.palette.common.Constant
import com.example.palette.data.base.BaseResponse
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ChatRequestManager {
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://paletteapp.xyz/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val chatService: ChatService = retrofit.create(ChatService::class.java)

    suspend fun createChat(token: String, chat: ChatData): Response<BaseResponse<PaletteChat>> {
        val response = chatService.chat(token = token, postChat = chat)
        Log.d(Constant.TAG, "ChatRequestManager createChat response is $response")
        return response
    }

    suspend fun getChatList(token: String, roomId: Int): BaseResponse<MutableList<Received>>? {
        val response = chatService.getChatList(token = token, roomId = roomId)
        Log.d(Constant.TAG, "getChatList is $response")
        if (!response.isSuccessful) {
            return BaseResponse(
                code = response.code(), // 실패 시의 상태 코드를 설정합니다
                message = response.message(), // 실패 시의 메시지를 설정합니다
                data = mutableListOf() // 빈 리스트를 반환합니다
            )
        }
        return response.body()
    }
}