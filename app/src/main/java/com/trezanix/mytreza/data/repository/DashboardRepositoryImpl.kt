package com.trezanix.mytreza.data.repository

import com.trezanix.mytreza.data.remote.api.MyTrezaApiService
import com.trezanix.mytreza.data.remote.dto.DashboardDto
import com.trezanix.mytreza.domain.model.DashboardData
import com.trezanix.mytreza.domain.repository.DashboardRepository
import javax.inject.Inject

class DashboardRepositoryImpl @Inject constructor(
    private val api: MyTrezaApiService
) : DashboardRepository {

    override suspend fun getDashboardSummary(): Result<DashboardData> {
        return try {
            val response = api.getDashboard()

            if (response.isSuccessful && response.body()?.success == true) {
                val dto = response.body()?.data
                if (dto != null) {
                    Result.success(dto.toDomain())
                } else {
                    Result.failure(Exception("Data dashboard kosong"))
                }
            } else {
                Result.failure(Exception(response.body()?.message ?: "Gagal memuat dashboard"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun DashboardDto.toDomain(): DashboardData {
        return DashboardData(
            netWorth = this.netWorth,
            income = this.incomeThisMonth,
            expense = this.expenseThisMonth,
            biggestCategory = this.biggestSpendingCategory?.name ?: "-",
            biggestCategoryAmount = this.biggestSpendingCategory?.amount ?: 0.0
        )
    }
}