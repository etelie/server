package com.etelie

import com.etelie.config.TLSConfig.sslConnector
import com.etelie.plugin.pluginApi
import com.etelie.plugin.pluginDatabases
import com.etelie.plugin.pluginHTTP
import com.etelie.plugin.pluginMonitoring
import com.etelie.plugin.pluginRouting
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
    pluginDatabases()
    pluginMonitoring()
    pluginHTTP()
    pluginApi()
    pluginRouting()
}
