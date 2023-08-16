package com.etelie.newsletter

import com.etelie.application.EtelieException
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
        val ipAddress: String = context.request.origin.remoteAddress

        if (email == null) {
            this.call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        try {
            NewsletterService.createSubscription(email, ipAddress)
        } catch (e: EtelieException) {
            call.respond(HttpStatusCode.BadRequest, "Failed to create subscription")
        }

        call.respond(HttpStatusCode.OK)
    } describe {
        summary = "Create a newsletter subscription"
        200 response {}
    }
}
