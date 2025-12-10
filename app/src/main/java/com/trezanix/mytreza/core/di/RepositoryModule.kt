package com.trezanix.mytreza.core.di

import com.trezanix.mytreza.data.repository.AuthRepositoryImpl
import com.trezanix.mytreza.data.repository.DashboardRepositoryImpl
import com.trezanix.mytreza.domain.repository.AuthRepository
import com.trezanix.mytreza.domain.repository.DashboardRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindDashboardRepository(
        dashboardRepositoryImpl: DashboardRepositoryImpl
    ): DashboardRepository

    @Binds
    @Singleton
    abstract fun bindWalletRepository(
        walletRepositoryImpl: com.trezanix.mytreza.data.repository.WalletRepositoryImpl
    ): com.trezanix.mytreza.domain.repository.WalletRepository

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(
        categoryRepositoryImpl: com.trezanix.mytreza.data.repository.CategoryRepositoryImpl
    ): com.trezanix.mytreza.domain.repository.CategoryRepository
}