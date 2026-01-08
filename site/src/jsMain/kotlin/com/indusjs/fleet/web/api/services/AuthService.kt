package com.indusjs.fleet.web.api.services

import com.indusjs.fleet.web.api.ApiClient
import com.indusjs.fleet.web.api.ApiResult
import com.indusjs.fleet.web.api.safeApiCall
import com.indusjs.fleet.web.models.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.json.Json

/**
 * Authentication API service
 * Uses local proxy endpoints to avoid CORS issues
 */
object AuthService {

    private val client = ApiClient.httpClient
    private val json = Json { ignoreUnknownKeys = true }

    /**
     * Login user - uses local proxy
     */
    suspend fun login(identifier: String, password: String): ApiResult<ApiResponse<LoginResponseData>> {
        return safeApiCall {
            client.post("/api/auth/login") {
                setBody(LoginRequest(identifier, password))
            }
        }
    }

    /**
     * Signup new owner - uses local proxy
     */
    suspend fun signup(
        email: String,
        mobile: String,
        password: String,
        firstName: String,
        lastName: String
    ): ApiResult<ApiResponse<LoginResponseData>> {
        return safeApiCall {
            client.post("/api/auth/signup") {
                setBody(SignupRequest(email, mobile, password, firstName, lastName))
            }
        }
    }

    /**
     * Forgot password - uses local proxy
     */
    suspend fun forgotPassword(email: String): ApiResult<ApiResponse<Unit>> {
        return safeApiCall {
            client.post("/api/auth/forgot-password") {
                setBody(ForgotPasswordRequest(email))
            }
        }
    }

    /**
     * Reset password
     */
    suspend fun resetPassword(token: String, password: String): ApiResult<ApiResponse<Unit>> {
        return safeApiCall {
            client.post("/api/auth/reset-password") {
                setBody(ResetPasswordRequest(token, password))
            }
        }
    }

    /**
     * Get current user profile
     */
    suspend fun getProfile(): ApiResult<ApiResponse<User>> {
        return safeApiCall {
            client.get("/api/profile")
        }
    }

    /**
     * Update profile
     */
    suspend fun updateProfile(
        firstName: String? = null,
        lastName: String? = null,
        mobile: String? = null
    ): ApiResult<ApiResponse<User>> {
        return safeApiCall {
            client.put("/api/profile") {
                setBody(UpdateProfileRequest(firstName, lastName, mobile))
            }
        }
    }

    /**
     * Change password
     */
    suspend fun changePassword(
        currentPassword: String,
        newPassword: String
    ): ApiResult<ApiResponse<Unit>> {
        return safeApiCall {
            client.post("/api/profile/change-password") {
                setBody(ChangePasswordRequest(currentPassword, newPassword))
            }
        }
    }

    /**
     * Handle successful login - save token and user data
     */
    fun handleLoginSuccess(data: LoginResponseData) {
        ApiClient.saveToken(data.token)
        ApiClient.saveUserData(json.encodeToString(User.serializer(), data.user))
    }

    /**
     * Logout - clear stored data
     */
    fun logout() {
        ApiClient.clearToken()
    }

    /**
     * Check if user is logged in
     */
    fun isLoggedIn(): Boolean {
        return ApiClient.isAuthenticated()
    }

    /**
     * Get current user from storage
     */
    fun getCurrentUser(): User? {
        return try {
            ApiClient.getUserData()?.let {
                json.decodeFromString(User.serializer(), it)
            }
        } catch (e: Exception) {
            null
        }
    }
}

