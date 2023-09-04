package com.etelie.application

import com.etelie.persistence.PersistenceService
import com.etelie.plugin.pluginApi
import com.etelie.plugin.pluginHTTP
import com.etelie.plugin.pluginMonitoring
import com.etelie.plugin.pluginRouting
import com.etelie.schedule.Scheduler
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.server.application.Application
import io.ktor.server.application.log
import io.ktor.server.engine.commandLineEnvironment
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

fun main(args: Array<String>) {
    embeddedServer(
        factory = Netty,
        commandLineEnvironment(args),
    ).start(wait = true)
}

@Suppress("unused") // Referenced in application.yaml
fun Application.module() {
    ExecutionEnvironment.initialize(environment)
    val buildTag: String = environment.config.property("etelie.build.tag").getString()
    log.info("Starting application with tag [$buildTag] in environment [${ExecutionEnvironment.current.label}]")

    installAllPlugins()

    if (ExecutionEnvironment.current.isServer()) {
        PersistenceService.connectToDatabase(environment)
    }

    if (ExecutionEnvironment.current.isDeployable()) {
        Scheduler.start(environment)
    }
}

fun Application.installAllPlugins() {
    pluginMonitoring()
    pluginHTTP()
    pluginApi()
    pluginRouting()
}

fun logger(lambda: () -> Unit) = KotlinLogging.logger(lambda)
