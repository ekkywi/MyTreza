package com.trezanix.mytreza.domain.repository

import com.trezanix.mytreza.domain.model.User

interface AuthRepository {
    suspend fun login(email: String, pass: String): Result<User>
    suspend fun register(fullName: String, email: String, pass: String): Result<User>
    suspend fun logout()
}