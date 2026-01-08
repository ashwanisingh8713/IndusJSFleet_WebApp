package com.indusjs.fleet.web.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Trip state enum
 */
enum class TripState {
    PLANNED,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED
}

/**
 * Trip priority enum
 */
enum class TripPriority {
    LOW,
    NORMAL,
    HIGH,
    URGENT
}

/**
 * Trip list item (for list views)
 */
@Serializable
data class TripListItem(
    val id: Long = 0,
    @SerialName("vehicle_id") val vehicleId: Long = 0,
    @SerialName("driver_id") val driverId: Long = 0,
    @SerialName("vehicle_number") val vehicleNumber: String? = null,
    @SerialName("driver_name") val driverName: String? = null,
    val state: String = "planned",
    @SerialName("state_label") val stateLabel: String? = null,
    @SerialName("start_location") val startLocation: String = "",
    @SerialName("end_location") val endLocation: String = "",
    @SerialName("scheduled_date") val scheduledDate: String? = null,
    @SerialName("estimated_distance") val estimatedDistance: Double? = null,
    @SerialName("estimated_distance_label") val estimatedDistanceLabel: String? = null,
    @SerialName("estimated_duration_minutes") val estimatedDurationMinutes: Int? = null,
    @SerialName("estimated_duration_label") val estimatedDurationLabel: String? = null,
    @SerialName("distance_display") val distanceDisplay: String? = null,
    @SerialName("duration_display") val durationDisplay: String? = null,
    @SerialName("cargo_type") val cargoType: String? = null,
    @SerialName("cargo_type_label") val cargoTypeLabel: String? = null,
    @SerialName("total_cost") val totalCost: Double? = null,
    @SerialName("total_cost_label") val totalCostLabel: String? = null,
    @SerialName("has_costs") val hasCosts: Boolean = false,
    val priority: String? = null,
    @SerialName("customer_name") val customerName: String? = null
) {
    val tripState: TripState get() = when (state.lowercase()) {
        "planned" -> TripState.PLANNED
        "in_progress" -> TripState.IN_PROGRESS
        "completed" -> TripState.COMPLETED
        "cancelled" -> TripState.CANCELLED
        else -> TripState.PLANNED
    }
}

/**
 * Full trip model
 */
@Serializable
data class Trip(
    val id: Long = 0,
    @SerialName("vehicle_id") val vehicleId: Long = 0,
    @SerialName("driver_id") val driverId: Long = 0,
    val vehicle: Vehicle? = null,
    val driver: Driver? = null,
    val state: String = "planned",
    @SerialName("scheduled_date") val scheduledDate: String? = null,
    @SerialName("start_time") val startTime: String? = null,
    @SerialName("delivery_date") val deliveryDate: String? = null,
    @SerialName("delivery_time") val deliveryTime: String? = null,
    @SerialName("planned_start") val plannedStart: String? = null,
    @SerialName("planned_end") val plannedEnd: String? = null,
    @SerialName("actual_start") val actualStart: String? = null,
    @SerialName("actual_end") val actualEnd: String? = null,
    @SerialName("start_location") val startLocation: String = "",
    @SerialName("start_lat") val startLat: Double = 0.0,
    @SerialName("start_lng") val startLng: Double = 0.0,
    @SerialName("end_location") val endLocation: String = "",
    @SerialName("end_lat") val endLat: Double = 0.0,
    @SerialName("end_lng") val endLng: Double = 0.0,
    @SerialName("current_lat") val currentLat: Double? = null,
    @SerialName("current_lng") val currentLng: Double? = null,
    @SerialName("estimated_distance") val estimatedDistance: Double? = null,
    @SerialName("covered_distance") val coveredDistance: Double? = null,
    @SerialName("actual_distance") val actualDistance: Double? = null,
    @SerialName("cargo_type") val cargoType: String? = null,
    @SerialName("cargo_description") val cargoDescription: String? = null,
    @SerialName("cargo_loading_weight") val cargoLoadingWeight: Double? = null,
    @SerialName("cargo_unloading_weight") val cargoUnloadingWeight: Double? = null,
    @SerialName("vehicle_weight") val vehicleWeight: Double? = null,
    @SerialName("weight_unit") val weightUnit: String? = null,
    @SerialName("fuel_type") val fuelType: String? = null,
    @SerialName("filled_fuel_quantity") val filledFuelQuantity: Double? = null,
    @SerialName("used_fuel_quantity") val usedFuelQuantity: Double? = null,
    @SerialName("fuel_rate") val fuelRate: Double? = null,
    @SerialName("km_per_liter") val kmPerLiter: Double? = null,
    @SerialName("purchase_price") val purchasePrice: Double? = null,
    @SerialName("selling_value") val sellingValue: Double? = null,
    @SerialName("estimated_expense") val estimatedExpense: Double? = null,
    @SerialName("payment_status") val paymentStatus: String? = null,
    @SerialName("pending_amount") val pendingAmount: Double? = null,
    @SerialName("payment_mode") val paymentMode: String? = null,
    @SerialName("customer_name") val customerName: String? = null,
    @SerialName("customer_contact") val customerContact: String? = null,
    @SerialName("customer_email") val customerEmail: String? = null,
    @SerialName("customer_address") val customerAddress: String? = null,
    val priority: String? = null,
    val notes: String? = null,
    @SerialName("special_instructions") val specialInstructions: String? = null,
    @SerialName("display_info") val displayInfo: TripDisplayInfo? = null,
    @SerialName("owner_id") val ownerId: Long = 0,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
) {
    val tripState: TripState get() = when (state.lowercase()) {
        "planned" -> TripState.PLANNED
        "in_progress" -> TripState.IN_PROGRESS
        "completed" -> TripState.COMPLETED
        "cancelled" -> TripState.CANCELLED
        else -> TripState.PLANNED
    }
}

