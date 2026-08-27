package com.srmfood.gag.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FoodItemDto(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("description") val description: String = "",
    @SerialName("image_url") val imageUrl: String? = null,
    @SerialName("price") val price: Double,
    @SerialName("outlet_id") val outletId: String,
    @SerialName("outlet_name") val outletName: String = "",
    @SerialName("category") val category: String = "",
    @SerialName("is_veg") val isVeg: Boolean = true,
    @SerialName("is_available") val isAvailable: Boolean = true,
    @SerialName("prep_time_minutes") val prepTimeMinutes: Int = 10,
    @SerialName("rating") val rating: Double = 0.0,
    @SerialName("total_reviews") val totalReviews: Int = 0,
    @SerialName("ingredients") val ingredients: List<String> = emptyList(),
    @SerialName("customizations") val customizations: List<FoodCustomizationDto> = emptyList(),
    @SerialName("tags") val tags: List<String> = emptyList(),
    @SerialName("calories") val calories: Int? = null,
    @SerialName("is_popular") val isPopular: Boolean = false,
    @SerialName("is_recommended") val isRecommended: Boolean = false
)

@Serializable
data class FoodCustomizationDto(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("options") val options: List<CustomizationOptionDto>,
    @SerialName("is_required") val isRequired: Boolean = false,
    @SerialName("max_selections") val maxSelections: Int = 1
)

@Serializable
data class CustomizationOptionDto(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("extra_price") val extraPrice: Double = 0.0
)

@Serializable
data class FoodCategoryDto(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("emoji") val emoji: String = "",
    @SerialName("image_url") val imageUrl: String? = null
)

@Serializable
data class FoodSearchRequestDto(
    @SerialName("query") val query: String,
    @SerialName("category") val category: String? = null,
    @SerialName("outlet_id") val outletId: String? = null,
    @SerialName("is_veg") val isVeg: Boolean? = null,
    @SerialName("max_price") val maxPrice: Double? = null,
    @SerialName("min_rating") val minRating: Double? = null,
    @SerialName("max_prep_time") val maxPrepTime: Int? = null,
    @SerialName("available_only") val availableOnly: Boolean = true,
    @SerialName("sort_by") val sortBy: String = "relevance"
)
