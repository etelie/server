package com.etelie.securities.price.imports.treasury

import io.github.oshai.kotlinlogging.KotlinLogging

private val log = KotlinLogging.logger {}

object AverageInterestRatesImport {

    fun import(): TreasuryClient.Response {
        return TreasuryClient.averageInterestRatesRequest()
    }

}
