package com.indusjs.fleet.web.api

import com.varabyte.kobweb.api.Api
import com.varabyte.kobweb.api.ApiContext
import com.varabyte.kobweb.api.http.HttpMethod
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

/**
 * Backend API proxy for all authenticated endpoints
 * Proxies requests from the frontend to the backend API to avoid CORS issues
 */
private val apiProxyClient = HttpClient(CIO) {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            isLenient = true
            encodeDefaults = true
        })
    }
}

private const val BACKEND_URL = "https://indusjs-fleet-docker-960880113496.asia-south2.run.app/api/v2"

/**
 * Helper to set JSON response
 */
private fun ApiContext.setJsonResponse(body: String, status: Int = 200) {
    res.status = status
    res.contentType = "application/json"
    res.body = body.encodeToByteArray()
}

/**
 * Get authorization token from request headers
 */
private fun ApiContext.getAuthToken(): String? {
    return req.headers["Authorization"]?.firstOrNull()
}

/**
 * Build query string from request
 */
private fun ApiContext.getQueryString(): String {
    val params = req.params
    return if (params.isNotEmpty()) {
        "?" + params.entries.joinToString("&") { (key, value) -> "$key=${value ?: ""}" }
    } else ""
}

// ==================== Dashboard APIs ====================

@Api("dashboard")
suspend fun proxyDashboard(ctx: ApiContext) {
    proxyGet(ctx, "/dashboard")
}

@Api("dashboard/owner")
suspend fun proxyDashboardOwner(ctx: ApiContext) {
    proxyGet(ctx, "/dashboard/owner")
}

@Api("dashboard/manager")
suspend fun proxyDashboardManager(ctx: ApiContext) {
    proxyGet(ctx, "/dashboard/manager")
}

@Api("dashboard/supervisor")
suspend fun proxyDashboardSupervisor(ctx: ApiContext) {
    proxyGet(ctx, "/dashboard/supervisor")
}

@Api("dashboard/cost-overview")
suspend fun proxyDashboardCostOverview(ctx: ApiContext) {
    proxyGet(ctx, "/dashboard/cost-overview${ctx.getQueryString()}")
}

@Api("dashboard/vehicle-status")
suspend fun proxyDashboardVehicleStatus(ctx: ApiContext) {
    proxyGet(ctx, "/dashboard/vehicle-status")
}

@Api("dashboard/trips-status")
suspend fun proxyDashboardTripsStatus(ctx: ApiContext) {
    proxyGet(ctx, "/dashboard/trips-status")
}

@Api("dashboard/drivers-status")
suspend fun proxyDashboardDriversStatus(ctx: ApiContext) {
    proxyGet(ctx, "/dashboard/drivers-status")
}

@Api("dashboard/alerts-status")
suspend fun proxyDashboardAlertsStatus(ctx: ApiContext) {
    proxyGet(ctx, "/dashboard/alerts-status")
}

@Api("dashboard/pending-payments")
suspend fun proxyDashboardPendingPayments(ctx: ApiContext) {
    proxyGet(ctx, "/dashboard/pending-payments${ctx.getQueryString()}")
}

// ==================== Vehicles APIs ====================

@Api("vehicles")
suspend fun proxyVehicles(ctx: ApiContext) {
    when (ctx.req.method) {
        HttpMethod.GET -> proxyGet(ctx, "/vehicles${ctx.getQueryString()}")
        HttpMethod.POST -> proxyPost(ctx, "/vehicles")
        else -> ctx.setJsonResponse("""{"success": false, "message": "Method not allowed"}""", 405)
    }
}

@Api("vehicles/{id}")
suspend fun proxyVehicle(ctx: ApiContext) {
    val id = ctx.req.params["id"]
    when (ctx.req.method) {
        HttpMethod.GET -> proxyGet(ctx, "/vehicles/$id")
        HttpMethod.PUT -> proxyPut(ctx, "/vehicles/$id")
        HttpMethod.DELETE -> proxyDelete(ctx, "/vehicles/$id")
        else -> ctx.setJsonResponse("""{"success": false, "message": "Method not allowed"}""", 405)
    }
}

@Api("vehicles/{id}/state")
suspend fun proxyVehicleState(ctx: ApiContext) {
    val id = ctx.req.params["id"]
    proxyPatch(ctx, "/vehicles/$id/state")
}

@Api("vehicles/{id}/overview")
suspend fun proxyVehicleOverview(ctx: ApiContext) {
    val id = ctx.req.params["id"]
    proxyGet(ctx, "/vehicles/$id/overview")
}

