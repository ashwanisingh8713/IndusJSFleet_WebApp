package com.indusjs.fleet.web.api

/**
 * API Configuration constants
 */
object ApiConfig {
    // Production base URL
    const val PRODUCTION_BASE_URL = "https://indusjs-fleet-docker-960880113496.asia-south2.run.app/api/v2"

    // Local development base URL
    const val LOCAL_BASE_URL = "http://localhost:8080/api/v2"

    // Current active base URL (switch for development/production)
    const val BASE_URL = PRODUCTION_BASE_URL

    // API version header
    const val API_VERSION_HEADER = "X-API-Version"
    const val API_VERSION = "v2"

    // Auth token key for localStorage
    const val TOKEN_STORAGE_KEY = "fleet_auth_token"
    const val USER_STORAGE_KEY = "fleet_user_data"

    // Request timeout in milliseconds
    const val REQUEST_TIMEOUT = 30_000L

    // Date formats used by API
    const val DATE_FORMAT = "DD-MM-YYYY"
    const val TIME_FORMAT = "HH:MM"
}

