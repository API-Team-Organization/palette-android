package com.api.palette.data.room

import android.util.Log
import com.api.palette.common.Constant
import com.api.palette.data.ApiClient
import com.api.palette.data.base.DataResponse
import com.api.palette.data.base.VoidResponse
import com.api.palette.data.room.data.RoomData
import com.api.palette.data.room.data.TitleData
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

    suspend fun setRoomTitle(token: String, title: TitleData, roomId: Int): Response<VoidResponse> {
        val response = roomService.setRoomTitle(token = token, title = title, roomId = roomId)
        if (!response.isSuccessful) {
            Log.d(Constant.TAG, "RoomRequestManager setRoomTitle 실패했습니다. $response")
        }
        return response
    }

    suspend fun regenRoom(token: String, roomId: Int) {
        roomService.regenRoom(token = token, roomId = roomId)
    }
}