package com.etelie.newsletter

import com.etelie.application.logger
import guru.zoroark.tegral.openapi.ktor.describe
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.plugins.origin
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route

private val log = logger { }

fun Route.newsletterRoutes() {
    route("/newsletter") {
        subscriptionRoute()
    }
}

fun Route.subscriptionRoute() {
    post("/subscription/{email}") {
        val email: String? = context.parameters.get("email")
        val ipAddress: String = context.request.origin.localAddress
        // todo: how to get source ip address from ip protocol? prefer not to add as query parameter
        // todo: check that localAddress is correct

        if (email == null) {
            this.call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        NewsletterService.createSubscription(email, ipAddress)

        call.respond(HttpStatusCode.OK)
    } describe {
        summary = "Create a newsletter subscription"
        200 response {}
    }
}
