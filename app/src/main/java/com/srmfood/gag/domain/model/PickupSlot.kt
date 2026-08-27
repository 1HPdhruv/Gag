package com.srmfood.gag.domain.model

import kotlinx.serialization.Serializable

/**
 * PickupSlot domain model.
 * Capacity is determined by the backend — client never decides.
 */
@Serializable
data class PickupSlot(
    val id: String,
    val outletId: String,
    val startTime: String,      // "12:30"
    val endTime: String,        // "12:40"
    val date: String,           // "2024-01-15"
    val capacity: Int,
    val bookedCount: Int,
    val status: SlotStatus
) {
    val availableCount: Int get() = capacity - bookedCount
    val displayTime: String get() = "$startTime – $endTime"
    val isSelectable: Boolean get() = status != SlotStatus.FULL
}

@Serializable
enum class SlotStatus {
    AVAILABLE, LIMITED, FULL;

    companion object {
        fun fromString(s: String): SlotStatus = when (s.uppercase()) {
            "FULL" -> FULL
            "LIMITED" -> LIMITED
            else -> AVAILABLE
        }
    }
}

/**
 * Notification domain model
 */
data class GagNotification(
    val id: String,
    val title: String,
    val body: String,
    val type: NotificationType,
    val orderId: String?,
    val isRead: Boolean,
    val createdAt: String,
    val deepLink: String?
)

enum class NotificationType {
    ORDER_ACCEPTED, ORDER_REJECTED, ORDER_PREPARING,
    ORDER_READY, ORDER_DELAYED, ORDER_CANCELLED, PICKUP_REMINDER,
    GENERAL;

    companion object {
        fun fromString(s: String): NotificationType = try {
            valueOf(s.uppercase())
        } catch (e: IllegalArgumentException) {
            GENERAL
        }
    }
}

/**
 * Review domain model
 */
data class Review(
    val id: String,
    val userId: String,
    val userName: String,
    val outletId: String?,
    val foodItemId: String?,
    val rating: Int,            // 1–5
    val comment: String?,
    val createdAt: String
)

/**
 * Favorite domain model
 */
data class Favorite(
    val foodItemId: String,
    val foodItem: FoodItem
)
