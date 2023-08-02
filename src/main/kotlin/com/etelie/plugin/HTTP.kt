package com.etelie.plugin

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.CacheControl
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.CachingOptions
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.cachingheaders.CachingHeaders
import io.ktor.server.plugins.defaultheaders.DefaultHeaders
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond

private val log = KotlinLogging.logger {}

fun Application.pluginHTTP() {
    install(DefaultHeaders) {
        header(HttpHeaders.Server, "redacted")
    }

    install(CachingHeaders) {
        options { _, outgoingContent ->
            when (outgoingContent.contentType?.withoutParameters()) {
                ContentType.Text.CSS -> CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 24 * 60 * 60))
                else -> null
            }
        }
    }

    install(StatusPages) {
        exception<Throwable> { call, exception ->
            log.error(exception) { "${exception.javaClass.simpleName} (suppressed by Ktor StatusPages plugin)" }
            call.response.status(HttpStatusCode.InternalServerError)
            call.respond("${call.response.status()}: $exception")
        }
    }
}
