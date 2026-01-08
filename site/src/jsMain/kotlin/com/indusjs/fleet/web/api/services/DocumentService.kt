package com.indusjs.fleet.web.api.services

import com.indusjs.fleet.web.api.ApiClient
import com.indusjs.fleet.web.api.ApiResult
import com.indusjs.fleet.web.api.safeApiCall
import com.indusjs.fleet.web.models.*
import io.ktor.client.request.*

/**
 * Document API service
 */
object DocumentService {

    private val client = ApiClient.httpClient

    /**
     * Get document types
     */
    suspend fun getDocumentTypes(): ApiResult<ApiResponse<List<DocumentType>>> {
        return safeApiCall {
            client.get("/api/documents/types")
        }
    }

    /**
     * Get document by ID
     */
    suspend fun getDocument(documentId: Long): ApiResult<ApiResponse<VehicleDocument>> {
        return safeApiCall {
            client.get("/api/documents/$documentId")
        }
    }

    /**
     * Update document
     */
    suspend fun updateDocument(documentId: Long, request: UpdateDocumentRequest): ApiResult<ApiResponse<VehicleDocument>> {
        return safeApiCall {
            client.put("/api/documents/$documentId") {
                setBody(request)
            }
        }
    }

    /**
     * Verify document
     */
    suspend fun verifyDocument(documentId: Long, status: String, notes: String? = null): ApiResult<ApiResponse<VehicleDocument>> {
        return safeApiCall {
            client.post("/api/documents/$documentId/verify") {
                setBody(VerifyDocumentRequest(status, notes))
            }
        }
    }

    /**
     * Delete document
     */
    suspend fun deleteDocument(documentId: Long): ApiResult<ApiResponse<Unit>> {
        return safeApiCall {
            client.delete("/api/documents/$documentId")
        }
    }

    /**
     * Get expiring documents
     */
    suspend fun getExpiringDocuments(days: Int = 30): ApiResult<ApiResponse<List<VehicleDocument>>> {
        return safeApiCall {
            client.get("/api/documents/expiring") {
                parameter("days", days)
            }
        }
    }

    /**
     * Get expired documents
     */
    suspend fun getExpiredDocuments(): ApiResult<ApiResponse<List<VehicleDocument>>> {
        return safeApiCall {
            client.get("/api/documents/expired")
        }
    }

    /**
     * Get documents by status
     */
    suspend fun getDocumentsByStatus(status: String): ApiResult<ApiResponse<List<VehicleDocument>>> {
        return safeApiCall {
            client.get("/api/documents/status/$status")
        }
    }
}

