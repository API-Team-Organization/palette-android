package com.example.palette.data.room

import android.util.Log
import com.example.palette.common.Constant
import com.example.palette.data.ApiClient
import com.example.palette.data.base.BaseResponse
import com.example.palette.data.base.DataResponse
import com.example.palette.data.base.VoidResponse
import com.example.palette.data.room.data.RoomData
import retrofit2.HttpException
import retrofit2.Response

object RoomRequestManager {
    private val roomService: RoomService = ApiClient.retrofit.create(RoomService::class.java)

    suspend fun roomRequest(token: String): Response<DataResponse<RoomData>> {
        return roomService.createRoom(token = token)
    }

    suspend fun roomList(token: String): DataResponse<List<RoomData>> {
        return roomService.getRoomList(token)
    }

    suspend fun deleteRoom(token: String, id: Int): Response<VoidResponse> {
        val response = roomService.deleteRoom(token = token, roomId = id)
        if (!response.isSuccessful) {
            Log.d(Constant.TAG,"RoomRequestManager deleteRoom response is ${response}")
            throw HttpException(response)
        }
        return response
    }

    suspend fun setRoomTitle(token: String, roomData: RoomData): Response<VoidResponse> {
        Log.d(Constant.TAG, "RoomRequestManager setRoomTitle roomData : $roomData.")

        val response = roomService.setRoomTitle(token = token, roomData = roomData)
        if (!response.isSuccessful) {
            Log.d(Constant.TAG, "RoomRequestManager setRoomTitle 실패했습니다. $response")
        }
        return response
    }
}