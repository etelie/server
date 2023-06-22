package com.etelie.securities.price.imports.treasury

import java.net.URI

object TreasuryAPI {
    const val basePath: String = "https://api.fiscaldata.treasury.gov/services/api/fiscal_service"
    val baseURI: URI = URI.create(basePath)
    val baseParams: Map<String, String> = mapOf(
        "page[size]" to "100",
        "page[number]" to "1",
        "format" to "json",
    )
}
