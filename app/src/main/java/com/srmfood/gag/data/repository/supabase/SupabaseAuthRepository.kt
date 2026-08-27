package com.srmfood.gag.data.repository.supabase

import com.srmfood.gag.core.security.TokenManager
import com.srmfood.gag.data.local.dao.UserDao
import com.srmfood.gag.data.local.entity.UserEntity
import com.srmfood.gag.domain.model.User
import com.srmfood.gag.domain.model.UserRole
import com.srmfood.gag.domain.repository.AuthRepository
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import javax.inject.Inject
import javax.inject.Singleton

@Serializable
data class ProfileDto(
    val id: String,
    val name: String,
    val email: String,
    val phone: String? = null,
    val role: String,
    val profile_image_url: String? = null,
    val registration_number: String? = null,
    val is_active: Boolean = true,
    val created_at: String
)

@Singleton
class SupabaseAuthRepository @Inject constructor(
    private val auth: Auth,
    private val postgrest: Postgrest,
    private val userDao: UserDao,
    private val tokenManager: TokenManager
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<User> = runCatching {
        auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
        
        val userSession = auth.currentSessionOrNull() ?: throw Exception("Login failed, no session")
        tokenManager.saveTokens(userSession.accessToken, userSession.refreshToken ?: "")
        
        val profile = postgrest["profiles"].select {
            filter { eq("id", userSession.user?.id ?: "") }
        }.decodeSingle<ProfileDto>()
        
        val domainUser = User(
            id = profile.id,
            name = profile.name,
            email = profile.email,
            phone = profile.phone,
            role = UserRole.fromString(profile.role),
            profileImageUrl = profile.profile_image_url,
            registrationNumber = profile.registration_number,
            isActive = profile.is_active,
            createdAt = profile.created_at
        )
        
        userDao.insertUser(UserEntity(
            id = domainUser.id,
            name = domainUser.name,
            email = domainUser.email,
            phone = domainUser.phone,
            role = domainUser.role.name,
            profileImageUrl = domainUser.profileImageUrl,
            registrationNumber = domainUser.registrationNumber,
            isActive = domainUser.isActive,
            createdAt = domainUser.createdAt
        ))
        
        domainUser
    }

    override suspend fun register(
        name: String,
        email: String,
        password: String,
        phone: String?,
        registrationNumber: String?
    ): Result<User> = runCatching {
        auth.signUpWith(Email) {
            this.email = email
            this.password = password
        }
        
        val userSession = auth.currentSessionOrNull() ?: throw Exception("Registration failed, no session")
        tokenManager.saveTokens(userSession.accessToken, userSession.refreshToken ?: "")
        
        val newProfile = ProfileDto(
            id = userSession.user?.id ?: "",
            name = name,
            email = email,
            phone = phone,
            role = "STUDENT",
            registration_number = registrationNumber,
            created_at = ""
        )
        
        // Let trigger or RLS handle insert, but usually auth.users creates the profile via trigger.
        // Wait, we didn't add a trigger for auth.users -> profiles in Phase 1. 
        // We will insert it directly here for now using service role or allow users to insert their own profile.
        // For simplicity, let's just insert it.
        val insertedProfile = postgrest["profiles"].insert(newProfile) {
            select()
        }.decodeSingle<ProfileDto>()
        
        val domainUser = User(
            id = insertedProfile.id,
            name = insertedProfile.name,
            email = insertedProfile.email,
            phone = insertedProfile.phone,
            role = UserRole.fromString(insertedProfile.role),
            profileImageUrl = insertedProfile.profile_image_url,
            registrationNumber = insertedProfile.registration_number,
            isActive = insertedProfile.is_active,
            createdAt = insertedProfile.created_at
        )
        
        userDao.insertUser(UserEntity(
            id = domainUser.id,
            name = domainUser.name,
            email = domainUser.email,
            phone = domainUser.phone,
            role = domainUser.role.name,
            profileImageUrl = domainUser.profileImageUrl,
            registrationNumber = domainUser.registrationNumber,
            isActive = domainUser.isActive,
            createdAt = domainUser.createdAt
        ))
        
        domainUser
    }

    override suspend fun logout() {
        runCatching { auth.signOut() }
        tokenManager.clearTokens()
        userDao.clearAll()
    }

    override fun getCurrentUser(): Flow<User?> {
        return userDao.observeCurrentUser().map { entity ->
            entity?.let {
                User(
                    id = it.id,
                    name = it.name,
                    email = it.email,
                    phone = it.phone,
                    role = UserRole.fromString(it.role),
                    profileImageUrl = it.profileImageUrl,
                    registrationNumber = it.registrationNumber,
                    isActive = it.isActive,
                    createdAt = it.createdAt
                )
            }
        }
    }

    override suspend fun isLoggedIn(): Boolean {
        return tokenManager.isLoggedIn()
    }

    override suspend fun getUserRole(): UserRole? {
        val userEntity = userDao.observeCurrentUser().firstOrNull()
        return userEntity?.let { UserRole.fromString(it.role) }
    }

    override suspend fun updateFcmToken(token: String): Result<Unit> = runCatching {
        // Will update in profiles table if needed.
    }
}
