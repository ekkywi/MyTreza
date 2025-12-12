package com.trezanix.mytreza.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CategoryDto(
    val id: String,
    val name: String,
    val type: String,
    @SerializedName("icon")
    val icon: String? = null,
    val color: String? = null,
    val userId: String? = null
)