package com.etelie.plugin

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cachingheaders.*
import io.ktor.server.plugins.defaultheaders.*

fun Application.pluginHTTP() {
//    this.install(HttpsRedirect) {
//            sslPort = 443
//            permanentRedirect = false
//        }
//    this.install(HSTS) {
//        includeSubDomains = true
//    }
    this.install(DefaultHeaders) {
        header("X-Engine", "Ktor") // will send this header with each response
    }
    this.install(CachingHeaders) {
        options { _, outgoingContent ->
            when (outgoingContent.contentType?.withoutParameters()) {
                ContentType.Text.CSS -> CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 24 * 60 * 60))
                else -> null
            }
        }
    }
}
