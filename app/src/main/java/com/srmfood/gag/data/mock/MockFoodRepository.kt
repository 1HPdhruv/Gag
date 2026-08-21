package com.srmfood.gag.data.mock

import com.srmfood.gag.domain.model.FoodCategory
import com.srmfood.gag.domain.model.FoodItem
import com.srmfood.gag.domain.model.FoodSearchFilter
import com.srmfood.gag.domain.model.SortOption
import com.srmfood.gag.domain.repository.FoodRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockFoodRepository @Inject constructor() : FoodRepository {

    private val favoriteIds = MutableStateFlow(setOf<String>())
    private val allFoodItems = MutableStateFlow(MockData.foodItems)

    override suspend fun searchFood(filter: FoodSearchFilter): Result<List<FoodItem>> {
        delay(500)
        var results = allFoodItems.value

        if (filter.query.isNotBlank()) {
            results = results.filter {
                it.name.contains(filter.query, ignoreCase = true) ||
                        it.description.contains(filter.query, ignoreCase = true) ||
                        it.tags.any { tag -> tag.contains(filter.query, ignoreCase = true) }
            }
        }
        filter.category?.let { cat -> results = results.filter { it.category.equals(cat, ignoreCase = true) } }
        filter.outletId?.let { id -> results = results.filter { it.outletId == id } }
        filter.isVeg?.let { veg -> results = results.filter { it.isVeg == veg } }
        filter.maxPrice?.let { max -> results = results.filter { it.price <= max } }
        filter.minRating?.let { min -> results = results.filter { it.rating >= min } }
        filter.maxPrepTimeMinutes?.let { max -> results = results.filter { it.prepTimeMinutes <= max } }
        if (filter.availableOnly) results = results.filter { it.isAvailable }

        results = when (filter.sortBy) {
            SortOption.PRICE_LOW_TO_HIGH -> results.sortedBy { it.price }
            SortOption.PRICE_HIGH_TO_LOW -> results.sortedByDescending { it.price }
            SortOption.RATING -> results.sortedByDescending { it.rating }
            SortOption.PREP_TIME -> results.sortedBy { it.prepTimeMinutes }
            SortOption.RELEVANCE -> results
        }
        return Result.success(results)
    }

    override suspend fun getFoodById(foodId: String): Result<FoodItem> {
        delay(200)
        val item = allFoodItems.value.find { it.id == foodId }
        return if (item != null) Result.success(item)
        else Result.failure(Exception("Food item not found"))
    }

    override suspend fun getMenuByOutlet(outletId: String): Result<List<FoodItem>> {
        delay(400)
        return Result.success(allFoodItems.value.filter { it.outletId == outletId })
    }

    override suspend fun getPopularFood(): Result<List<FoodItem>> {
        delay(300)
        return Result.success(allFoodItems.value.filter { it.isPopular && it.isAvailable })
    }

    override suspend fun getRecommendedFood(): Result<List<FoodItem>> {
        delay(300)
        return Result.success(allFoodItems.value.filter { it.isRecommended && it.isAvailable })
    }

    override suspend fun getCategories(): Result<List<FoodCategory>> {
        delay(200)
        return Result.success(MockData.categories)
    }

    override fun getFavorites(): Flow<List<FoodItem>> =
        favoriteIds.map { ids -> allFoodItems.value.filter { it.id in ids } }

    override suspend fun toggleFavorite(foodItemId: String): Result<Boolean> {
        val current = favoriteIds.value.toMutableSet()
        val isNowFavorite = if (foodItemId in current) {
            current.remove(foodItemId); false
        } else {
            current.add(foodItemId); true
        }
        favoriteIds.value = current
        return Result.success(isNowFavorite)
    }

    override suspend fun isFavorite(foodItemId: String): Boolean =
        foodItemId in favoriteIds.value
}
