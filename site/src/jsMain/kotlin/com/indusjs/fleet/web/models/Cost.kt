package com.indusjs.fleet.web.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Trip cost type info
 */
@Serializable
data class CostType(
    val type: String,
    val label: String,
    val category: String
)

/**
 * Trip cost
 */
@Serializable
data class TripCost(
    val id: Long = 0,
    @SerialName("trip_id") val tripId: Long = 0,
    @SerialName("vehicle_id") val vehicleId: Long = 0,
    @SerialName("cost_type") val costType: String = "",
    @SerialName("cost_type_label") val costTypeLabel: String? = null,
    val amount: Double = 0.0,
    val date: String? = null,
    val time: String? = null,
    val notes: String? = null,
    @SerialName("custom_cost_type_name") val customCostTypeName: String? = null,
    // Fuel-specific fields
    @SerialName("fuel_type") val fuelType: String? = null,
    @SerialName("fuel_quantity") val fuelQuantity: Double? = null,
    @SerialName("fuel_rate") val fuelRate: Double? = null,
    @SerialName("km_per_liter") val kmPerLiter: Double? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)

/**
 * Create trip cost request
 */
@Serializable
data class CreateTripCostRequest(
    @SerialName("trip_id") val tripId: Long,
    @SerialName("vehicle_id") val vehicleId: Long,
    @SerialName("cost_type") val costType: String,
    val amount: Double,
    val date: String,
    val time: String? = null,
    val notes: String? = null,
    @SerialName("custom_cost_type_name") val customCostTypeName: String? = null,
    // Fuel-specific fields
    @SerialName("fuel_type") val fuelType: String? = null,
    @SerialName("fuel_quantity") val fuelQuantity: Double? = null,
    @SerialName("fuel_rate") val fuelRate: Double? = null,
    @SerialName("km_per_liter") val kmPerLiter: Double? = null
)

/**
 * Bulk create trip cost item
 */
@Serializable
data class BulkTripCostItem(
    @SerialName("cost_type") val costType: String,
    val amount: Double,
    val date: String,
    val time: String? = null,
    val notes: String? = null,
    @SerialName("custom_cost_type_name") val customCostTypeName: String? = null,
    @SerialName("fuel_type") val fuelType: String? = null,
    @SerialName("fuel_quantity") val fuelQuantity: Double? = null,
    @SerialName("fuel_rate") val fuelRate: Double? = null,
    @SerialName("km_per_liter") val kmPerLiter: Double? = null
)

/**
 * Bulk create trip costs request
 */
@Serializable
data class BulkTripCostRequest(
    val costs: List<BulkTripCostItem>
)

/**
 * Trip cost summary
 */
@Serializable
data class TripCostSummary(
    @SerialName("total_cost") val totalCost: Double = 0.0,
    @SerialName("fuel_cost") val fuelCost: Double = 0.0,
    @SerialName("toll_cost") val tollCost: Double = 0.0,
    @SerialName("other_costs") val otherCosts: Double = 0.0,
    @SerialName("cost_count") val costCount: Int = 0,
    @SerialName("last_updated") val lastUpdated: String? = null,
    val breakdown: Map<String, Double> = emptyMap()
)

/**
 * Maintenance cost type info
 */
@Serializable
data class MaintenanceCostType(
    val type: String,
    val label: String
)

/**
 * Maintenance cost
 */
@Serializable
data class MaintenanceCost(
    val id: Long = 0,
    @SerialName("vehicle_id") val vehicleId: Long = 0,
    @SerialName("cost_type") val costType: String = "",
    @SerialName("cost_type_label") val costTypeLabel: String? = null,
    val amount: Double = 0.0,
    val date: String? = null,
    val time: String? = null,
    val description: String? = null,
    val notes: String? = null,
    @SerialName("vendor_name") val vendorName: String? = null,
    @SerialName("invoice_no") val invoiceNo: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)

/**
 * Create maintenance cost request
 */
@Serializable
data class CreateMaintenanceCostRequest(
    @SerialName("vehicle_id") val vehicleId: Long,
    @SerialName("cost_type") val costType: String,
    val amount: Double,
    val date: String,
    val time: String? = null,
    val description: String? = null,
    val notes: String? = null,
    @SerialName("vendor_name") val vendorName: String? = null,
    @SerialName("invoice_no") val invoiceNo: String? = null
)

/**
 * Maintenance cost summary
 */
@Serializable
data class MaintenanceCostSummary(
    @SerialName("total_cost") val totalCost: Double = 0.0,
    @SerialName("cost_by_type") val costByType: Map<String, Double> = emptyMap(),
    @SerialName("last_maintenance") val lastMaintenance: String? = null,
    @SerialName("maintenance_count") val maintenanceCount: Int = 0
)

