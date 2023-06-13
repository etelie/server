package com.etelie.control

import com.etelie.control.status.Status
import com.etelie.control.status.StatusResponse
import guru.zoroark.tegral.openapi.dsl.schema
import guru.zoroark.tegral.openapi.ktor.describe
import guru.zoroark.tegral.openapi.ktor.openApiEndpoint
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
        openAPIRoute()
    }
}

fun Route.healthCheckRoute() {
    get("/healthcheck") {
        call.respond("Service is healthy")
    } describe {
        summary = "healthcheck"
        200 response {
            plainText {
                schema<String>("Service is healthy")
            }
        }
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

fun Route.openAPIRoute() {
    openApiEndpoint("/openapi") describe {
        summary = "OpenAPI documentation"
    }
}
