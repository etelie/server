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
        savingsBondRoute()
    }
}

fun Route.averageInterestRatesImportRoute() {
    patch("/average-interest-rates") {
        val importResponse = AverageInterestRatesImporter.import()
        call.respond<String>(HttpStatusCode.OK, importResponse)
    } describe {
        summary = "Average interest rates treasury import"
        200 response {}
    }
}

fun Route.auctionedRoute() {
    patch("/auctioned") {
        val importResponse = AuctionedImporter.import()
        call.respond<String>(HttpStatusCode.OK, importResponse)
    } describe {
        summary = "Auctioned securities treasury import"
        200 response {}
    }
}

fun Route.savingsBondRoute() {
    patch("/savings-bonds") {
        val importResponse = SavingsBondRatesImporter.import()
        call.respond<String>(HttpStatusCode.OK, importResponse)
    } describe {
        summary = "Savings bonds import"
        200 response {}
    }
}
