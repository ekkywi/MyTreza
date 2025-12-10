package com.trezanix.mytreza.domain.repository

import com.trezanix.mytreza.domain.model.Category

interface CategoryRepository {
    suspend fun getCategories(): Result<List<Category>>
}
