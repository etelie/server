package com.etelie.plugin

import com.etelie.application.ExecutionEnvironment
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.CacheControl
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.CachingOptions
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.cachingheaders.CachingHeaders
import io.ktor.server.plugins.cors.routing.CORS
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

    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Patch)
        allowMethod(HttpMethod.Delete)
        ExecutionEnvironment.current.getHosts().forEach {
            allowHost(it, listOf("http", "https"))
        }
    }
}

private fun ExecutionEnvironment.getHosts(): Collection<String> = if (isProduction()) {
    setOf("etelie.com")
} else if (isStaging()) {
    setOf("qa.etelie.com")
    // storybook.qa.etelie.com is intentionally excluded to forbid API requests via CORS
} else {
    setOf("localhost", "127.0.0.1", "192.168.0.1", "0.0.0.0", "::1").flatMap {
        setOf(it, "$it:3000")
    }
}
