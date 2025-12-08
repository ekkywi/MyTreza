package com.trezanix.mytreza.data.repository

import com.trezanix.mytreza.data.local.TokenManager
import com.trezanix.mytreza.data.remote.api.MyTrezaApiService
import com.trezanix.mytreza.data.remote.dto.DashboardDto
import com.trezanix.mytreza.domain.model.DashboardData
import com.trezanix.mytreza.domain.repository.DashboardRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class DashboardRepositoryImpl @Inject constructor(
    private val api: MyTrezaApiService,
    private val tokenManager: TokenManager
) : DashboardRepository {

    override suspend fun getDashboardSummary(): Result<DashboardData> {
        return try {
            val response = api.getDashboard()

            if (response.isSuccessful && response.body()?.success == true) {
                val dto = response.body()?.data

                val storedName = tokenManager.userName.first() ?: "User MyTreza"

                if (dto != null) {
                    Result.success(dto.toDomain(storedName))
                } else {
                    Result.failure(Exception("Data dashboard kosong"))
                }
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun DashboardDto.toDomain(userName: String): DashboardData {
        return DashboardData(
            userName = userName,
            netWorth = this.netWorth,
            income = this.incomeThisMonth,
            expense = this.expenseThisMonth,
            biggestCategory = this.biggestSpendingCategory?.name ?: "-",
            biggestCategoryAmount = this.biggestSpendingCategory?.amount ?: 0.0
        )
    }
}