/**
 * Trip display info (state-based)
 */
@Serializable
data class TripDisplayInfo(
    @SerialName("distance_info") val distanceInfo: DistanceInfo? = null,
    @SerialName("duration_info") val durationInfo: DurationInfo? = null,
    @SerialName("cargo_info") val cargoInfo: CargoInfo? = null,
    @SerialName("cost_info") val costInfo: CostInfo? = null,
    @SerialName("progress_info") val progressInfo: ProgressInfo? = null
)

@Serializable
data class DistanceInfo(
    @SerialName("estimated_distance") val estimatedDistance: Double? = null,
    @SerialName("estimated_distance_label") val estimatedDistanceLabel: String? = null,
    @SerialName("covered_distance") val coveredDistance: Double? = null,
    @SerialName("covered_distance_label") val coveredDistanceLabel: String? = null,
    @SerialName("total_distance") val totalDistance: Double? = null,
    @SerialName("total_distance_label") val totalDistanceLabel: String? = null,
    @SerialName("display_value") val displayValue: String? = null,
    @SerialName("display_label") val displayLabel: String? = null
)

@Serializable
data class DurationInfo(
    @SerialName("planned_duration_minutes") val plannedDurationMinutes: Int? = null,
    @SerialName("planned_duration_label") val plannedDurationLabel: String? = null,
    @SerialName("actual_duration_minutes") val actualDurationMinutes: Int? = null,
    @SerialName("actual_duration_label") val actualDurationLabel: String? = null,
    @SerialName("display_value") val displayValue: String? = null,
    @SerialName("display_label") val displayLabel: String? = null
)

@Serializable
data class CargoInfo(
    @SerialName("cargo_type") val cargoType: String? = null,
    @SerialName("cargo_type_label") val cargoTypeLabel: String? = null,
    @SerialName("cargo_description") val cargoDescription: String? = null,
    @SerialName("loading_weight") val loadingWeight: Double? = null,
    @SerialName("loading_weight_label") val loadingWeightLabel: String? = null,
    @SerialName("weight_unit") val weightUnit: String? = null
)

@Serializable
data class CostInfo(
    @SerialName("has_costs") val hasCosts: Boolean = false,
    @SerialName("total_cost") val totalCost: Double? = null,
    @SerialName("total_cost_label") val totalCostLabel: String? = null,
    @SerialName("cost_count") val costCount: Int = 0,
    @SerialName("fuel_cost") val fuelCost: Double? = null,
    @SerialName("toll_cost") val tollCost: Double? = null,
    @SerialName("other_costs") val otherCosts: Double? = null
)

