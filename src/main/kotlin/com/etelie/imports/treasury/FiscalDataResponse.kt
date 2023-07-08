package com.etelie.imports.treasury

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FiscalDataResponse(
    val data: List<Map<String, String>>,
    val meta: Meta,
    val links: Links,
) {
    @Serializable
    data class Meta(
        val count: Int,
        @SerialName("total-count") val totalCount: Int,
        @SerialName("total-pages") val totalPages: Int,
        val labels: Map<String, String>,
        val dataTypes: Map<String, String>,
        val dataFormats: Map<String, String>,
    )

    @Serializable
    data class Links(
        val self: String,
        val first: String,
        val prev: String?,
        val next: String?,
        val last: String,
    )
}