@Api("vehicles/{id}/detail")
suspend fun proxyVehicleDetail(ctx: ApiContext) {
    val id = ctx.req.params["id"]
    proxyGet(ctx, "/vehicles/$id/detail")
}

@Api("vehicles/{id}/trips")
suspend fun proxyVehicleTrips(ctx: ApiContext) {
    val id = ctx.req.params["id"]
    proxyGet(ctx, "/vehicles/$id/trips${ctx.getQueryString()}")
}

@Api("vehicles/{id}/documents")
suspend fun proxyVehicleDocuments(ctx: ApiContext) {
    val id = ctx.req.params["id"]
    when (ctx.req.method) {
        HttpMethod.GET -> proxyGet(ctx, "/vehicles/$id/documents")
        HttpMethod.POST -> proxyPost(ctx, "/vehicles/$id/documents")
        else -> ctx.setJsonResponse("""{"success": false, "message": "Method not allowed"}""", 405)
    }
}

@Api("vehicles/{id}/trip-costs")
suspend fun proxyVehicleTripCosts(ctx: ApiContext) {
    val id = ctx.req.params["id"]
    proxyGet(ctx, "/vehicles/$id/trip-costs${ctx.getQueryString()}")
}

@Api("vehicles/{id}/maintenance-costs")
suspend fun proxyVehicleMaintenanceCosts(ctx: ApiContext) {
    val id = ctx.req.params["id"]
    proxyGet(ctx, "/vehicles/$id/maintenance-costs${ctx.getQueryString()}")
}

// ==================== Drivers APIs ====================

@Api("drivers")
suspend fun proxyDrivers(ctx: ApiContext) {
    when (ctx.req.method) {
        HttpMethod.GET -> proxyGet(ctx, "/drivers${ctx.getQueryString()}")
        HttpMethod.POST -> proxyPost(ctx, "/drivers")
        else -> ctx.setJsonResponse("""{"success": false, "message": "Method not allowed"}""", 405)
    }
}

@Api("drivers/available")
suspend fun proxyDriversAvailable(ctx: ApiContext) {
    proxyGet(ctx, "/drivers/available")
}

@Api("drivers/{id}")
suspend fun proxyDriver(ctx: ApiContext) {
    val id = ctx.req.params["id"]
    when (ctx.req.method) {
        HttpMethod.GET -> proxyGet(ctx, "/drivers/$id")
        HttpMethod.PUT -> proxyPut(ctx, "/drivers/$id")
        HttpMethod.DELETE -> proxyDelete(ctx, "/drivers/$id")
        else -> ctx.setJsonResponse("""{"success": false, "message": "Method not allowed"}""", 405)
    }
}

@Api("drivers/{id}/status")
suspend fun proxyDriverStatus(ctx: ApiContext) {
    val id = ctx.req.params["id"]
    proxyPatch(ctx, "/drivers/$id/status")
}

@Api("drivers/{id}/toggle-active")
suspend fun proxyDriverToggleActive(ctx: ApiContext) {
    val id = ctx.req.params["id"]
    proxyPatch(ctx, "/drivers/$id/toggle-active")
}

// ==================== Trips APIs ====================

@Api("trips")
suspend fun proxyTrips(ctx: ApiContext) {
    when (ctx.req.method) {
        HttpMethod.GET -> proxyGet(ctx, "/trips${ctx.getQueryString()}")
        HttpMethod.POST -> proxyPost(ctx, "/trips")
        else -> ctx.setJsonResponse("""{"success": false, "message": "Method not allowed"}""", 405)
    }
}

@Api("trips/{id}")
suspend fun proxyTrip(ctx: ApiContext) {
    val id = ctx.req.params["id"]
    when (ctx.req.method) {
        HttpMethod.GET -> proxyGet(ctx, "/trips/$id")
        HttpMethod.PUT -> proxyPut(ctx, "/trips/$id")
        HttpMethod.DELETE -> proxyDelete(ctx, "/trips/$id")
        else -> ctx.setJsonResponse("""{"success": false, "message": "Method not allowed"}""", 405)
    }
}

@Api("trips/{id}/state")
suspend fun proxyTripState(ctx: ApiContext) {
    val id = ctx.req.params["id"]
    proxyPatch(ctx, "/trips/$id/state")
}

@Api("trips/{id}/progress")
suspend fun proxyTripProgress(ctx: ApiContext) {
    val id = ctx.req.params["id"]
    proxyPatch(ctx, "/trips/$id/progress")
}

