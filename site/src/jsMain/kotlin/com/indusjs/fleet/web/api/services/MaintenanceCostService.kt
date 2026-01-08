package com.indusjs.fleet.web.api.services

import com.indusjs.fleet.web.api.ApiClient
import com.indusjs.fleet.web.api.ApiResult
import com.indusjs.fleet.web.api.safeApiCall
import com.indusjs.fleet.web.models.*
import io.ktor.client.request.*

/**
 * Maintenance Cost API service
 */
object MaintenanceCostService {

    private val client = ApiClient.httpClient

    /**
     * Get all maintenance cost types
     */
    suspend fun getCostTypes(): ApiResult<ApiResponse<List<MaintenanceCostType>>> {
        return safeApiCall {
            client.get("/api/maintenance-costs/types")
        }
    }

    /**
     * List all maintenance costs with pagination and filters
     */
    suspend fun listMaintenanceCosts(
        page: Int = 1,
        perPage: Int = 20,
        vehicleId: Long? = null,
        costType: String? = null,
        startDate: String? = null,
        endDate: String? = null
    ): ApiResult<ApiResponse<MaintenanceCostsListResponse>> {
        return safeApiCall {
            client.get("/api/maintenance-costs") {
                parameter("page", page)
                parameter("per_page", perPage)
                vehicleId?.let { parameter("vehicle_id", it) }
                costType?.let { parameter("cost_type", it) }
                startDate?.let { parameter("start_date", it) }
                endDate?.let { parameter("end_date", it) }
            }
        }
    }

    /**
     * Get single maintenance cost by ID
     */
    suspend fun getMaintenanceCost(costId: Long): ApiResult<ApiResponse<MaintenanceCost>> {
        return safeApiCall {
            client.get("/api/maintenance-costs/$costId")
        }
    }

    /**
     * Create new maintenance cost
     */
    suspend fun createMaintenanceCost(request: CreateMaintenanceCostRequest): ApiResult<ApiResponse<MaintenanceCost>> {
        return safeApiCall {
            client.post("/api/maintenance-costs") {
                setBody(request)
            }
        }
    }

    /**
     * Delete maintenance cost
     */
    suspend fun deleteMaintenanceCost(costId: Long): ApiResult<ApiResponse<Unit>> {
        return safeApiCall {
            client.delete("/api/maintenance-costs/$costId")
        }
    }

    /**
     * Get vehicle maintenance costs
     */
    suspend fun getVehicleMaintenanceCosts(
        vehicleId: Long,
        page: Int = 1,
        perPage: Int = 20,
        costType: String? = null,
        startDate: String? = null,
        endDate: String? = null
    ): ApiResult<ApiResponse<MaintenanceCostsListResponse>> {
        return safeApiCall {
            client.get("/api/vehicles/$vehicleId/maintenance-costs") {
                parameter("page", page)
                parameter("per_page", perPage)
                costType?.let { parameter("cost_type", it) }
                startDate?.let { parameter("start_date", it) }
                endDate?.let { parameter("end_date", it) }
            }
        }
    }

    /**
     * Get vehicle maintenance summary
     */
    suspend fun getVehicleMaintenanceSummary(vehicleId: Long): ApiResult<ApiResponse<MaintenanceCostSummary>> {
        return safeApiCall {
            client.get("/api/vehicles/$vehicleId/maintenance-costs/summary")
        }
    }
}

