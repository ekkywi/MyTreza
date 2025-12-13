package com.trezanix.mytreza.domain.repository

import com.trezanix.mytreza.domain.model.Category

interface CategoryRepository {
    suspend fun getCategories(): Result<List<Category>>
    suspend fun createCategory(name: String, type: String, icon: String, color: String): Result<Category>
    suspend fun updateCategory(id: String, name: String, type: String, icon: String, color: String): Result<Category>
    suspend fun deleteCategory(id: String): Result<Boolean>
}
