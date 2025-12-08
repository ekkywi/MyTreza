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
}