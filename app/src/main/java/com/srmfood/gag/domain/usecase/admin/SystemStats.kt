package com.srmfood.gag.domain.usecase.admin

data class SystemStats(
    val totalUsers: Int,
    val ordersToday: Int,
    val revenueToday: Double
)
