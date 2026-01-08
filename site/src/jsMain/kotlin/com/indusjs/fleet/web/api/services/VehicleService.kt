package com.indusjs.fleet.web.api.services

import com.indusjs.fleet.web.api.ApiClient
import com.indusjs.fleet.web.api.ApiResult
import com.indusjs.fleet.web.api.safeApiCall
import com.indusjs.fleet.web.models.*
import io.ktor.client.request.*

/**
 * Vehicle API service
 */
object VehicleService {

    private val client = ApiClient.httpClient

    /**
     * List vehicles with pagination and filters
     */
    suspend fun listVehicles(
        page: Int = 1,
        perPage: Int = 10,
        state: String? = null
    ): ApiResult<PaginatedResponse<Vehicle>> {
        return safeApiCall {
            client.get("/api/vehicles") {
                parameter("page", page)
                parameter("per_page", perPage)
                state?.let { parameter("state", it) }
            }
        }
    }

    /**
     * Get single vehicle by ID
     */
    suspend fun getVehicle(vehicleId: Long): ApiResult<ApiResponse<Vehicle>> {
        return safeApiCall {
            client.get("/api/vehicles/$vehicleId")
        }
    }

    /**
     * Get vehicle detail (full info)
     */
    suspend fun getVehicleDetail(vehicleId: Long): ApiResult<ApiResponse<VehicleDetail>> {
        return safeApiCall {
            client.get("/api/vehicles/$vehicleId/detail")
        }
    }

    /**
     * Get vehicle overview
     */
    suspend fun getVehicleOverview(vehicleId: Long): ApiResult<ApiResponse<VehicleOverview>> {
        return safeApiCall {
            client.get("/api/vehicles/$vehicleId/overview")
        }
    }

    /**
     * Register new vehicle
     */
    suspend fun createVehicle(request: VehicleRequest): ApiResult<ApiResponse<Vehicle>> {
        return safeApiCall {
            client.post("/api/vehicles") {
                setBody(request)
            }
        }
    }

    /**
     * Update vehicle
     */
    suspend fun updateVehicle(vehicleId: Long, request: VehicleRequest): ApiResult<ApiResponse<Vehicle>> {
        return safeApiCall {
            client.put("/api/vehicles/$vehicleId") {
                setBody(request)
            }
        }
    }

    /**
     * Update vehicle state
     */
    suspend fun updateVehicleState(vehicleId: Long, state: String): ApiResult<ApiResponse<Vehicle>> {
        return safeApiCall {
            client.patch("/api/vehicles/$vehicleId/state") {
                setBody(VehicleStateRequest(state))
            }
        }
    }

    /**
     * Delete vehicle
     */
    suspend fun deleteVehicle(vehicleId: Long): ApiResult<ApiResponse<Unit>> {
        return safeApiCall {
            client.delete("/api/vehicles/$vehicleId")
        }
    }

    /**
     * Get vehicle location
     */
    suspend fun getVehicleLocation(vehicleId: Long): ApiResult<ApiResponse<VehicleLocation>> {
        return safeApiCall {
            client.get("/api/vehicles/$vehicleId/location")
        }
    }

    /**
     * Get vehicle location history
     */
    suspend fun getVehicleLocationHistory(
        vehicleId: Long,
        startTime: String,
        endTime: String
    ): ApiResult<ApiResponse<List<VehicleLocation>>> {
        return safeApiCall {
            client.get("/api/vehicles/$vehicleId/location/history") {
                parameter("start_time", startTime)
                parameter("end_time", endTime)
            }
        }
    }

    /**
     * Get vehicle trips
     */
    suspend fun getVehicleTrips(
        vehicleId: Long,
        page: Int = 1,
        perPage: Int = 10
    ): ApiResult<PaginatedResponse<TripListItem>> {
        return safeApiCall {
            client.get("/api/vehicles/$vehicleId/trips") {
                parameter("page", page)
                parameter("per_page", perPage)
            }
        }
    }

    /**
     * Get vehicle documents
     */
    suspend fun getVehicleDocuments(vehicleId: Long): ApiResult<ApiResponse<List<VehicleDocument>>> {
        return safeApiCall {
            client.get("/api/vehicles/$vehicleId/documents")
        }
    }

    /**
     * Get vehicle documents detail
     */
    suspend fun getVehicleDocumentsDetail(vehicleId: Long): ApiResult<ApiResponse<List<VehicleDocument>>> {
        return safeApiCall {
            client.get("/api/vehicles/$vehicleId/documents/detail")
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
        endDate: String? = null,
        sortBy: String = "date",
        sortOrder: String = "desc"
    ): ApiResult<PaginatedResponse<MaintenanceCost>> {
        return safeApiCall {
            client.get("/api/vehicles/$vehicleId/maintenance-costs") {
                parameter("page", page)
                parameter("per_page", perPage)
                costType?.let { parameter("cost_type", it) }
                startDate?.let { parameter("start_date", it) }
                endDate?.let { parameter("end_date", it) }
                parameter("sort_by", sortBy)
                parameter("sort_order", sortOrder)
            }
        }
    }

    /**
     * Get vehicle trip costs
     */
    suspend fun getVehicleTripCosts(
        vehicleId: Long,
        page: Int = 1,
        perPage: Int = 20,
        costType: String? = null,
        startDate: String? = null,
        endDate: String? = null,
        sortBy: String = "date",
        sortOrder: String = "desc"
    ): ApiResult<PaginatedResponse<TripCost>> {
        return safeApiCall {
            client.get("/api/vehicles/$vehicleId/trip-costs") {
                parameter("page", page)
                parameter("per_page", perPage)
                costType?.let { parameter("cost_type", it) }
                startDate?.let { parameter("start_date", it) }
                endDate?.let { parameter("end_date", it) }
                parameter("sort_by", sortBy)
                parameter("sort_order", sortOrder)
            }
        }
    }
}

