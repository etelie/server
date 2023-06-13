package com.etelie.plugin

import guru.zoroark.tegral.openapi.ktor.TegralOpenApiKtor
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.autohead.AutoHeadResponse
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.resources.Resources
import kotlinx.serialization.json.Json

fun Application.pluginApi() {
    install(ContentNegotiation) {
        json(Json { isLenient = true })
    }
    install(AutoHeadResponse)
    install(Resources)
    install(TegralOpenApiKtor) {
        title = "Etelie Server API"
        version = "0.1.0"
        contactEmail = "hello@etelie.com"
    }
}
