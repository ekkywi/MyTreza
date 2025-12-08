package com.trezanix.mytreza.data.repository

import com.trezanix.mytreza.data.local.TokenManager
import com.trezanix.mytreza.data.remote.api.MyTrezaApiService
import com.trezanix.mytreza.data.remote.dto.LoginRequest
import com.trezanix.mytreza.data.remote.dto.RegisterRequest
import com.trezanix.mytreza.data.remote.dto.UserDto
import com.trezanix.mytreza.domain.model.User
import com.trezanix.mytreza.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: MyTrezaApiService,
    private val tokenManager: TokenManager
) : AuthRepository {

    override suspend fun login(email: String, pass: String): Result<User> {
        return try {
            val response = api.login(LoginRequest(email, pass))

            if (response.isSuccessful && response.body()?.success == true) {
                val authData = response.body()?.data

                authData?.accessToken?.let { tokenManager.saveToken(it) }

                val userDomain = authData?.user?.toDomain()

                if (userDomain != null) {
                    Result.success(userDomain)
                } else {
                    Result.failure(Exception("Data user kosong"))
                }
            } else {
                Result.failure(Exception(response.body()?.message ?: "Login gagal"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(fullName: String, email: String, pass: String): Result<User> {
        return try {
            val response = api.register(RegisterRequest(fullName, email, pass))

            if (response.isSuccessful && response.body()?.success == true) {
                val authData = response.body()?.data
                authData?.accessToken?.let { tokenManager.saveToken(it) }

                val userDomain = authData?.user?.toDomain()
                if (userDomain != null) {
                    Result.success(userDomain)
                } else {
                    Result.failure(Exception("Data user kosong"))
                }
            } else {
                Result.failure(Exception(response.body()?.message ?: "Register gagal"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        tokenManager.clearToken()
    }

    private fun UserDto.toDomain(): User {
        return User(
            id = this.id,
            fullName = this.fullName,
            email = this.email
        )
    }
}