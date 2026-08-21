package com.srmfood.gag.data.mock

import com.srmfood.gag.domain.model.Outlet
import com.srmfood.gag.domain.repository.OutletRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockOutletRepository @Inject constructor() : OutletRepository {

    private val _outlets = MutableStateFlow(MockData.outlets)

    override fun getOutlets(): Flow<List<Outlet>> = _outlets.asStateFlow()

    override suspend fun refreshOutlets(): Result<List<Outlet>> {
        delay(600)
        return Result.success(MockData.outlets)
    }

    override suspend fun getOutletById(outletId: String): Result<Outlet> {
        delay(300)
        val outlet = MockData.outlets.find { it.id == outletId }
        return if (outlet != null) Result.success(outlet)
        else Result.failure(Exception("Outlet not found"))
    }

    override suspend fun getNearbyOutlets(lat: Double, lng: Double): Result<List<Outlet>> {
        delay(400)
        return Result.success(MockData.outlets.filter { it.isOpen })
    }
}
