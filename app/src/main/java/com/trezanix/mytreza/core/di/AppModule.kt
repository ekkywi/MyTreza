package com.trezanix.mytreza.core.di

import com.trezanix.mytreza.data.local.TokenManager
import com.trezanix.mytreza.data.remote.AuthInterceptor
import com.trezanix.mytreza.data.remote.api.MyTrezaApiService
import com.trezanix.mytreza.data.repository.WalletRepositoryImpl
import com.trezanix.mytreza.domain.repository.WalletRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    @Provides
    @Singleton
    fun provideMyTrezaApi(client: OkHttpClient): MyTrezaApiService {
        return Retrofit.Builder()
            .baseUrl("http://localhost:3000/api/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MyTrezaApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideWalletRepository(api: MyTrezaApiService): WalletRepository {
        return WalletRepositoryImpl(api)
    }
}