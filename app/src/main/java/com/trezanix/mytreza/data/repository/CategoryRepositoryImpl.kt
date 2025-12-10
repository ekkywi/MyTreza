package com.trezanix.mytreza.data.repository

import com.trezanix.mytreza.data.remote.api.MyTrezaApiService
import com.trezanix.mytreza.data.remote.dto.CategoryDto
import com.trezanix.mytreza.domain.model.Category
import com.trezanix.mytreza.domain.repository.CategoryRepository
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val api: MyTrezaApiService
) : CategoryRepository {

    override suspend fun getCategories(): Result<List<Category>> {
        return try {
            val response = api.getCategories()

            if (response.isSuccessful && response.body()?.success == true) {
                val items = response.body()?.data?.map { it.toDomain() } ?: emptyList()
                Result.success(items)
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun CategoryDto.toDomain(): Category {
        return Category(
            id = this.id,
            name = this.name,
            type = this.type,
            icon = this.icon
        )
    }
}
