package com.indusjs.fleet.web.state

import androidx.compose.runtime.*
import com.indusjs.fleet.web.api.services.AuthService
import com.indusjs.fleet.web.models.User
import com.indusjs.fleet.web.models.UserRole

/**
 * Global authentication state
 */
object AuthState {

    private val _isAuthenticated = mutableStateOf(AuthService.isLoggedIn())
    val isAuthenticated: State<Boolean> = _isAuthenticated

    private val _currentUser = mutableStateOf<User?>(AuthService.getCurrentUser())
    val currentUser: State<User?> = _currentUser

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    /**
     * Initialize auth state from storage
     */
    fun initialize() {
        _isAuthenticated.value = AuthService.isLoggedIn()
        _currentUser.value = AuthService.getCurrentUser()
    }

    /**
     * Set loading state
     */
    fun setLoading(loading: Boolean) {
        _isLoading.value = loading
    }

    /**
     * Update state after successful login
     */
    fun onLoginSuccess(user: User) {
        _currentUser.value = user
        _isAuthenticated.value = true
    }

    /**
     * Update user data (e.g., after profile update)
     */
    fun updateUser(user: User) {
        _currentUser.value = user
        // Also update the stored user data
        try {
            com.indusjs.fleet.web.api.ApiClient.saveUserData(
                kotlinx.serialization.json.Json.encodeToString(User.serializer(), user)
            )
        } catch (e: Exception) {
            console.log("Failed to save updated user data: ${e.message}")
        }
    }

    /**
     * Clear state on logout
     */
    fun logout() {
        AuthService.logout()
        _currentUser.value = null
        _isAuthenticated.value = false
    }

    /**
     * Clear state on logout (alias for backwards compatibility)
     */
    fun onLogout() {
        logout()
    }

    /**
     * Check if current user has required role
     */
    fun hasRole(vararg roles: UserRole): Boolean {
        val userRole = _currentUser.value?.userRole ?: return false
        return roles.contains(userRole)
    }

    /**
     * Check if user is owner
     */
    fun isOwner(): Boolean = hasRole(UserRole.OWNER)

    /**
     * Check if user is manager or owner
     */
    fun isManagerOrAbove(): Boolean = hasRole(UserRole.OWNER, UserRole.MANAGER)
}

