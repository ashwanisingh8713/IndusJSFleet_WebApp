package com.indusjs.fleet.web.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Driver status enum
 */
enum class DriverStatus {
    ACTIVE,
    INACTIVE,
    ON_LEAVE,
    SUSPENDED
}

/**
 * Driver model
 */
@Serializable
data class Driver(
    val id: Long = 0,
    @SerialName("first_name") val firstName: String = "",
    @SerialName("last_name") val lastName: String = "",
    val mobile: String = "",
    val email: String? = null,
    @SerialName("license_number") val licenseNumber: String = "",
    @SerialName("license_expiry") val licenseExpiry: String? = null,
    @SerialName("license_type") val licenseType: String? = null,
    @SerialName("date_of_birth") val dateOfBirth: String? = null,
    val address: String? = null,
    @SerialName("emergency_contact") val emergencyContact: String? = null,
    @SerialName("blood_group") val bloodGroup: String? = null,
    val status: String = "active",
    @SerialName("owner_id") val ownerId: Long = 0,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
) {
    val fullName: String get() = "$firstName $lastName".trim()

    val driverStatus: DriverStatus get() = when (status.lowercase()) {
        "active" -> DriverStatus.ACTIVE
        "inactive" -> DriverStatus.INACTIVE
        "on_leave" -> DriverStatus.ON_LEAVE
        "suspended" -> DriverStatus.SUSPENDED
        else -> DriverStatus.ACTIVE
    }
}

/**
 * Create/Update driver request
 */
@Serializable
data class DriverRequest(
    @SerialName("first_name") val firstName: String? = null,
    @SerialName("last_name") val lastName: String? = null,
    val mobile: String? = null,
    val email: String? = null,
    @SerialName("license_number") val licenseNumber: String? = null,
    @SerialName("license_expiry") val licenseExpiry: String? = null,
    @SerialName("license_type") val licenseType: String? = null,
    @SerialName("date_of_birth") val dateOfBirth: String? = null,
    val address: String? = null,
    @SerialName("emergency_contact") val emergencyContact: String? = null,
    @SerialName("blood_group") val bloodGroup: String? = null
)

/**
 * Driver status update request
 */
@Serializable
data class DriverStatusRequest(
    val status: String
)

