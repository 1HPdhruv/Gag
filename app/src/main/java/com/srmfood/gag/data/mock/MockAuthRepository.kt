package com.srmfood.gag.data.mock

import com.srmfood.gag.core.security.TokenManager
import com.srmfood.gag.domain.model.User
import com.srmfood.gag.domain.model.UserRole
import com.srmfood.gag.domain.repository.AuthRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockAuthRepository @Inject constructor(
    private val tokenManager: TokenManager
) : AuthRepository {

    private val _currentUser = MutableStateFlow<User?>(null)

    init {
        // Restore session if token exists
        if (tokenManager.isLoggedIn()) {
            val role = tokenManager.getUserRole()?.let { UserRole.fromString(it) } ?: UserRole.STUDENT
            _currentUser.value = when (role) {
                UserRole.STUDENT -> MockData.mockStudent
                UserRole.VENDOR -> MockData.mockVendor
                UserRole.ADMIN -> MockData.mockAdmin
            }
        }
    }

    override suspend fun login(email: String, password: String): Result<User> {
        delay(800) // Simulate network
        val entry = MockData.credentials[email.lowercase()]
        return if (entry != null && entry.first == password) {
            val user = entry.second
            tokenManager.saveTokens("mock_access_token_${user.id}", "mock_refresh_token_${user.id}")
            tokenManager.saveUserInfo(user.id, user.name, user.email, user.role.name)
            _currentUser.value = user
            Result.success(user)
        } else {
            Result.failure(Exception("Invalid email or password"))
        }
    }

    override suspend fun register(
        name: String, email: String, password: String, phone: String?, registrationNumber: String?
    ): Result<User> {
        delay(1000)
        // In mock mode, always create a student account
        val user = MockData.mockStudent.copy(
            id = "student-new-${System.currentTimeMillis()}",
            name = name, email = email, phone = phone, registrationNumber = registrationNumber
        )
        tokenManager.saveTokens("mock_access_token_${user.id}", "mock_refresh_token_${user.id}")
        tokenManager.saveUserInfo(user.id, user.name, user.email, user.role.name)
        _currentUser.value = user
        return Result.success(user)
    }

    override suspend fun logout() {
        delay(200)
        tokenManager.clearTokens()
        _currentUser.value = null
    }

    override fun getCurrentUser(): Flow<User?> = _currentUser.asStateFlow()

    override suspend fun isLoggedIn(): Boolean = tokenManager.isLoggedIn()

    override suspend fun getUserRole(): UserRole? {
        val roleStr = tokenManager.getUserRole() ?: return null
        return UserRole.fromString(roleStr)
    }

    override suspend fun updateFcmToken(token: String): Result<Unit> {
        delay(200)
        return Result.success(Unit)
    }
}
