package com.etelie.control

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get

fun Routing.controlRoutes() {
    healthCheckRoute()
}

fun Route.healthCheckRoute() {
    get("/healthcheck") {
        call.response.status(HttpStatusCode.OK)
        call.respond("Service is healthy")
    }
}
