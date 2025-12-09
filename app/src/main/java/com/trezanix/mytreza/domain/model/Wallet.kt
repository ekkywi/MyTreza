package com.trezanix.mytreza.domain.model

data class Wallet(
    val id: String,
    val name: String,
    val type: String,
    val balance: Double,
    val accountNumber: String,
    val color: String? = null,
    val icon: String? = null
)