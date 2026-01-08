package com.indusjs.fleet.web.api.services

import com.indusjs.fleet.web.api.ApiClient
import com.indusjs.fleet.web.api.ApiResult
import com.indusjs.fleet.web.api.safeApiCall
import com.indusjs.fleet.web.models.*
import io.ktor.client.request.*

/**
 * Trip API service
 */
object TripService {

    private val client = ApiClient.httpClient

    /**
     * List trips with pagination and filters
     */
    suspend fun listTrips(
        page: Int = 1,
        perPage: Int = 10,
        state: String? = null
    ): ApiResult<PaginatedResponse<TripListItem>> {
        return safeApiCall {
            client.get("/api/trips") {
                parameter("page", page)
                parameter("per_page", perPage)
                state?.let { parameter("state", it) }
            }
        }
    }

    /**
     * Get single trip by ID
     */
    suspend fun getTrip(tripId: Long): ApiResult<ApiResponse<Trip>> {
        return safeApiCall {
            client.get("/api/trips/$tripId")
        }
    }

    /**
     * Create new trip
     */
    suspend fun createTrip(request: CreateTripRequest): ApiResult<ApiResponse<Trip>> {
        return safeApiCall {
            client.post("/api/trips") {
                setBody(request)
            }
        }
    }

    /**
     * Update trip
     */
    suspend fun updateTrip(tripId: Long, request: CreateTripRequest): ApiResult<ApiResponse<Trip>> {
        return safeApiCall {
            client.put("/api/trips/$tripId") {
                setBody(request)
            }
        }
    }

    /**
     * Update trip state
     */
    suspend fun updateTripState(tripId: Long, state: String): ApiResult<ApiResponse<Trip>> {
        return safeApiCall {
            client.patch("/api/trips/$tripId/state") {
                setBody(TripStateRequest(state))
            }
        }
    }

    /**
     * Update trip location
     */
    suspend fun updateTripLocation(tripId: Long, lat: Double, lng: Double): ApiResult<ApiResponse<Trip>> {
        return safeApiCall {
            client.patch("/api/trips/$tripId/location") {
                setBody(TripLocationRequest(lat, lng))
            }
        }
    }

    /**
     * Update trip progress
     */
    suspend fun updateTripProgress(tripId: Long, request: TripProgressRequest): ApiResult<ApiResponse<Trip>> {
        return safeApiCall {
            client.patch("/api/trips/$tripId/progress") {
                setBody(request)
            }
        }
    }

    /**
     * Delete trip
     */
    suspend fun deleteTrip(tripId: Long): ApiResult<ApiResponse<Unit>> {
        return safeApiCall {
            client.delete("/api/trips/$tripId")
        }
    }

    /**
     * Get trip stops
     */
    suspend fun getTripStops(tripId: Long): ApiResult<ApiResponse<List<TripStop>>> {
        return safeApiCall {
            client.get("/api/trips/$tripId/stops")
        }
    }

    /**
     * Add trip stop
     */
    suspend fun addTripStop(tripId: Long, request: CreateTripStopRequest): ApiResult<ApiResponse<TripStop>> {
        return safeApiCall {
            client.post("/api/trips/$tripId/stops") {
                setBody(request)
            }
        }
    }

    /**
     * Mark stop as completed
     */
    suspend fun markStopCompleted(tripId: Long, stopId: Long): ApiResult<ApiResponse<TripStop>> {
        return safeApiCall {
            client.patch("/api/trips/$tripId/stops/$stopId/complete")
        }
    }

    /**
     * Delete trip stop
     */
    suspend fun deleteTripStop(tripId: Long, stopId: Long): ApiResult<ApiResponse<Unit>> {
        return safeApiCall {
            client.delete("/api/trips/$tripId/stops/$stopId")
        }
    }

    /**
     * Get trip costs
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

    /**
     * Get trip profit/loss
     */
    suspend fun getTripProfitLoss(tripId: Long): ApiResult<ApiResponse<CostOverview>> {
        return safeApiCall {
            client.get("/api/trips/$tripId/profit-loss")
        }
    }
}

