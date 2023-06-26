package com.etelie.securities.price.imports.treasury

import com.etelie.network.addAllQueries
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.http4k.client.ApacheClient
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request
import java.net.URI

object TreasuryClient {

    private const val basePath: String = "https://api.fiscaldata.treasury.gov/services/api/fiscal_service"
    private val client: HttpHandler = ApacheClient()
    private val baseURI: URI = URI.create(basePath)
    private val baseParams: Map<String, String> = mapOf(
        "page[size]" to "100",
        "page[number]" to "1",
        "format" to "json",
    )


    fun averageInterestRateForTypeAndDate(
        securityType: AverageInterestRatesImport.SecurityType,
        currentDate: LocalDate,
    ): Response {
        val name: String = securityType.treasurySerialName
        val year: String = currentDate.year.let {
            String.format("%04d", it)
        }
        val month: String = currentDate.month.value.let {
            String.format("%02d", it)
        }

        val path = "${basePath}/v2/accounting/od/avg_interest_rates"
        val request = Request(Method.GET, path)
            .addAllQueries(baseParams)
            .addAllQueries(
                mapOf(
                    "filter" to "security_desc:eq:$name,record_calendar_year:eq:$year,record_calendar_month:eq:$month",
                    "sort" to "record_date",
                ),
            )
        val response = client(request)
        return Json.decodeFromString(response.bodyString())
    }

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
