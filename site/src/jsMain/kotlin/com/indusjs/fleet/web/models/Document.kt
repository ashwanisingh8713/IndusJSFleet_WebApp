package com.indusjs.fleet.web.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Document status enum
 */
enum class DocumentStatus {
    PENDING,
    VERIFIED,
    REJECTED,
    EXPIRED
}

/**
 * Vehicle document
 */
@Serializable
data class VehicleDocument(
    val id: Long = 0,
    @SerialName("vehicle_id") val vehicleId: Long = 0,
    @SerialName("document_type") val documentType: String = "",
    @SerialName("document_type_label") val documentTypeLabel: String? = null,
    @SerialName("file_url") val fileUrl: String? = null,
    @SerialName("file_name") val fileName: String? = null,
    @SerialName("expiry_date") val expiryDate: String? = null,
    val status: String = "pending",
    val notes: String? = null,
    @SerialName("verified_by") val verifiedBy: Long? = null,
    @SerialName("verified_at") val verifiedAt: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
) {
    val documentStatus: DocumentStatus get() = when (status.lowercase()) {
        "pending" -> DocumentStatus.PENDING
        "verified" -> DocumentStatus.VERIFIED
        "rejected" -> DocumentStatus.REJECTED
        "expired" -> DocumentStatus.EXPIRED
        else -> DocumentStatus.PENDING
    }
}

/**
 * Document type info
 */
@Serializable
data class DocumentType(
    val type: String,
    val label: String,
    val description: String? = null
)

/**
 * Verify document request
 */
@Serializable
data class VerifyDocumentRequest(
    val status: String, // "verified" or "rejected"
    val notes: String? = null
)

/**
 * Update document request
 */
@Serializable
data class UpdateDocumentRequest(
    @SerialName("expiry_date") val expiryDate: String? = null,
    val notes: String? = null
)

