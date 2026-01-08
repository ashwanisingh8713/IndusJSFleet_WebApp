package com.indusjs.fleet.web.api.services

import com.indusjs.fleet.web.api.ApiClient
import com.indusjs.fleet.web.api.ApiResult
import com.indusjs.fleet.web.api.safeApiCall
import com.indusjs.fleet.web.models.*
import io.ktor.client.request.*

/**
 * Driver API service
 */
object DriverService {

    private val client = ApiClient.httpClient

    /**
     * List drivers with pagination and filters
     */
    suspend fun listDrivers(
        page: Int = 1,
        perPage: Int = 10,
        status: String? = null
    ): ApiResult<PaginatedResponse<Driver>> {
        return safeApiCall {
            client.get("/api/drivers") {
                parameter("page", page)
                parameter("per_page", perPage)
                status?.let { parameter("status", it) }
            }
        }
    }

    /**
     * Get available drivers
     */
    suspend fun getAvailableDrivers(): ApiResult<ApiResponse<List<Driver>>> {
        return safeApiCall {
            client.get("/api/drivers/available")
        }
    }

    /**
     * Get single driver by ID
     */
    suspend fun getDriver(driverId: Long): ApiResult<ApiResponse<Driver>> {
        return safeApiCall {
            client.get("/api/drivers/$driverId")
        }
    }

    /**
     * Create new driver
     */
    suspend fun createDriver(request: DriverRequest): ApiResult<ApiResponse<Driver>> {
        return safeApiCall {
            client.post("/api/drivers") {
                setBody(request)
            }
        }
    }

    /**
     * Update driver
     */
    suspend fun updateDriver(driverId: Long, request: DriverRequest): ApiResult<ApiResponse<Driver>> {
        return safeApiCall {
            client.put("/api/drivers/$driverId") {
                setBody(request)
            }
        }
    }

    /**
     * Update driver status
     */
    suspend fun updateDriverStatus(driverId: Long, status: String): ApiResult<ApiResponse<Driver>> {
        return safeApiCall {
            client.patch("/api/drivers/$driverId/status") {
                setBody(DriverStatusRequest(status))
            }
        }
    }

    /**
     * Toggle driver active status
     */
    suspend fun toggleDriverActive(driverId: Long): ApiResult<ApiResponse<Driver>> {
        return safeApiCall {
            client.patch("/api/drivers/$driverId/toggle-active")
        }
    }

    /**
     * Delete driver
     */
    suspend fun deleteDriver(driverId: Long): ApiResult<ApiResponse<Unit>> {
        return safeApiCall {
            client.delete("/api/drivers/$driverId")
        }
    }
}

