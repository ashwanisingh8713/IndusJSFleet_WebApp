package com.indusjs.fleet.web.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Generic API response wrapper
 */
@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val message: String? = null,
    val data: T? = null
)

/**
 * Paginated response wrapper
 */
@Serializable
data class PaginatedResponse<T>(
    val success: Boolean,
    val message: String? = null,
    val data: List<T> = emptyList(),
    val count: Int = 0,
    val page: Int = 1,
    @SerialName("per_page") val perPage: Int = 10,
    @SerialName("total_pages") val totalPages: Int = 0,
    @SerialName("has_more") val hasMore: Boolean = false,
    @SerialName("next_page") val nextPage: Int? = null
)

