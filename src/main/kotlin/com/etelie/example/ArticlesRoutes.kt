package com.etelie.example;

import io.ktor.resources.Resource
import io.ktor.server.application.call
import io.ktor.server.resources.get
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.Routing
import kotlinx.serialization.Serializable

fun Routing.articlesRoutes() {
    articlesRoute()
}

private fun Route.articlesRoute() {
    get<Articles> { article ->
        // Get all articles ...
        call.respond("List of articles sorted starting from ${article.sort}")
    }
}

@Serializable
@Resource("/articles")
data class Articles(val sort: String? = "new")
