package com.indusjs.fleet.web.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.browser.localStorage
import kotlinx.serialization.json.Json

/**
 * Singleton HTTP client for all API calls
 */
object ApiClient {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
        prettyPrint = false
        coerceInputValues = true
    }

    val httpClient: HttpClient by lazy {
        HttpClient {
            install(ContentNegotiation) {
                json(json)
            }

            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        console.log("API: $message")
                    }
                }
                level = LogLevel.INFO
            }

            install(HttpTimeout) {
                requestTimeoutMillis = ApiConfig.REQUEST_TIMEOUT
                connectTimeoutMillis = ApiConfig.REQUEST_TIMEOUT
            }

            defaultRequest {
                // Use relative URLs for local proxy endpoints
                // The browser will use the current origin
                contentType(ContentType.Application.Json)
                header(ApiConfig.API_VERSION_HEADER, ApiConfig.API_VERSION)

                // Add auth token if available
                getToken()?.let { token ->
                    header(HttpHeaders.Authorization, "Bearer $token")
                }
            }
        }
    }

    /**
     * Get stored auth token
     */
    fun getToken(): String? {
        return try {
            localStorage.getItem(ApiConfig.TOKEN_STORAGE_KEY)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Save auth token to storage
     */
    fun saveToken(token: String) {
        try {
            localStorage.setItem(ApiConfig.TOKEN_STORAGE_KEY, token)
        } catch (e: Exception) {
            console.error("Failed to save token: ${e.message}")
        }
    }

    /**
     * Clear auth token (logout)
     */
    fun clearToken() {
        try {
            localStorage.removeItem(ApiConfig.TOKEN_STORAGE_KEY)
            localStorage.removeItem(ApiConfig.USER_STORAGE_KEY)
        } catch (e: Exception) {
            console.error("Failed to clear token: ${e.message}")
        }
    }

    /**
     * Check if user is authenticated
     */
    fun isAuthenticated(): Boolean {
        return getToken() != null
    }

    /**
     * Save user data to storage
     */
    fun saveUserData(userData: String) {
        try {
            localStorage.setItem(ApiConfig.USER_STORAGE_KEY, userData)
        } catch (e: Exception) {
            console.error("Failed to save user data: ${e.message}")
        }
    }

    /**
     * Get stored user data
     */
    fun getUserData(): String? {
        return try {
            localStorage.getItem(ApiConfig.USER_STORAGE_KEY)
        } catch (e: Exception) {
            null
        }
    }
}

/**
 * Sealed class for API response handling
 */
sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val message: String, val code: Int = 0) : ApiResult<Nothing>()
    data object Loading : ApiResult<Nothing>()
}

/**
 * Extension function to safely make API calls
 */
suspend inline fun <reified T> safeApiCall(crossinline call: suspend () -> HttpResponse): ApiResult<T> {
    return try {
        console.log("Making API call...")
        val response = call()
        console.log("API Response status: ${response.status.value}")

        if (response.status.isSuccess()) {
            try {
                val body = response.body<T>()
                console.log("API call successful, body parsed")
                ApiResult.Success(body)
            } catch (e: Exception) {
                console.log("Failed to parse response body: ${e.message}")
                ApiResult.Error(message = "Failed to parse response: ${e.message}")
            }
        } else {
            val errorMsg = "Request failed: ${response.status.value} - ${response.status.description}"
            console.log(errorMsg)
            ApiResult.Error(
                message = errorMsg,
                code = response.status.value
            )
        }
    } catch (e: Exception) {
        val errorMsg = e.message ?: "Unknown error occurred"
        console.log("API call exception: $errorMsg")
        ApiResult.Error(message = errorMsg)
    }
}