@Serializable
data class ProgressInfo(
    @SerialName("progress_percent") val progressPercent: Double? = null,
    @SerialName("progress_percent_label") val progressPercentLabel: String? = null,
    @SerialName("remaining_distance") val remainingDistance: Double? = null,
    @SerialName("remaining_distance_label") val remainingDistanceLabel: String? = null,
    @SerialName("estimated_arrival") val estimatedArrival: String? = null,
    @SerialName("estimated_arrival_label") val estimatedArrivalLabel: String? = null
)

/**
 * Create trip request
 */
@Serializable
data class CreateTripRequest(
    @SerialName("vehicle_id") val vehicleId: Long,
    @SerialName("driver_id") val driverId: Long,
    @SerialName("scheduled_date") val scheduledDate: String,
    @SerialName("start_time") val startTime: String,
    @SerialName("delivery_date") val deliveryDate: String? = null,
    @SerialName("delivery_time") val deliveryTime: String? = null,
    @SerialName("planned_start") val plannedStart: String? = null,
    @SerialName("planned_end") val plannedEnd: String? = null,
    @SerialName("start_location") val startLocation: String,
    @SerialName("start_lat") val startLat: Double,
    @SerialName("start_lng") val startLng: Double,
    @SerialName("end_location") val endLocation: String,
    @SerialName("end_lat") val endLat: Double,
    @SerialName("end_lng") val endLng: Double,
    @SerialName("estimated_distance") val estimatedDistance: Double? = null,
    @SerialName("cargo_type") val cargoType: String,
    @SerialName("cargo_description") val cargoDescription: String? = null,
    @SerialName("cargo_loading_weight") val cargoLoadingWeight: Double? = null,
    @SerialName("cargo_unloading_weight") val cargoUnloadingWeight: Double? = null,
    @SerialName("vehicle_weight") val vehicleWeight: Double? = null,
    @SerialName("weight_unit") val weightUnit: String? = null,
    @SerialName("fuel_type") val fuelType: String? = null,
    @SerialName("filled_fuel_quantity") val filledFuelQuantity: Double? = null,
    @SerialName("fuel_rate") val fuelRate: Double? = null,
    @SerialName("km_per_liter") val kmPerLiter: Double? = null,
    @SerialName("purchase_price") val purchasePrice: Double? = null,
    @SerialName("selling_value") val sellingValue: Double? = null,
    @SerialName("estimated_expense") val estimatedExpense: Double? = null,
    @SerialName("payment_status") val paymentStatus: String? = null,
    @SerialName("pending_amount") val pendingAmount: Double? = null,
    @SerialName("payment_mode") val paymentMode: String? = null,
    @SerialName("customer_name") val customerName: String? = null,
    @SerialName("customer_contact") val customerContact: String? = null,
    val priority: String? = null
)

/**
 * Update trip state request
 */
@Serializable
data class TripStateRequest(
    val state: String
)

/**
 * Update trip location request
 */
@Serializable
data class TripLocationRequest(
    @SerialName("current_lat") val currentLat: Double,
    @SerialName("current_lng") val currentLng: Double
)

/**
 * Update trip progress request
 */
@Serializable
data class TripProgressRequest(
    @SerialName("covered_distance") val coveredDistance: Double? = null,
    @SerialName("covered_duration_minutes") val coveredDurationMinutes: Int? = null,
    @SerialName("current_lat") val currentLat: Double? = null,
    @SerialName("current_lng") val currentLng: Double? = null
)

/**
 * Trip stop
 */
@Serializable
data class TripStop(
    val id: Long = 0,
    @SerialName("trip_id") val tripId: Long = 0,
    @SerialName("stop_order") val stopOrder: Int = 0,
    val location: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    @SerialName("arrival_time") val arrivalTime: String? = null,
    @SerialName("departure_time") val departureTime: String? = null,
    @SerialName("stop_duration") val stopDuration: Int? = null,
    val notes: String? = null,
    @SerialName("is_completed") val isCompleted: Boolean = false,
    @SerialName("completed_at") val completedAt: String? = null
)

/**
 * Create trip stop request
 */
@Serializable
data class CreateTripStopRequest(
    @SerialName("stop_order") val stopOrder: Int,
    val location: String,
    val latitude: Double,
    val longitude: Double,
    @SerialName("arrival_time") val arrivalTime: String? = null,
    @SerialName("stop_duration") val stopDuration: Int? = null,
    val notes: String? = null
)

