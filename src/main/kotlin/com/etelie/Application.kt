package com.etelie

import com.etelie.persistence.PersistenceConfig.connectToDatabase
import com.etelie.plugin.pluginApi
import com.etelie.plugin.pluginHTTP
import com.etelie.plugin.pluginMonitoring
import com.etelie.plugin.pluginRouting
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*


//val environment = applicationEngineEnvironment {
//    log = LoggerFactory.getLogger(this::class.simpleName)
//    sslConnector()
//    module(Application::module)
//}
//val port = environment.config.property("ktor.deployment.port").getString().toInt()
//val host = environment.config.property("ktor.deployment.host").getString()

fun main(args: Array<String>) {
    embeddedServer(
        factory = Netty,
        commandLineEnvironment(args),
    ).start(wait = true)
}

@Suppress("unused") // Referenced in application.yaml
fun Application.module() {
    connectToDatabase()
    installAllPlugins()
}

fun Application.installAllPlugins() {
    pluginMonitoring()
    pluginHTTP()
    pluginApi()
    pluginRouting()
}
