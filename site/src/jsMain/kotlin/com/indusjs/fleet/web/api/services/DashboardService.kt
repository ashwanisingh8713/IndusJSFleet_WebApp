package com.indusjs.fleet.web.api.services

import com.indusjs.fleet.web.api.ApiClient
import com.indusjs.fleet.web.api.ApiResult
import com.indusjs.fleet.web.api.safeApiCall
import com.indusjs.fleet.web.models.*
import io.ktor.client.request.*

/**
 * Dashboard API service
 */
object DashboardService {

    private val client = ApiClient.httpClient

    /**
     * Get unified dashboard
     */
    suspend fun getDashboard(): ApiResult<ApiResponse<Dashboard>> {
        return safeApiCall {
            client.get("/api/dashboard")
        }
    }

    /**
     * Get owner dashboard
     */
    suspend fun getOwnerDashboard(): ApiResult<ApiResponse<Dashboard>> {
        return safeApiCall {
            client.get("/api/dashboard/owner")
        }
    }

    /**
     * Get manager dashboard
     */
    suspend fun getManagerDashboard(): ApiResult<ApiResponse<Dashboard>> {
        return safeApiCall {
            client.get("/api/dashboard/manager")
        }
    }

    /**
     * Get supervisor dashboard
     */
    suspend fun getSupervisorDashboard(): ApiResult<ApiResponse<Dashboard>> {
        return safeApiCall {
            client.get("/api/dashboard/supervisor")
        }
    }

    /**
     * Get cost overview with filter (today, weekly, monthly)
     */
    suspend fun getCostOverview(filter: String = "today"): ApiResult<ApiResponse<CostOverview>> {
        return safeApiCall {
            client.get("/api/dashboard/cost-overview") {
                parameter("filter", filter)
            }
        }
    }

    /**
     * Get pending payments
     */
    suspend fun getPendingPayments(
        page: Int = 1,
        perPage: Int = 20
    ): ApiResult<PaginatedResponse<PendingPayment>> {
        return safeApiCall {
            client.get("/api/dashboard/pending-payments") {
                parameter("page", page)
                parameter("per_page", perPage)
            }
        }
    }

    /**
     * Get vehicle status summary
     */
    suspend fun getVehicleStatus(): ApiResult<ApiResponse<VehicleStatusSummary>> {
        return safeApiCall {
            client.get("/api/dashboard/vehicle-status")
        }
    }

    /**
     * Get trips status summary
     */
    suspend fun getTripsStatus(): ApiResult<ApiResponse<TripsStatusSummary>> {
        return safeApiCall {
            client.get("/api/dashboard/trips-status")
        }
    }

    /**
     * Get drivers status summary
     */
    suspend fun getDriversStatus(): ApiResult<ApiResponse<DriversStatusSummary>> {
        return safeApiCall {
            client.get("/api/dashboard/drivers-status")
        }
    }

    /**
     * Get alerts status summary
     */
    suspend fun getAlertsStatus(): ApiResult<ApiResponse<AlertsStatusSummary>> {
        return safeApiCall {
            client.get("/api/dashboard/alerts-status")
        }
    }
}

