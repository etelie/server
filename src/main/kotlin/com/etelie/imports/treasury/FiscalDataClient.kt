package com.etelie.imports.treasury

import com.etelie.network.addAllQueries
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.datetime.LocalDate
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.http4k.client.ApacheClient
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request
import java.math.BigDecimal

private val log = KotlinLogging.logger {}

object FiscalDataClient {

    private const val scheme: String = "https"
    private const val host: String = "api.fiscaldata.treasury.gov"
    private const val basePath: String = "/services/api/fiscal_service"
    private val client: HttpHandler = ApacheClient()
    private val baseParams: Map<String, String> = mapOf(
        "page[size]" to "100",
        "page[number]" to "1",
        "format" to "json",
    )
    private val json = Json.Default

    /**
     * Get the average interest rate for the given security from the US Treasury API
     * @return Map of the security's serial name to the average interest rate
     */
    fun averageInterestRateForDate(
        currentDate: LocalDate,
    ): Map<String, BigDecimal> {
        val path = "$basePath/v2/accounting/od/avg_interest_rates"
        val year: String = currentDate.year.let {
            String.format("%04d", it)
        }
        val month: String = currentDate.month.value.let {
            String.format("%02d", it)
        }
        val request = Request(Method.GET, "$scheme://$host$path")
            .addAllQueries(baseParams)
            .addAllQueries(
                mapOf(
                    "filter" to "record_calendar_year:eq:$year,record_calendar_month:eq:$month",
                    "sort" to "record_date",
                ),
            )


        val responseBody = client(request).bodyString()
        val response = json.decodeFromString<FiscalDataResponse>(responseBody)

        return response.data.fold(mapOf()) { acc, datum ->
            val securityName: String = datum.getOrElse("security_desc") {
                log.error(IllegalStateException()) { "Field [security_desc] not found in external API response" }
                return@fold acc
            }
            val rateString: String = datum.getOrElse("avg_interest_rate_amt") {
                log.error(IllegalStateException()) { "Field [avg_interest_rate_amt] not found in external API response" }
                return@fold acc
            }
            val rate: BigDecimal = try {
                rateString.toBigDecimal()
            } catch (exception: NumberFormatException) {
                log.error(exception) { "Error converting interest rate string to BigDecimal" }
                return@fold acc
            }
            acc.plus(securityName to rate)
        }
    }

}
