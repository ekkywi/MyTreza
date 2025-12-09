package com.trezanix.mytreza.data.remote.api

import com.trezanix.mytreza.data.remote.dto.AuthData
import com.trezanix.mytreza.data.remote.dto.BaseResponse
import com.trezanix.mytreza.data.remote.dto.CreateWalletRequest
import com.trezanix.mytreza.data.remote.dto.LoginRequest
import com.trezanix.mytreza.data.remote.dto.RegisterRequest
import com.trezanix.mytreza.data.remote.dto.DashboardDto
import com.trezanix.mytreza.data.remote.dto.UserDto
import com.trezanix.mytreza.data.remote.dto.WalletDataResponse
import com.trezanix.mytreza.data.remote.dto.WalletDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST

interface MyTrezaApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<BaseResponse<AuthData>>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<BaseResponse<UserDto>>

    @DELETE("users/me")
    suspend fun deleteAccount(): Response<BaseResponse<Any>>

    @GET("dashboard")
    suspend fun getDashboard(): Response<BaseResponse<DashboardDto>>

    @GET("wallets")
    suspend fun getWallets(): Response<BaseResponse<WalletDataResponse>>

    @POST("wallets")
    suspend fun createWallet(@Body request: CreateWalletRequest): Response<BaseResponse<WalletDto>>
}