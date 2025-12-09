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
                val token = authData?.accessToken
                val name = authData?.user?.fullName ?: "User MyTreza"
                val userEmail = authData?.user?.email ?: ""

                if (token != null) {
                    tokenManager.saveAuthData(token, name, email)
                }

                val userDomain = authData?.user?.toDomain()
                if (userDomain != null) Result.success(userDomain) else Result.failure(Exception("Data kosong"))

            } else {
                val errorMsg = if (response.code() == 429) {
                    "Terlalu banyak percobaan. Tunggu 5 menit."
                } else {
                    response.body()?.message ?: "Login gagal (Code: ${response.code()})"
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(fullName: String, email: String, pass: String): Result<User> {
        return try {
            val response = api.register(RegisterRequest(fullName, email, pass))

            if (response.isSuccessful && response.body()?.success == true) {
                val userDto = response.body()?.data

                if (userDto != null) {
                    val tempUser = User(
                        id = userDto.id,
                        fullName = userDto.fullName ?: fullName,
                        email = userDto.email ?: email
                    )
                    Result.success(tempUser)
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

    override suspend fun deleteAccount(): Result<Boolean> {
        return try {
            val response = api.deleteAccount()

            if (response.isSuccessful) {
                tokenManager.clearAuthData()
                Result.success(true)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Gagal menghapus akun"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun UserDto.toDomain(): User {
        return User(
            id = this.id,
            fullName = this.fullName ?: "No Name",
            email = this.email ?: "No Email"
        )
    }
}