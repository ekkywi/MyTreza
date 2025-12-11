package com.trezanix.mytreza.domain.model

data class Category(
    val id: String,
    val name: String,
    val type: String,
    val icon: String? = "category",
    val color: String? = "#000000",
    val userId: String? = null,
    val createdAt: String? = null
)
