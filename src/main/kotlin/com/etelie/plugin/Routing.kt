package com.etelie.plugin

import com.etelie.control.controlRoutes
import io.ktor.server.application.Application
import io.ktor.server.routing.routing

fun Application.pluginRouting() {
    routing {
        controlRoutes()
    }
}
