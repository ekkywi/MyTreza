package com.trezanix.mytreza.data.repository

import com.trezanix.mytreza.data.remote.api.MyTrezaApiService
import com.trezanix.mytreza.data.remote.dto.UserDto
import com.trezanix.mytreza.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val api: MyTrezaApiService
) : UserRepository {

    override suspend fun getUserProfile(): Result<UserDto> {
        return try {
            val response = api.getUserProfile()
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}