@Api("trips/{id}/costs")
suspend fun proxyTripCosts(ctx: ApiContext) {
    val id = ctx.req.params["id"]
    when (ctx.req.method) {
        HttpMethod.GET -> proxyGet(ctx, "/trips/$id/costs")
        HttpMethod.POST -> proxyPost(ctx, "/trips/$id/costs")
        else -> ctx.setJsonResponse("""{"success": false, "message": "Method not allowed"}""", 405)
    }
}

@Api("trips/{id}/stops")
suspend fun proxyTripStops(ctx: ApiContext) {
    val id = ctx.req.params["id"]
    when (ctx.req.method) {
        HttpMethod.GET -> proxyGet(ctx, "/trips/$id/stops")
        HttpMethod.POST -> proxyPost(ctx, "/trips/$id/stops")
        else -> ctx.setJsonResponse("""{"success": false, "message": "Method not allowed"}""", 405)
    }
}

@Api("trips/{id}/profit-loss")
suspend fun proxyTripProfitLoss(ctx: ApiContext) {
    val id = ctx.req.params["id"]
    proxyGet(ctx, "/trips/$id/profit-loss")
}

// ==================== Trip Costs APIs ====================

@Api("trip-costs/types")
suspend fun proxyTripCostTypes(ctx: ApiContext) {
    proxyGet(ctx, "/trip-costs/types")
}

@Api("trip-costs/{id}")
suspend fun proxyTripCost(ctx: ApiContext) {
    val id = ctx.req.params["id"]
    when (ctx.req.method) {
        HttpMethod.GET -> proxyGet(ctx, "/trip-costs/$id")
        HttpMethod.PUT -> proxyPut(ctx, "/trip-costs/$id")
        HttpMethod.DELETE -> proxyDelete(ctx, "/trip-costs/$id")
        else -> ctx.setJsonResponse("""{"success": false, "message": "Method not allowed"}""", 405)
    }
}

// ==================== Maintenance Costs APIs ====================

@Api("maintenance-costs/types")
suspend fun proxyMaintenanceCostTypes(ctx: ApiContext) {
    proxyGet(ctx, "/maintenance-costs/types")
}

@Api("maintenance-costs")
suspend fun proxyMaintenanceCosts(ctx: ApiContext) {
    when (ctx.req.method) {
        HttpMethod.GET -> proxyGet(ctx, "/maintenance-costs${ctx.getQueryString()}")
        HttpMethod.POST -> proxyPost(ctx, "/maintenance-costs")
        else -> ctx.setJsonResponse("""{"success": false, "message": "Method not allowed"}""", 405)
    }
}

@Api("maintenance-costs/{id}")
suspend fun proxyMaintenanceCost(ctx: ApiContext) {
    val id = ctx.req.params["id"]
    when (ctx.req.method) {
        HttpMethod.GET -> proxyGet(ctx, "/maintenance-costs/$id")
        HttpMethod.PUT -> proxyPut(ctx, "/maintenance-costs/$id")
        HttpMethod.DELETE -> proxyDelete(ctx, "/maintenance-costs/$id")
        else -> ctx.setJsonResponse("""{"success": false, "message": "Method not allowed"}""", 405)
    }
}

// ==================== Team APIs ====================

@Api("team/members")
suspend fun proxyTeamMembers(ctx: ApiContext) {
    when (ctx.req.method) {
        HttpMethod.GET -> proxyGet(ctx, "/team/members${ctx.getQueryString()}")
        HttpMethod.POST -> proxyPost(ctx, "/team/members")
        else -> ctx.setJsonResponse("""{"success": false, "message": "Method not allowed"}""", 405)
    }
}

@Api("team/members/{id}")
suspend fun proxyTeamMember(ctx: ApiContext) {
    val id = ctx.req.params["id"]
    when (ctx.req.method) {
        HttpMethod.GET -> proxyGet(ctx, "/team/members/$id")
        HttpMethod.PUT -> proxyPut(ctx, "/team/members/$id")
        HttpMethod.DELETE -> proxyDelete(ctx, "/team/members/$id")
        else -> ctx.setJsonResponse("""{"success": false, "message": "Method not allowed"}""", 405)
    }
}

// ==================== Profile APIs ====================

@Api("profile")
suspend fun proxyProfile(ctx: ApiContext) {
    when (ctx.req.method) {
        HttpMethod.GET -> proxyGet(ctx, "/profile")
        HttpMethod.PUT -> proxyPut(ctx, "/profile")
        else -> ctx.setJsonResponse("""{"success": false, "message": "Method not allowed"}""", 405)
    }
}

