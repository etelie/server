package com.etelie.securities.price.imports.treasury

import guru.zoroark.tegral.openapi.ktor.describe
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.patch
import io.ktor.server.routing.route

fun Route.treasuryImportRoutes() {
    route("/treasury-imports") {
        averageInterestRatesImportRoute()
    }
}

fun Route.averageInterestRatesImportRoute() {
    patch("/average-interest-rates") {
        AverageInterestRatesImport.import()
        call.respond<Unit>(HttpStatusCode.OK, Unit)
    } describe {
        summary = "Average Interest Rates Import"
        200 response {}
    }
}
