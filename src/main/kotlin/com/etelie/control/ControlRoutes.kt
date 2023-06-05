package com.etelie.control

import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.route

fun Routing.controlRoutes() {
    route("/control") {
        healthCheckRoute()
        statusRoute()
    }
}

fun Route.healthCheckRoute() {
    get("/healthcheck") {
        call.respond("Service is healthy")
    }
}

fun Route.statusRoute() {
    get("/status") {
        val status = Status.fetchCurrent() ?: Status.UNKNOWN
        call.respond<StatusResponse>(
            StatusResponse(
                ControlResponse(Control.STATUS.identifier, status.state),
                status.name
            )
        )
    }
}
