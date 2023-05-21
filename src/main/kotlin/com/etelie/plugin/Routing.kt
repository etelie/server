package com.etelie.plugin

import com.etelie.control.controlRoutes
import com.etelie.example.articlesRoutes
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun Application.pluginRouting() {
    routing {
        get("/") {
            call.respond("etelie api")
        }

        controlRoutes()
        articlesRoutes()
    }
}
