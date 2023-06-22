package com.etelie.securities.price.imports.treasury

import io.github.oshai.kotlinlogging.KotlinLogging

private val log = KotlinLogging.logger {}

object AverageInterestRatesImport {
    val path = "${TreasuryAPI.basePath}/v2/accounting/od/avg_interest_rates"

    fun import() {
        val response = khttp.get(
            url = path,
            params = TreasuryAPI.baseParams + mutableMapOf(
                "filter" to "record_date:eq:2023-05-31",
                "sort" to "avg_interest_rate_amt",
            ),
        )
        log.info { response }

        khttp.async.get(
            url = path,
            params = TreasuryAPI.baseParams + mutableMapOf(
                "filter" to "record_date:eq:2023-05-31",
                "sort" to "avg_interest_rate_amt",
            ),
            onError = {
                log.error(this) { "khttp error" }
            },
            onResponse = {
                log.info { this }
            }
        )
    }

}
