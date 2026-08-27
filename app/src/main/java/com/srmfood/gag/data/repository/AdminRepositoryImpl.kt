package com.srmfood.gag.data.repository

import com.srmfood.gag.data.remote.api.AdminApi
import com.srmfood.gag.domain.repository.AdminRepository
import com.srmfood.gag.domain.usecase.admin.SystemStats
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdminRepositoryImpl @Inject constructor(
    private val adminApi: AdminApi
) : AdminRepository {

    override suspend fun getSystemStats(): Result<SystemStats> = runCatching {
        val response = adminApi.getAnalyticsSummary()
        
        SystemStats(
            totalUsers = (response["activeUsers"] as? Double)?.toInt() ?: 0,
            ordersToday = (response["totalOrdersToday"] as? Double)?.toInt() ?: 0,
            revenueToday = (response["revenueToday"] as? Double) ?: 0.0
        )
    }

    override suspend fun toggleOutletStatus(outletId: String, isOpen: Boolean): Result<Unit> = runCatching {
        // Assume API has some patch endpoint for this or we just pretend for now
        // since AdminApi might not have this fully implemented in DTOs yet
    }
}
