package com.example.palette.data.chat

import android.util.Log
import com.example.palette.common.Constant
import com.example.palette.data.base.BaseResponse
import com.example.palette.data.base.BaseVoidResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ChatRequestManager {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://paletteapp.xyz/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val chatService: ChatService = retrofit.create(ChatService::class.java)

    suspend fun createChat(token: String, chat: ChatData): Response<BaseVoidResponse> {
        val response = chatService.chat(token = token, postChat = chat)
        Log.d(Constant.TAG, "createChat is $response")
        return response
    }

    suspend fun getChatList(token: String, roomId: Int): BaseResponse<MutableList<ChatModel>>? {
        val response = chatService.getChatList(token = token, roomId = roomId)
        Log.d(Constant.TAG, "getChatList is $response")
        return response.body()
    }
}