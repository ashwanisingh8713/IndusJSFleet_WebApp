package com.indusjs.fleet.web.api.services

import com.indusjs.fleet.web.api.ApiClient
import com.indusjs.fleet.web.api.ApiResult
import com.indusjs.fleet.web.api.safeApiCall
import com.indusjs.fleet.web.models.*
import io.ktor.client.request.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Report type info
 */
@Serializable
data class ReportType(
    val type: String,
    val label: String,
    val description: String? = null
)

/**
 * Report period info
 */
@Serializable
data class ReportPeriod(
    val period: String,
    val label: String
)

/**
 * Profit/Loss report
 */
@Serializable
data class ProfitLossReport(
    val period: String? = null,
    @SerialName("start_date") val startDate: String? = null,
    @SerialName("end_date") val endDate: String? = null,
    @SerialName("total_revenue") val totalRevenue: Double = 0.0,
    @SerialName("total_expenses") val totalExpenses: Double = 0.0,
    @SerialName("profit_loss") val profitLoss: Double = 0.0,
    @SerialName("profit_margin") val profitMargin: Double = 0.0,
    @SerialName("trip_count") val tripCount: Int = 0,
    @SerialName("expense_breakdown") val expenseBreakdown: ExpenseBreakdown? = null,
    @SerialName("vehicle_breakdown") val vehicleBreakdown: List<VehicleProfitLoss> = emptyList()
)

@Serializable
data class ExpenseBreakdown(
    @SerialName("fuel_cost") val fuelCost: Double = 0.0,
    @SerialName("toll_cost") val tollCost: Double = 0.0,
    @SerialName("maintenance_cost") val maintenanceCost: Double = 0.0,
    @SerialName("driver_allowance") val driverAllowance: Double = 0.0,
    @SerialName("other_costs") val otherCosts: Double = 0.0
)

@Serializable
data class VehicleProfitLoss(
    @SerialName("vehicle_id") val vehicleId: Long = 0,
    @SerialName("registration_number") val registrationNumber: String = "",
    val revenue: Double = 0.0,
    val expenses: Double = 0.0,
    @SerialName("profit_loss") val profitLoss: Double = 0.0,
    @SerialName("trip_count") val tripCount: Int = 0
)

/**
 * Reports API service
 */
object ReportService {

    private val client = ApiClient.httpClient

    /**
     * Get available report types
     */
    suspend fun getReportTypes(): ApiResult<ApiResponse<List<ReportType>>> {
        return safeApiCall {
            client.get("/api/reports/types")
        }
    }

    /**
     * Get available report periods
     */
    suspend fun getReportPeriods(): ApiResult<ApiResponse<List<ReportPeriod>>> {
        return safeApiCall {
            client.get("/api/reports/periods")
        }
    }

    /**
     * Get fleet profit/loss report
     */
    suspend fun getFleetProfitLoss(
        period: String? = null,
        startDate: String? = null,
        endDate: String? = null
    ): ApiResult<ApiResponse<ProfitLossReport>> {
        return safeApiCall {
            client.get("/api/reports/profit-loss") {
                period?.let { parameter("period", it) }
                startDate?.let { parameter("start_date", it) }
                endDate?.let { parameter("end_date", it) }
            }
        }
    }

    /**
     * Get vehicle profit/loss report
     */
    suspend fun getVehicleProfitLoss(
        vehicleId: Long,
        period: String? = null,
        startDate: String? = null,
        endDate: String? = null
    ): ApiResult<ApiResponse<ProfitLossReport>> {
        return safeApiCall {
            client.get("/api/vehicles/$vehicleId/profit-loss") {
                period?.let { parameter("period", it) }
                startDate?.let { parameter("start_date", it) }
                endDate?.let { parameter("end_date", it) }
            }
        }
    }
}