@Api("profile/change-password")
suspend fun proxyProfileChangePassword(ctx: ApiContext) {
    proxyPost(ctx, "/profile/change-password")
}

// ==================== Reports APIs ====================

@Api("reports/types")
suspend fun proxyReportTypes(ctx: ApiContext) {
    proxyGet(ctx, "/reports/types")
}

@Api("reports/periods")
suspend fun proxyReportPeriods(ctx: ApiContext) {
    proxyGet(ctx, "/reports/periods")
}

@Api("reports/profit-loss")
suspend fun proxyReportsProfitLoss(ctx: ApiContext) {
    proxyGet(ctx, "/reports/profit-loss${ctx.getQueryString()}")
}

// ==================== Documents APIs ====================

@Api("documents/types")
suspend fun proxyDocumentTypes(ctx: ApiContext) {
    proxyGet(ctx, "/documents/types")
}

@Api("documents/expiring")
suspend fun proxyDocumentsExpiring(ctx: ApiContext) {
    proxyGet(ctx, "/documents/expiring${ctx.getQueryString()}")
}

@Api("documents/expired")
suspend fun proxyDocumentsExpired(ctx: ApiContext) {
    proxyGet(ctx, "/documents/expired")
}

@Api("documents/{id}")
suspend fun proxyDocument(ctx: ApiContext) {
    val id = ctx.req.params["id"]
    when (ctx.req.method) {
        HttpMethod.GET -> proxyGet(ctx, "/documents/$id")
        HttpMethod.PUT -> proxyPut(ctx, "/documents/$id")
        HttpMethod.DELETE -> proxyDelete(ctx, "/documents/$id")
        else -> ctx.setJsonResponse("""{"success": false, "message": "Method not allowed"}""", 405)
    }
}

// ==================== Helper Functions ====================

private suspend fun proxyGet(ctx: ApiContext, path: String) {
    try {
        val response = apiProxyClient.get("$BACKEND_URL$path") {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            header("X-API-Version", "v2")
            ctx.getAuthToken()?.let { header("Authorization", it) }
        }
        ctx.setJsonResponse(response.bodyAsText(), response.status.value)
    } catch (e: Exception) {
        ctx.setJsonResponse("""{"success": false, "message": "Proxy error: ${e.message}"}""", 500)
    }
}

private suspend fun proxyPost(ctx: ApiContext, path: String) {
    try {
        val body = ctx.req.body?.decodeToString() ?: ""
        val response = apiProxyClient.post("$BACKEND_URL$path") {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            header("X-API-Version", "v2")
            ctx.getAuthToken()?.let { header("Authorization", it) }
            setBody(body)
        }
        ctx.setJsonResponse(response.bodyAsText(), response.status.value)
    } catch (e: Exception) {
        ctx.setJsonResponse("""{"success": false, "message": "Proxy error: ${e.message}"}""", 500)
    }
}

private suspend fun proxyPut(ctx: ApiContext, path: String) {
    try {
        val body = ctx.req.body?.decodeToString() ?: ""
        val response = apiProxyClient.put("$BACKEND_URL$path") {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            header("X-API-Version", "v2")
            ctx.getAuthToken()?.let { header("Authorization", it) }
            setBody(body)
        }
        ctx.setJsonResponse(response.bodyAsText(), response.status.value)
    } catch (e: Exception) {
        ctx.setJsonResponse("""{"success": false, "message": "Proxy error: ${e.message}"}""", 500)
    }
}

private suspend fun proxyPatch(ctx: ApiContext, path: String) {
    try {
        val body = ctx.req.body?.decodeToString() ?: ""
        val response = apiProxyClient.patch("$BACKEND_URL$path") {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            header("X-API-Version", "v2")
            ctx.getAuthToken()?.let { header("Authorization", it) }
            setBody(body)
        }
        ctx.setJsonResponse(response.bodyAsText(), response.status.value)
    } catch (e: Exception) {
        ctx.setJsonResponse("""{"success": false, "message": "Proxy error: ${e.message}"}""", 500)
    }
}

private suspend fun proxyDelete(ctx: ApiContext, path: String) {
    try {
        val response = apiProxyClient.delete("$BACKEND_URL$path") {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            header("X-API-Version", "v2")
            ctx.getAuthToken()?.let { header("Authorization", it) }
        }
        ctx.setJsonResponse(response.bodyAsText(), response.status.value)
    } catch (e: Exception) {
        ctx.setJsonResponse("""{"success": false, "message": "Proxy error: ${e.message}"}""", 500)
    }
}

