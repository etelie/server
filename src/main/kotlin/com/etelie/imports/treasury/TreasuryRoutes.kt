package com.etelie.imports.treasury

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
        auctionedRoute()
    }
}

fun Route.averageInterestRatesImportRoute() {
    patch("/average-interest-rates") {
        val importResponse = AverageInterestRatesImport.import()
        call.respond<String>(HttpStatusCode.OK, importResponse)
    } describe {
        summary = "Average interest rates treasury import"
        200 response {}
    }
}

fun Route.auctionedRoute() {
    patch("/auctioned") {
        val importResponse = AuctionedImport.import()
        call.respond<String>(HttpStatusCode.OK, importResponse)
    } describe {
        summary = "Auctioned securities treasury import"
        200 response {}
    }

    patch("/savings-bonds-value-files") {
        val importResponse = SavingsBondsValueFilesImport.import()
        call.respond<String>(HttpStatusCode.OK, importResponse)
    } describe {
        summary = "Savings bonds value files import"
        200 response {}
    }
}
