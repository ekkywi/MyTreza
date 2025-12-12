package com.trezanix.mytreza.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CreateCategoryRequest(
    @SerializedName("name") val name: String,
    @SerializedName("type") val type: String,
    @SerializedName("icon") val icon: String,
    @SerializedName("color") val color: String
)
