package com.indusjs.fleet.web.api.services

import com.indusjs.fleet.web.api.ApiClient
import com.indusjs.fleet.web.api.ApiResult
import com.indusjs.fleet.web.api.safeApiCall
import com.indusjs.fleet.web.models.*
import io.ktor.client.request.*

/**
 * Trip Cost API service
 */
object TripCostService {

    private val client = ApiClient.httpClient

    /**
     * Get all trip cost types
     */
    suspend fun getCostTypes(): ApiResult<ApiResponse<List<CostType>>> {
        return safeApiCall {
            client.get("/api/trip-costs/types")
        }
    }

    /**
     * List all trip costs with pagination and filters
     */
    suspend fun listTripCosts(
        page: Int = 1,
        perPage: Int = 20,
        tripId: Long? = null,
        vehicleId: Long? = null,
        costType: String? = null,
        startDate: String? = null,
        endDate: String? = null
    ): ApiResult<ApiResponse<TripCostsListResponse>> {
        return safeApiCall {
            client.get("/api/trip-costs") {
                parameter("page", page)
                parameter("per_page", perPage)
                tripId?.let { parameter("trip_id", it) }
                vehicleId?.let { parameter("vehicle_id", it) }
                costType?.let { parameter("cost_type", it) }
                startDate?.let { parameter("start_date", it) }
                endDate?.let { parameter("end_date", it) }
            }
        }
    }

    /**
     * Get single trip cost by ID
     */
    suspend fun getTripCost(costId: Long): ApiResult<ApiResponse<TripCost>> {
        return safeApiCall {
            client.get("/api/trip-costs/$costId")
        }
    }

    /**
     * Create new trip cost
     */
    suspend fun createTripCost(request: CreateTripCostRequest): ApiResult<ApiResponse<TripCost>> {
        return safeApiCall {
            client.post("/api/trip-costs") {
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
     * Get costs for a specific trip
     */
    suspend fun getTripCosts(tripId: Long): ApiResult<ApiResponse<List<TripCost>>> {
        return safeApiCall {
            client.get("/api/trips/$tripId/costs")
        }
    }

    /**
     * Get trip cost summary
     */
    suspend fun getTripCostSummary(tripId: Long): ApiResult<ApiResponse<TripCostSummary>> {
        return safeApiCall {
            client.get("/api/trips/$tripId/costs/summary")
        }
    }
}

