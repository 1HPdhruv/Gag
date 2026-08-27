package com.srmfood.gag.domain.repository

import com.srmfood.gag.domain.model.FoodCategory
import com.srmfood.gag.domain.model.FoodItem
import com.srmfood.gag.domain.model.FoodSearchFilter
import kotlinx.coroutines.flow.Flow

interface FoodRepository {
    suspend fun searchFood(filter: FoodSearchFilter): Result<List<FoodItem>>
    suspend fun getFoodById(foodId: String): Result<FoodItem>
    suspend fun getMenuByOutlet(outletId: String): Result<List<FoodItem>>
    suspend fun getPopularFood(): Result<List<FoodItem>>
    suspend fun getRecommendedFood(): Result<List<FoodItem>>
    suspend fun getCategories(): Result<List<FoodCategory>>
    fun getFavorites(): Flow<List<FoodItem>>
    suspend fun toggleFavorite(foodItemId: String): Result<Boolean>
    suspend fun isFavorite(foodItemId: String): Boolean
}
