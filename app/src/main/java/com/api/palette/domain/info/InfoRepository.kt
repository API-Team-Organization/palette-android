package com.api.palette.domain.info

import com.api.palette.data.base.DataResponse
import com.api.palette.data.base.VoidResponse
import com.api.palette.data.info.data.ChangeInfoRequest
import com.api.palette.data.info.data.ProfileData
import retrofit2.Response

interface InfoRepository {
    suspend fun getProfileInfo(token: String): Response<DataResponse<ProfileData>>
    suspend fun changeName(token: String, request: ChangeInfoRequest): Response<VoidResponse>
    suspend fun changeBirthDate(token: String, request: ChangeInfoRequest): Response<VoidResponse>
}
