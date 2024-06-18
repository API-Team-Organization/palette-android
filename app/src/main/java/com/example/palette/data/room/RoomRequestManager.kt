package com.example.palette.data.room

import android.util.Log
import com.example.palette.common.Constant
import com.example.palette.data.base.BaseResponse
import com.example.palette.data.base.BaseVoidResponse
import com.example.palette.data.room.data.IdData
import com.example.palette.data.room.data.RoomData
import com.example.palette.data.room.data.TitleData
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RoomRequestManager {
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://standard.alcl.cloud:24136")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val roomService: RoomService = retrofit.create(RoomService::class.java)

    suspend fun roomRequest(token: String, title: TitleData): Response<BaseVoidResponse> {
        val response = roomService.createRoom(token = token, title = title)
        if (!response.isSuccessful){
            throw HttpException(response)
        }
        return response
    }

    suspend fun roomList(token: String): BaseResponse<List<RoomData>> {
        return roomService.getRoomList(token)
    }

    // TODO: id=3 title= 에서 버그남. 해결 ㄱㄱ
    suspend fun deleteRoom(token: String, id: Int): Response<BaseVoidResponse> {
        val response = roomService.deleteRoom(token = token, roomId = id)
        if (!response.isSuccessful) {
            Log.d(Constant.TAG,"RoomRequestManager deleteRoom response is ${response}")
            throw HttpException(response)
        }
        return response
    }
}