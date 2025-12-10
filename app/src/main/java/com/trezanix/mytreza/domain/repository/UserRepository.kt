package com.trezanix.mytreza.domain.repository

import com.trezanix.mytreza.data.remote.dto.UserDto

interface UserRepository {
    suspend fun getUserProfile(): Result<UserDto>
}