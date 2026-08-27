package com.srmfood.gag.data.repository.supabase

import com.srmfood.gag.data.local.dao.FoodItemDao
import com.srmfood.gag.data.mapper.toDomain
import com.srmfood.gag.data.mapper.toEntity
import com.srmfood.gag.domain.model.FoodCategory
import com.srmfood.gag.domain.model.FoodItem
import com.srmfood.gag.domain.model.FoodSearchFilter
import com.srmfood.gag.domain.repository.FoodRepository
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Serializable
data class CategoryDto(
    val id: String,
    val name: String,
    val emoji: String,
    val image_url: String? = null
)

@Serializable
data class OutletRelationDto(val name: String)

@Serializable
data class CategoryRelationDto(val name: String)

@Serializable
data class FoodItemDto(
    val id: String,
    val outlet_id: String,
    val category_id: String,
    val name: String,
    val description: String,
    val image_url: String? = null,
    val price: Double,
    val is_veg: Boolean,
    val is_available: Boolean,
    val prep_time_minutes: Int,
    val calories: Int? = null,
    val ingredients: List<String>? = null,
    val tags: List<String>? = null,
    val is_popular: Boolean,
    val is_recommended: Boolean,
    val rating: Double,
    val total_reviews: Int,
    val outlets: OutletRelationDto? = null,
    val categories: CategoryRelationDto? = null
)

@Singleton
class SupabaseFoodRepository @Inject constructor(
    private val postgrest: Postgrest,
    private val foodDao: FoodItemDao
) : FoodRepository {

    override suspend fun searchFood(filter: FoodSearchFilter): Result<List<FoodItem>> = runCatching {
        val dtos = postgrest["food_items"].select {
            filter {
                if (filter.query.isNotBlank()) {
                    ilike("name", "%${filter.query}%")
                }
                filter.outletId?.let { eq("outlet_id", it) }
                filter.isVeg?.let { eq("is_veg", it) }
                if (filter.availableOnly) {
                    eq("is_available", true)
                }
                filter.maxPrice?.let { lte("price", it) }
                filter.minRating?.let { gte("rating", it) }
                filter.maxPrepTimeMinutes?.let { lte("prep_time_minutes", it) }
                // Category requires join or separate filter if UUID isn't passed (query uses name).
                // For simplicity, skip name-based category filter here unless we fetch categories first.
            }
            // Supabase Kotlin mapping for relations
            select(Columns.raw("*, outlets(name), categories(name)"))
        }.decodeList<FoodItemDto>()
        
        // Post-filter category if needed, since category filter is by string name in UI
        val filteredDtos = if (filter.category != null && filter.category != "All") {
            dtos.filter { it.categories?.name == filter.category }
        } else {
            dtos
        }

        // Sort locally for simplicity
        val sorted = when(filter.sortBy) {
            com.srmfood.gag.domain.model.SortOption.PRICE_LOW_TO_HIGH -> filteredDtos.sortedBy { it.price }
            com.srmfood.gag.domain.model.SortOption.PRICE_HIGH_TO_LOW -> filteredDtos.sortedByDescending { it.price }
            com.srmfood.gag.domain.model.SortOption.RATING -> filteredDtos.sortedByDescending { it.rating }
            com.srmfood.gag.domain.model.SortOption.PREP_TIME -> filteredDtos.sortedBy { it.prep_time_minutes }
            com.srmfood.gag.domain.model.SortOption.RELEVANCE -> filteredDtos // Not implemented
        }
        
        sorted.map { it.toDomain() }
    }

    override suspend fun getFoodById(foodId: String): Result<FoodItem> = runCatching {
        val local = foodDao.getFoodById(foodId)
        if (local != null) return@runCatching local.toDomain()

        val dto = postgrest["food_items"].select {
            filter { eq("id", foodId) }
            select(Columns.raw("*, outlets(name), categories(name)"))
        }.decodeSingle<FoodItemDto>()
        
        val domain: FoodItem = dto.toDomain()
        foodDao.insertAll(listOf(domain.toEntity()))
        domain
    }

    override suspend fun getMenuByOutlet(outletId: String): Result<List<FoodItem>> = runCatching {
        val dtos = postgrest["food_items"].select {
            filter { eq("outlet_id", outletId) }
            select(Columns.raw("*, outlets(name), categories(name)"))
        }.decodeList<FoodItemDto>()
        
        val domains: List<FoodItem> = dtos.map { it.toDomain() }
        foodDao.insertAll(domains.map { it.toEntity() })
        domains
    }

    override suspend fun getPopularFood(): Result<List<FoodItem>> = runCatching {
        val dtos = postgrest["food_items"].select {
            filter { eq("is_popular", true) }
            select(Columns.raw("*, outlets(name), categories(name)"))
        }.decodeList<FoodItemDto>()
        val domains: List<FoodItem> = dtos.map { it.toDomain() }
        domains
    }

    override suspend fun getRecommendedFood(): Result<List<FoodItem>> = runCatching {
        val dtos = postgrest["food_items"].select {
            filter { eq("is_recommended", true) }
            select(Columns.raw("*, outlets(name), categories(name)"))
        }.decodeList<FoodItemDto>()
        val domains: List<FoodItem> = dtos.map { it.toDomain() }
        domains
    }

    override suspend fun getCategories(): Result<List<FoodCategory>> = runCatching {
        val dtos = postgrest["categories"].select().decodeList<CategoryDto>()
        dtos.map { 
            FoodCategory(
                id = it.id,
                name = it.name,
                emoji = it.emoji,
                imageUrl = it.image_url
            )
        }
    }

    override fun getFavorites(): Flow<List<FoodItem>> {
        return foodDao.observeFavorites().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun toggleFavorite(foodItemId: String): Result<Boolean> = runCatching {
        val current = foodDao.isFavorite(foodItemId) ?: false
        val newStatus = !current
        foodDao.updateFavorite(foodItemId, newStatus)
        // Usually, we would also update a `favorites` table in Supabase here.
        newStatus
    }

    override suspend fun isFavorite(foodItemId: String): Boolean {
        return foodDao.isFavorite(foodItemId) ?: false
    }

    private fun FoodItemDto.toDomain(): FoodItem {
        return FoodItem(
            id = id,
            name = name,
            description = description,
            imageUrl = image_url,
            price = price,
            outletId = outlet_id,
            outletName = outlets?.name ?: "Unknown Outlet",
            category = categories?.name ?: "General",
            isVeg = is_veg,
            isAvailable = is_available,
            prepTimeMinutes = prep_time_minutes,
            rating = rating,
            totalReviews = total_reviews,
            ingredients = ingredients ?: emptyList(),
            customizations = emptyList(), // Can fetch from food_variants if needed
            tags = tags ?: emptyList(),
            calories = calories,
            isPopular = is_popular,
            isRecommended = is_recommended,
            isFavorite = false
        )
    }

    private fun FoodItem.toEntity(): com.srmfood.gag.data.local.entity.FoodItemEntity {
        return com.srmfood.gag.data.local.entity.FoodItemEntity(
            id = id,
            name = name,
            description = description,
            imageUrl = imageUrl,
            price = price,
            outletId = outletId,
            outletName = outletName,
            category = category,
            isVeg = isVeg,
            isAvailable = isAvailable,
            prepTimeMinutes = prepTimeMinutes,
            rating = rating,
            totalReviews = totalReviews,
            ingredients = Json.encodeToString(ingredients),
            tags = Json.encodeToString(tags),
            calories = calories,
            isPopular = isPopular,
            isRecommended = isRecommended,
            isFavorite = isFavorite
        )
    }
}
