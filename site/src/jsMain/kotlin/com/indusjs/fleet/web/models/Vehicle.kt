package com.indusjs.fleet.web.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Vehicle state enum
 */
enum class VehicleState {
    ACTIVE,
    INACTIVE,
    MAINTENANCE
}

/**
 * Vehicle model
 */
@Serializable
data class Vehicle(
    val id: Long = 0,
    @SerialName("registration_number") val registrationNumber: String = "",
    val make: String = "",
    val model: String = "",
    val year: Int = 0,
    @SerialName("vehicle_type") val vehicleType: String = "",
    @SerialName("fuel_type") val fuelType: String = "",
    val capacity: Double = 0.0,
    val color: String? = null,
    val mileage: Double? = null,
    val state: String = "active",
    @SerialName("assigned_driver_id") val assignedDriverId: Long? = null,
    @SerialName("assigned_driver") val assignedDriver: Driver? = null,
    @SerialName("owner_id") val ownerId: Long = 0,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
) {
    val vehicleState: VehicleState get() = when (state.lowercase()) {
        "active" -> VehicleState.ACTIVE
        "inactive" -> VehicleState.INACTIVE
        "maintenance" -> VehicleState.MAINTENANCE
        else -> VehicleState.ACTIVE
    }

    val displayName: String get() = "$make $model ($registrationNumber)"
}

/**
 * Create/Update vehicle request
 */
@Serializable
data class VehicleRequest(
    @SerialName("registration_number") val registrationNumber: String? = null,
    val make: String? = null,
    val model: String? = null,
    val year: Int? = null,
    @SerialName("vehicle_type") val vehicleType: String? = null,
    @SerialName("fuel_type") val fuelType: String? = null,
    val capacity: Double? = null,
    val color: String? = null,
    val mileage: Double? = null,
    @SerialName("assigned_driver_id") val assignedDriverId: Long? = null
)

/**
 * Vehicle state update request
 */
@Serializable
data class VehicleStateRequest(
    val state: String
)

/**
 * Vehicle location
 */
@Serializable
data class VehicleLocation(
    @SerialName("vehicle_id") val vehicleId: Long = 0,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val speed: Double? = null,
    val heading: Double? = null,
    val altitude: Double? = null,
    val accuracy: Double? = null,
    @SerialName("recorded_at") val recordedAt: String? = null
)

/**
 * Vehicle overview for detail screen
 */
@Serializable
data class VehicleOverview(
    val vehicle: Vehicle,
    @SerialName("current_location") val currentLocation: VehicleLocation? = null,
    @SerialName("total_trips") val totalTrips: Int = 0,
    @SerialName("active_trips") val activeTrips: Int = 0,
    @SerialName("total_distance") val totalDistance: Double = 0.0,
    @SerialName("maintenance_count") val maintenanceCount: Int = 0
)

/**
 * Vehicle detail with full information
 */
@Serializable
data class VehicleDetail(
    val overview: VehicleOverview,
    @SerialName("recent_trips") val recentTrips: List<TripListItem> = emptyList(),
    @SerialName("documents_summary") val documentsSummary: DocumentsSummary? = null
)

/**
 * Documents summary
 */
@Serializable
data class DocumentsSummary(
    val total: Int = 0,
    val verified: Int = 0,
    val pending: Int = 0,
    val expired: Int = 0,
    val expiring: Int = 0
)

