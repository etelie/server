package com.etelie.control

import com.etelie.control.status.Status
import com.etelie.control.status.StatusResponse
import guru.zoroark.tegral.openapi.dsl.schema
import guru.zoroark.tegral.openapi.ktor.describe
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route

fun Route.controlRoutes() {
    route("/control") {
        statusRoute()
    }
}

fun Route.statusRoute() {
    get("/status") {
        val status = Status.fetchCurrent() ?: Status.UNKNOWN
        call.respond<StatusResponse>(
            StatusResponse(
                ControlResponse(Control.STATUS.identifier, status.state),
                status.name,
            ),
        )
    } describe {
        summary = "Current status of the server"
        200 response {
            json {
                schema<StatusResponse>(
                    StatusResponse(
                        ControlResponse(Control.STATUS.identifier, Status.OPERATIONAL.state),
                        Status.OPERATIONAL.name,
                    ),
                )
            }
        }
    }
}
