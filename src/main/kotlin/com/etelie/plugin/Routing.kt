package com.etelie.plugin

import com.etelie.admin.adminRoutes
import com.etelie.control.controlRoutes
import guru.zoroark.tegral.openapi.ktor.describe
import guru.zoroark.tegral.openapi.ktor.openApiEndpoint
import io.ktor.server.application.Application
import io.ktor.server.routing.routing

fun Application.pluginRouting() {
    routing {
        controlRoutes()
        adminRoutes()

        openApiEndpoint("/openapi") describe {
            summary = "OpenAPI documentation"
        }
    }
}
