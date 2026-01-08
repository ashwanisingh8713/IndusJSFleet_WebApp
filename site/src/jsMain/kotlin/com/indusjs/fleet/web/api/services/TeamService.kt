package com.indusjs.fleet.web.api.services

import com.indusjs.fleet.web.api.ApiClient
import com.indusjs.fleet.web.api.ApiResult
import com.indusjs.fleet.web.api.safeApiCall
import com.indusjs.fleet.web.models.*
import io.ktor.client.request.*

/**
 * Team Management API service (Owner only)
 */
object TeamService {

    private val client = ApiClient.httpClient

    /**
     * List team members
     */
    suspend fun listTeamMembers(
        page: Int = 1,
        perPage: Int = 10
    ): ApiResult<PaginatedResponse<TeamMember>> {
        return safeApiCall {
            client.get("/api/team/members") {
                parameter("page", page)
                parameter("per_page", perPage)
            }
        }
    }

    /**
     * Get team member by ID
     */
    suspend fun getTeamMember(memberId: Long): ApiResult<ApiResponse<TeamMember>> {
        return safeApiCall {
            client.get("/api/team/members/$memberId")
        }
    }

    /**
     * Create team member
     */
    suspend fun createTeamMember(request: CreateTeamMemberRequest): ApiResult<ApiResponse<TeamMember>> {
        return safeApiCall {
            client.post("/api/team/members") {
                setBody(request)
            }
        }
    }

    /**
     * Update team member
     */
    suspend fun updateTeamMember(memberId: Long, request: UpdateTeamMemberRequest): ApiResult<ApiResponse<TeamMember>> {
        return safeApiCall {
            client.put("/api/team/members/$memberId") {
                setBody(request)
            }
        }
    }

    /**
     * Delete team member
     */
    suspend fun deleteTeamMember(memberId: Long): ApiResult<ApiResponse<Unit>> {
        return safeApiCall {
            client.delete("/api/team/members/$memberId")
        }
    }
}

