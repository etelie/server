package com.etelie.plugins

import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.resources.*
import io.ktor.resources.*
import io.ktor.server.resources.Resources
import kotlinx.serialization.Serializable
import io.ktor.server.plugins.autohead.*
import io.ktor.server.application.*

fun Application.configureRouting() {
    install(Resources)
    install(AutoHeadResponse)
    install(Routing) {
        this@install.get("/") get@{
            this@get.call.respondText("Hello World!")
        }
        this@install.get<Articles> get@{ article ->
            // Get all articles ...
            this@get.call.respond("List of articles sorted starting from ${article.sort}")
        }
    }
}

@Serializable
@Resource("/articles")
class Articles(val sort: String? = "new")
