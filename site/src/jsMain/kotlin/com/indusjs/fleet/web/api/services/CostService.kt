package com.indusjs.fleet.web.api.services

import com.indusjs.fleet.web.api.ApiClient
import com.indusjs.fleet.web.api.ApiResult
import com.indusjs.fleet.web.api.safeApiCall
import com.indusjs.fleet.web.models.*
import io.ktor.client.request.*

/**
 * Cost API service (Trip Costs & Maintenance Costs)
 */
object CostService {

    private val client = ApiClient.httpClient

    // ===== Trip Cost Types =====

    /**
     * Get trip cost types
     */
    suspend fun getTripCostTypes(): ApiResult<ApiResponse<List<CostType>>> {
        return safeApiCall {
            client.get("/api/trip-costs/types")
        }
    }

    // ===== Trip Costs =====

    /**
     * Create trip cost
     */
    suspend fun createTripCost(request: CreateTripCostRequest): ApiResult<ApiResponse<TripCost>> {
        return safeApiCall {
            client.post("/api/trip-costs") {
                setBody(request)
            }
        }
    }

    /**
     * Get trip cost by ID
     */
    suspend fun getTripCost(costId: Long): ApiResult<ApiResponse<TripCost>> {
        return safeApiCall {
            client.get("/api/trip-costs/$costId")
        }
    }

    /**
     * Update trip cost
     */
    suspend fun updateTripCost(costId: Long, request: CreateTripCostRequest): ApiResult<ApiResponse<TripCost>> {
        return safeApiCall {
            client.put("/api/trip-costs/$costId") {
                setBody(request)
            }
        }
    }

    /**
     * Delete trip cost
     */
    suspend fun deleteTripCost(costId: Long): ApiResult<ApiResponse<Unit>> {
        return safeApiCall {
            client.delete("/api/trip-costs/$costId")
        }
    }

    /**
     * Bulk create trip costs
     */
    suspend fun bulkCreateTripCosts(
        tripId: Long,
        costs: List<BulkTripCostItem>
    ): ApiResult<ApiResponse<List<TripCost>>> {
        return safeApiCall {
            client.post("/api/trips/$tripId/costs/bulk") {
                setBody(BulkTripCostRequest(costs))
            }
        }
    }

    // ===== Maintenance Costs =====

    /**
     * Get maintenance cost types
     */
    suspend fun getMaintenanceCostTypes(): ApiResult<ApiResponse<List<MaintenanceCostType>>> {
        return safeApiCall {
            client.get("/api/maintenance-costs/types")
        }
    }

    /**
     * List all maintenance costs
     */
    suspend fun listMaintenanceCosts(
        page: Int = 1,
        perPage: Int = 20,
        vehicleId: Long? = null,
        costType: String? = null,
        startDate: String? = null,
        endDate: String? = null
    ): ApiResult<PaginatedResponse<MaintenanceCost>> {
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
     * Create maintenance cost
     */
    suspend fun createMaintenanceCost(request: CreateMaintenanceCostRequest): ApiResult<ApiResponse<MaintenanceCost>> {
        return safeApiCall {
            client.post("/api/maintenance-costs") {
                setBody(request)
            }
        }
    }

    /**
     * Get maintenance cost by ID
     */
    suspend fun getMaintenanceCost(costId: Long): ApiResult<ApiResponse<MaintenanceCost>> {
        return safeApiCall {
            client.get("/api/maintenance-costs/$costId")
        }
    }

    /**
     * Update maintenance cost
     */
    suspend fun updateMaintenanceCost(costId: Long, request: CreateMaintenanceCostRequest): ApiResult<ApiResponse<MaintenanceCost>> {
        return safeApiCall {
            client.put("/api/maintenance-costs/$costId") {
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
     * Get vehicle maintenance summary
     */
    suspend fun getVehicleMaintenanceSummary(vehicleId: Long): ApiResult<ApiResponse<MaintenanceCostSummary>> {
        return safeApiCall {
            client.get("/api/vehicles/$vehicleId/maintenance-costs/summary")
        }
    }
}

