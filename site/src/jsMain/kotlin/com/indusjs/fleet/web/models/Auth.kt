package com.indusjs.fleet.web.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * User roles enum
 */
enum class UserRole {
    OWNER,
    MANAGER,
    SUPERVISOR
}

/**
 * User model
 */
@Serializable
data class User(
    val id: Long = 0,
    val email: String = "",
    val mobile: String? = null,
    @SerialName("first_name") val firstName: String = "",
    @SerialName("last_name") val lastName: String = "",
    val role: String = "",
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
) {
    val fullName: String get() = "$firstName $lastName".trim()

    val userRole: UserRole get() = when (role.lowercase()) {
        "owner" -> UserRole.OWNER
        "manager" -> UserRole.MANAGER
        "supervisor" -> UserRole.SUPERVISOR
        else -> UserRole.SUPERVISOR
    }
}

/**
 * Login request
 */
@Serializable
data class LoginRequest(
    val identifier: String,
    val password: String
)

/**
 * Login response data
 */
@Serializable
data class LoginResponseData(
    val token: String,
    val user: User
)

/**
 * Signup request
 */
@Serializable
data class SignupRequest(
    val email: String,
    val mobile: String,
    val password: String,
    @SerialName("first_name") val firstName: String,
    @SerialName("last_name") val lastName: String
)

/**
 * Forgot password request
 */
@Serializable
data class ForgotPasswordRequest(
    val email: String
)

/**
 * Reset password request
 */
@Serializable
data class ResetPasswordRequest(
    val token: String,
    val password: String
)

/**
 * Change password request
 */
@Serializable
data class ChangePasswordRequest(
    @SerialName("current_password") val currentPassword: String,
    @SerialName("new_password") val newPassword: String
)

/**
 * Update profile request
 */
@Serializable
data class UpdateProfileRequest(
    @SerialName("first_name") val firstName: String? = null,
    @SerialName("last_name") val lastName: String? = null,
    val mobile: String? = null
)

