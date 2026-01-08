package com.indusjs.fleet.web.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Team member model
 */
@Serializable
data class TeamMember(
    val id: Long = 0,
    val email: String = "",
    val mobile: String? = null,
    @SerialName("first_name") val firstName: String = "",
    @SerialName("last_name") val lastName: String = "",
    val role: String = "",
    @SerialName("owner_id") val ownerId: Long = 0,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
) {
    val fullName: String get() = "$firstName $lastName".trim()
}

/**
 * Create team member request
 */
@Serializable
data class CreateTeamMemberRequest(
    val email: String,
    val mobile: String,
    val password: String,
    @SerialName("first_name") val firstName: String,
    @SerialName("last_name") val lastName: String,
    val role: String // "manager" or "supervisor"
)

/**
 * Update team member request
 */
@Serializable
data class UpdateTeamMemberRequest(
    @SerialName("first_name") val firstName: String? = null,
    @SerialName("last_name") val lastName: String? = null,
    val mobile: String? = null
)

