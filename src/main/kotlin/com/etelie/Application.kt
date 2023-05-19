package com.etelie

import com.etelie.config.TLSConfig.sslConnector
import com.etelie.plugins.configureDatabases
import com.etelie.plugins.configureHTTP
import com.etelie.plugins.configureMonitoring
import com.etelie.plugins.configureRouting
import com.etelie.plugins.configureSerialization
import io.ktor.server.application.Application
import io.ktor.server.engine.applicationEngineEnvironment
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.slf4j.LoggerFactory

fun main() {
    val environment = applicationEngineEnvironment {
        log = LoggerFactory.getLogger(this::class.simpleName)
        sslConnector()
        module(Application::module)
    }

    embeddedServer(
        Netty,
        port = 8080,
        host = "0.0.0.0",
        module = Application::module,
    ).start(wait = true)
}

fun Application.module() {
    configureSerialization()
    configureDatabases()
    configureMonitoring()
    configureHTTP()
    configureRouting()
}
