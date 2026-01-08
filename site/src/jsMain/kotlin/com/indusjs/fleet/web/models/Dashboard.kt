package com.indusjs.fleet.web.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Dashboard response
 */
@Serializable
data class Dashboard(
    @SerialName("user_info") val userInfo: DashboardUserInfo? = null,
    @SerialName("fleet_overview") val fleetOverview: FleetOverview? = null,
    @SerialName("today_summary") val todaySummary: TodaySummary? = null,
    val alerts: List<DashboardAlert> = emptyList(),
    @SerialName("quick_actions") val quickActions: QuickActions? = null,
    @SerialName("live_status") val liveStatus: LiveStatus? = null,
    @SerialName("team_stats") val teamStats: TeamStats? = null,
    @SerialName("document_stats") val documentStats: DocumentStats? = null,
    @SerialName("total_alerts") val totalAlerts: Int = 0
)

@Serializable
data class DashboardUserInfo(
    val name: String = "",
    val role: String = "",
    val email: String = ""
)

@Serializable
data class FleetOverview(
    @SerialName("total_vehicles") val totalVehicles: Int = 0,
    @SerialName("active_vehicles") val activeVehicles: Int = 0,
    @SerialName("on_trip") val onTrip: Int = 0,
    @SerialName("maintenance") val maintenance: Int = 0,
    @SerialName("total_drivers") val totalDrivers: Int = 0,
    @SerialName("available_drivers") val availableDrivers: Int = 0,
    @SerialName("on_duty") val onDuty: Int = 0,
    @SerialName("on_leave") val onLeave: Int = 0
)

@Serializable
data class TodaySummary(
    @SerialName("trips_planned") val tripsPlanned: Int = 0,
    @SerialName("trips_in_progress") val tripsInProgress: Int = 0,
    @SerialName("trips_completed") val tripsCompleted: Int = 0,
    @SerialName("total_revenue") val totalRevenue: Double = 0.0,
    @SerialName("total_expenses") val totalExpenses: Double = 0.0,
    @SerialName("profit_loss") val profitLoss: Double = 0.0
)

@Serializable
data class DashboardAlert(
    val id: Long = 0,
    val type: String = "",
    val priority: String = "",
    val title: String = "",
    val message: String = "",
    @SerialName("entity_type") val entityType: String? = null,
    @SerialName("entity_id") val entityId: Long? = null,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class QuickActions(
    @SerialName("vehicles_count") val vehiclesCount: Int = 0,
    @SerialName("drivers_count") val driversCount: Int = 0,
    @SerialName("trips_count") val tripsCount: Int = 0,
    @SerialName("pending_payments_count") val pendingPaymentsCount: Int = 0,
    @SerialName("can_add_vehicle") val canAddVehicle: Boolean = true,
    @SerialName("can_add_driver") val canAddDriver: Boolean = true,
    @SerialName("can_add_trip") val canAddTrip: Boolean = true,
    @SerialName("can_manage_team") val canManageTeam: Boolean = false
)

@Serializable
data class LiveStatus(
    @SerialName("vehicles_moving") val vehiclesMoving: Int = 0,
    @SerialName("vehicles_idle") val vehiclesIdle: Int = 0,
    @SerialName("vehicles_stopped") val vehiclesStopped: Int = 0,
    @SerialName("last_updated") val lastUpdated: String? = null
)

@Serializable
data class TeamStats(
    @SerialName("total_managers") val totalManagers: Int = 0,
    @SerialName("total_supervisors") val totalSupervisors: Int = 0,
    @SerialName("active_members") val activeMembers: Int = 0
)

@Serializable
data class DocumentStats(
    @SerialName("expiring_soon") val expiringSoon: Int = 0,
    val expired: Int = 0,
    @SerialName("pending_verification") val pendingVerification: Int = 0
)

/**
 * Cost overview response
 */
@Serializable
data class CostOverview(
    @SerialName("total_expenses") val totalExpenses: Double = 0.0,
    @SerialName("total_profit_loss") val totalProfitLoss: Double = 0.0,
    @SerialName("completed_trips") val completedTrips: Int = 0,
    @SerialName("total_revenue") val totalRevenue: Double = 0.0,
    @SerialName("fuel_cost") val fuelCost: Double = 0.0,
    @SerialName("toll_cost") val tollCost: Double = 0.0,
    @SerialName("maintenance_cost") val maintenanceCost: Double = 0.0,
    @SerialName("other_costs") val otherCosts: Double = 0.0,
    @SerialName("profit_margin") val profitMargin: Double = 0.0,
    val filter: String = "today"
)

/**
 * Pending payment item
 */
@Serializable
data class PendingPayment(
    @SerialName("trip_id") val tripId: Long = 0,
    @SerialName("vehicle_registration") val vehicleRegistration: String = "",
    @SerialName("customer_name") val customerName: String? = null,
    @SerialName("selling_value") val sellingValue: Double = 0.0,
    @SerialName("partial_payment") val partialPayment: Double = 0.0,
    @SerialName("pending_amount") val pendingAmount: Double = 0.0,
    @SerialName("days_overdue") val daysOverdue: Int = 0,
    @SerialName("payment_status") val paymentStatus: String = ""
)

/**
 * Vehicle status summary
 */
@Serializable
data class VehicleStatusSummary(
    @SerialName("on_trip_planned") val onTripPlanned: Int = 0,
    @SerialName("on_trip_in_progress") val onTripInProgress: Int = 0,
    @SerialName("under_maintenance") val underMaintenance: Int = 0,
    val active: Int = 0,
    val inactive: Int = 0,
    val total: Int = 0
)

/**
 * Trips status summary
 */
@Serializable
data class TripsStatusSummary(
    @SerialName("in_progress") val inProgress: Int = 0,
    val planned: Int = 0,
    val delayed: Int = 0,
    @SerialName("completed_today") val completedToday: Int = 0,
    @SerialName("completed_total") val completedTotal: Int = 0,
    val cancelled: Int = 0,
    val total: Int = 0,
    @SerialName("recent_trips") val recentTrips: List<TripListItem> = emptyList()
)

/**
 * Drivers status summary
 */
@Serializable
data class DriversStatusSummary(
    @SerialName("on_trip_planned") val onTripPlanned: Int = 0,
    @SerialName("on_trip_in_progress") val onTripInProgress: Int = 0,
    val available: Int = 0,
    @SerialName("on_leave") val onLeave: Int = 0,
    val inactive: Int = 0,
    val total: Int = 0
)

/**
 * Alerts status summary
 */
@Serializable
data class AlertsStatusSummary(
    @SerialName("total_alerts") val totalAlerts: Int = 0,
    @SerialName("critical_alerts") val criticalAlerts: Int = 0,
    @SerialName("warning_alerts") val warningAlerts: Int = 0,
    @SerialName("info_alerts") val infoAlerts: Int = 0,
    @SerialName("document_expiring") val documentExpiring: Int = 0,
    @SerialName("license_expiring") val licenseExpiring: Int = 0,
    @SerialName("pending_payments") val pendingPayments: Int = 0,
    @SerialName("maintenance_vehicles") val maintenanceVehicles: Int = 0,
    val alerts: List<DashboardAlert> = emptyList()
)

