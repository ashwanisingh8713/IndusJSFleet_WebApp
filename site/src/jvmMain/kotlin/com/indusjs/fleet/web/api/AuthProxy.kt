package com.indusjs.fleet.web.api

import com.varabyte.kobweb.api.Api
import com.varabyte.kobweb.api.ApiContext
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

/**
 * Backend proxy for API calls to avoid CORS issues
 */
private val proxyClient = HttpClient(CIO) {
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
 * Helper function to set JSON response
 */
private fun ApiContext.setJsonResponse(body: String, status: Int = 200) {
    res.status = status
    res.contentType = "application/json"
    res.body = body.encodeToByteArray()
}

@Api("auth/login")
suspend fun proxyAuthLogin(ctx: ApiContext) {
    try {
        val body = ctx.req.body?.decodeToString() ?: ""

        val response = proxyClient.post("$BACKEND_URL/auth/login") {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            header("X-API-Version", "v2")
            setBody(body)
        }

        val responseBody = response.bodyAsText()
        ctx.setJsonResponse(responseBody, response.status.value)
    } catch (e: Exception) {
        ctx.setJsonResponse("""{"success": false, "message": "Proxy error: ${e.message}"}""", 500)
    }
}

@Api("auth/signup")
suspend fun proxyAuthSignup(ctx: ApiContext) {
    try {
        val body = ctx.req.body?.decodeToString() ?: ""

        val response = proxyClient.post("$BACKEND_URL/auth/signup") {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            header("X-API-Version", "v2")
            setBody(body)
        }

        val responseBody = response.bodyAsText()
        ctx.setJsonResponse(responseBody, response.status.value)
    } catch (e: Exception) {
        ctx.setJsonResponse("""{"success": false, "message": "Proxy error: ${e.message}"}""", 500)
    }
}

@Api("auth/forgot-password")
suspend fun proxyAuthForgotPassword(ctx: ApiContext) {
    try {
        val body = ctx.req.body?.decodeToString() ?: ""

        val response = proxyClient.post("$BACKEND_URL/auth/forgot-password") {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            header("X-API-Version", "v2")
            setBody(body)
        }

        val responseBody = response.bodyAsText()
        ctx.setJsonResponse(responseBody, response.status.value)
    } catch (e: Exception) {
        ctx.setJsonResponse("""{"success": false, "message": "Proxy error: ${e.message}"}""", 500)
    }
}

