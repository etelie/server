package com.etelie.imports.treasury

import com.etelie.network.addAllQueries
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.http4k.client.ApacheClient
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request

object TreasuryDirectClient {
    private const val scheme: String = "https"
    private const val host: String = "treasurydirect.gov"
    private const val basePath: String = "/TA_WS/securities"
    private val client: HttpHandler = ApacheClient()
    private val baseParams: Map<String, String> = mapOf(
        "format" to "json",
        "pagesize" to "1000",
    )
    private val json = Json {
        ignoreUnknownKeys = true
    }

    fun auctionedSecurities(days: Int): List<TreasuryDirectSecurityResponse> {
        val path = "$basePath/auctioned"
        val request = Request(Method.GET, "$scheme://$host$path")
            .addAllQueries(baseParams)
            .addAllQueries(
                mapOf(
                    "days" to "$days",
                ),
            )

        val responseBody = client(request).bodyString()
        return json.decodeFromString<List<TreasuryDirectSecurityResponse>>(responseBody)
    }

}
