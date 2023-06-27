package com.etelie.admin

import com.etelie.imports.treasury.treasuryImportRoutes
import io.ktor.server.routing.Route
import io.ktor.server.routing.route

fun Route.adminRoutes() {
    route("/admin") {
        route("/jobs") {
            treasuryImportRoutes()
        }
    }
}
