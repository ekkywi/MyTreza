package com.trezanix.mytreza.data.remote.api

import com.trezanix.mytreza.data.remote.dto.AuthData
import com.trezanix.mytreza.data.remote.dto.BaseResponse
import com.trezanix.mytreza.data.remote.dto.LoginRequest
import com.trezanix.mytreza.data.remote.dto.RegisterRequest
import com.trezanix.mytreza.data.remote.dto.DashboardDto
import com.trezanix.mytreza.data.remote.dto.UserDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface MyTrezaApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<BaseResponse<AuthData>>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<BaseResponse<UserDto>>

    @GET("dashboard")
    suspend fun getDashboard(): Response<BaseResponse<DashboardDto>>
}