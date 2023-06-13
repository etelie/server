package com.etelie.plugin

import com.etelie.control.controlRoutes
import guru.zoroark.tegral.openapi.ktor.describe
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun Application.pluginRouting() {
    routing {
        get("/") {
            call.respond(HttpStatusCode.OK)
        } describe {
            summary = "root"
        }

        controlRoutes()
    }
}
