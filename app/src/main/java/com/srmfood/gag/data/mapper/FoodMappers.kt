package com.srmfood.gag.data.mapper

import com.srmfood.gag.data.local.entity.FoodItemEntity
import com.srmfood.gag.data.remote.dto.CustomizationOptionDto
import com.srmfood.gag.data.remote.dto.FoodCustomizationDto
import com.srmfood.gag.data.remote.dto.FoodItemDto
import com.srmfood.gag.domain.model.CustomizationOption
import com.srmfood.gag.domain.model.FoodCustomization
import com.srmfood.gag.domain.model.FoodItem
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun FoodItemDto.toDomain(): FoodItem = FoodItem(
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
    ingredients = ingredients,
    customizations = customizations.map { it.toDomain() },
    tags = tags,
    calories = calories,
    isPopular = isPopular,
    isRecommended = isRecommended
)

fun FoodItemDto.toEntity(): FoodItemEntity = FoodItemEntity(
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
    isRecommended = isRecommended
)

fun FoodItemEntity.toDomain(customizations: List<FoodCustomization> = emptyList()): FoodItem = FoodItem(
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
    ingredients = try { Json.decodeFromString(ingredients) } catch (e: Exception) { emptyList() },
    customizations = customizations, // Entities don't store full customizations array to avoid complex nested tables for cache, so default to empty
    tags = try { Json.decodeFromString(tags) } catch (e: Exception) { emptyList() },
    calories = calories,
    isPopular = isPopular,
    isRecommended = isRecommended,
    isFavorite = isFavorite
)

fun FoodCustomizationDto.toDomain(): FoodCustomization = FoodCustomization(
    id = id,
    name = name,
    options = options.map { it.toDomain() },
    isRequired = isRequired,
    maxSelections = maxSelections
)

fun CustomizationOptionDto.toDomain(): CustomizationOption = CustomizationOption(
    id = id,
    name = name,
    extraPrice = extraPrice
)

fun com.srmfood.gag.data.remote.dto.FoodCategoryDto.toDomain(): com.srmfood.gag.domain.model.FoodCategory = com.srmfood.gag.domain.model.FoodCategory(
    id = id,
    name = name,
    emoji = emoji,
    imageUrl = imageUrl
)
