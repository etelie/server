package com.etelie.securities.price.imports.treasury

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.net.URI

object TreasuryAPI {

    const val basePath: String = "https://api.fiscaldata.treasury.gov/services/api/fiscal_service"
    val baseURI: URI = URI.create(basePath)
    val baseParams: Map<String, String> = mapOf(
        "page[size]" to "100",
        "page[number]" to "1",
        "format" to "json",
    )

    @Serializable
    data class Response(
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

}
