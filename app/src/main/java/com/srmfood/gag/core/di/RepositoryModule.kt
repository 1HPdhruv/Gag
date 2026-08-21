package com.srmfood.gag.core.di

import com.srmfood.gag.BuildConfig
import com.srmfood.gag.data.mock.MockAuthRepository
import com.srmfood.gag.data.mock.MockCartRepository
import com.srmfood.gag.data.mock.MockFoodRepository
import com.srmfood.gag.data.mock.MockOrderRepository
import com.srmfood.gag.data.mock.MockOutletRepository
import com.srmfood.gag.data.repository.AuthRepositoryImpl
import com.srmfood.gag.data.repository.CartRepositoryImpl
import com.srmfood.gag.data.repository.FoodRepositoryImpl
import com.srmfood.gag.data.repository.OrderRepositoryImpl
import com.srmfood.gag.data.repository.OutletRepositoryImpl
import com.srmfood.gag.domain.repository.AuthRepository
import com.srmfood.gag.domain.repository.CartRepository
import com.srmfood.gag.domain.repository.FoodRepository
import com.srmfood.gag.domain.repository.OrderRepository
import com.srmfood.gag.domain.repository.OutletRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Binds repository interfaces to their implementations.
 * In MOCK mode (BuildConfig.USE_MOCK = true), mock implementations are used.
 * In production, real implementations backed by Retrofit + Room are used.
 *
 * To switch: change USE_MOCK in BuildConfig or use a product flavor.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        @Suppress("UNUSED_PARAMETER")
        mock: MockAuthRepository
        // Replace with: impl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindOutletRepository(
        mock: MockOutletRepository
        // Replace with: impl: OutletRepositoryImpl
    ): OutletRepository

    @Binds
    @Singleton
    abstract fun bindFoodRepository(
        mock: MockFoodRepository
        // Replace with: impl: FoodRepositoryImpl
    ): FoodRepository

    @Binds
    @Singleton
    abstract fun bindCartRepository(
        mock: MockCartRepository
        // Replace with: impl: CartRepositoryImpl
    ): CartRepository

    @Binds
    @Singleton
    abstract fun bindOrderRepository(
        mock: MockOrderRepository
        // Replace with: impl: OrderRepositoryImpl
    ): OrderRepository
}
