package com.trezanix.mytreza.data.remote.dto

data class CategoryDto(
    val id: String,
    val name: String,
    val type: String,
    val icon: String? = null
)