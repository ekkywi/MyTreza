package com.trezanix.mytreza.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.trezanix.mytreza.data.remote.api.MyTrezaApiService
import com.trezanix.mytreza.data.remote.dto.CategoryDto
import com.trezanix.mytreza.data.remote.dto.CreateCategoryRequest
import com.trezanix.mytreza.domain.model.Category
import com.trezanix.mytreza.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
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

    override suspend fun createCategory(name: String, type: String, icon: String, color: String): Result<Boolean> {
        return try {
            val request = CreateCategoryRequest(
                name = name,
                type = type,
                icon = icon,
                color = color
            )
            val response = api.createCategory(request)
            
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(true)
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateCategory(id: String, name: String, type: String, icon: String, color: String): Result<Boolean> {
        return try {
            val request = CreateCategoryRequest(
                name = name,
                type = type,
                icon = icon,
                color = color
            )
            val response = api.updateCategory(id, request)

            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(true)
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteCategory(id: String): Result<Boolean> {
        return try {
            val response = api.deleteCategory(id)

            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(true)
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
            icon = this.icon,
            color = this.color,
            userId = this.userId
        )
    }
}
