package com.etelie.securities.price.imports.treasury

import com.etelie.network.addAllQueries
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.http4k.asString
import org.http4k.client.ApacheClient
import org.http4k.core.Method
import org.http4k.core.Request

private val log = KotlinLogging.logger {}

object AverageInterestRatesImport {
    val path = "${TreasuryAPI.basePath}/v2/accounting/od/avg_interest_rates"
    val request = Request(Method.GET, path)
        .addAllQueries(
            TreasuryAPI.baseParams + mapOf(
                "filter" to "record_date:eq:2023-05-31",
                "sort" to "avg_interest_rate_amt",
            ),
        )

    fun import(): TreasuryAPI.Response {
        val response = ApacheClient()(request)
        log.info { response }
        return Json.decodeFromString<TreasuryAPI.Response>(response.body.payload.asString())
    }

}
