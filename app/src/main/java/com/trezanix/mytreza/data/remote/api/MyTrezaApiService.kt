package com.trezanix.mytreza.data.remote.api

import com.trezanix.mytreza.data.remote.dto.AuthData
import com.trezanix.mytreza.data.remote.dto.BaseResponse
import com.trezanix.mytreza.data.remote.dto.CategoryDto
import com.trezanix.mytreza.data.remote.dto.CreateTransactionRequest
import com.trezanix.mytreza.data.remote.dto.CreateTransferRequest
import com.trezanix.mytreza.data.remote.dto.CreateWalletRequest
import com.trezanix.mytreza.data.remote.dto.LoginRequest
import com.trezanix.mytreza.data.remote.dto.RegisterRequest
import com.trezanix.mytreza.data.remote.dto.DashboardDto
import com.trezanix.mytreza.data.remote.dto.TransactionDataResponse
import com.trezanix.mytreza.data.remote.dto.TransactionDto
import com.trezanix.mytreza.data.remote.dto.UpdateWalletRequest
import com.trezanix.mytreza.data.remote.dto.UserDto
import com.trezanix.mytreza.data.remote.dto.WalletDto
import com.trezanix.mytreza.data.remote.dto.WalletListDataResponse
import com.trezanix.mytreza.data.remote.dto.WalletStatsDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface MyTrezaApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<BaseResponse<AuthData>>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<BaseResponse<UserDto>>

    @GET("users/me")
    suspend fun getUserProfile(): Response<BaseResponse<UserDto>>

    @DELETE("users/me")
    suspend fun deleteAccount(): Response<BaseResponse<Any>>

    @GET("dashboard")
    suspend fun getDashboard(): Response<BaseResponse<DashboardDto>>

    @GET("wallets")
    suspend fun getWallets(): Response<BaseResponse<WalletListDataResponse>>

    @GET("wallets/{id}")
    suspend fun getWalletDetail(@Path("id") id: String): Response<BaseResponse<WalletDto>>

    @GET("wallets/{id}/stats")
    suspend fun getWalletStats(
        @Path("id") id: String,
        @Query("month") month: Int,
        @Query("year") year: Int
    ): Response<BaseResponse<WalletStatsDto>>

    @POST("wallets")
    suspend fun createWallet(
        @Body request: CreateWalletRequest // Ubah dari Map ke DTO
    ): Response<BaseResponse<WalletDto>>

    @PUT("wallets/{id}")
    suspend fun updateWallet(
        @Path("id") id: String,
        @Body request: UpdateWalletRequest // Ubah dari Map ke DTO
    ): Response<BaseResponse<WalletDto>>

    @DELETE("wallets/{id}")
    suspend fun deleteWallet(@Path("id") id: String): Response<BaseResponse<Any>>

    @PATCH("wallets/{id}/archive")
    suspend fun archiveWallet(@Path("id") id: String): Response<BaseResponse<Any>>

    @GET("transactions")
    suspend fun getTransactionsByWallet(
        @Query("walletId") walletId: String,
        @Query("month") month: Int,
        @Query("year") year: Int,
        @Query("limit") limit: Int = 20
    ): Response<BaseResponse<TransactionDataResponse>>

    @GET("transactions")
    suspend fun getTransactions(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10
    ): Response<BaseResponse<TransactionDataResponse>>

    @POST("transactions")
    suspend fun createTransaction(
        @Body request: CreateTransactionRequest
    ): Response<BaseResponse<TransactionDto>>

    @POST("transfers")
    suspend fun createTransfer(
        @Body request: CreateTransferRequest
    ): Response<BaseResponse<Any>>

    @DELETE("transactions/{id}")
    suspend fun deleteTransaction(
        @Path("id") id: String
    ): Response<BaseResponse<Any>>

    @GET("categories")
    suspend fun getCategories(): Response<BaseResponse<List<CategoryDto>>>
}