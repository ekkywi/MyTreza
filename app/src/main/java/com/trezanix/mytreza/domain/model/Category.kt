package com.trezanix.mytreza.domain.model

data class Category(
    val id: String,
    val name: String,
    val type: String,
    val icon: String? = null
)
