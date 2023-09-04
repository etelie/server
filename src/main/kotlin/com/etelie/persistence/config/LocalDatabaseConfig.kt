package com.etelie.persistence.config

import com.etelie.application.ExecutionEnvironment
import io.ktor.server.application.ApplicationEnvironment

class LocalDatabaseConfig(
    private val applicationEnvironment: ApplicationEnvironment,
) : DatabaseConfig(
    executionEnvironment = ExecutionEnvironment.DEVELOPMENT,
) {
    override val host: String = applicationEnvironment.config
        .property("etelie.postgresql.deploy.host")
        .getString()
    override val port: Int = applicationEnvironment.config
        .property("etelie.postgresql.deploy.port")
        .getString()
        .toInt()
    override val user: String = applicationEnvironment.config
        .property("etelie.postgresql.credential.user")
        .getString()
    override val password: String = applicationEnvironment.config
        .property("etelie.postgresql.credential.password")
        .getString()
    override val database: String = applicationEnvironment.config
        .property("etelie.postgresql.database")
        .getString()
    override val maxConnections: Int = applicationEnvironment.config
        .property("etelie.hikaricp.max_connections")
        .getString()
        .toInt()
}
