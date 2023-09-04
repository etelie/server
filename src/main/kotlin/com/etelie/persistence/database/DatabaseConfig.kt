package com.etelie.persistence.database

import com.etelie.application.ExecutionEnvironment

sealed class DatabaseConfig(
    open val executionEnvironment: ExecutionEnvironment,
) {
    abstract val host: String
    abstract val port: Int
    abstract val user: String
    abstract val password: String
    abstract val database: String
    abstract val maxConnections: Int
    val jdbcUrl: String
        get() = "jdbc:postgresql://$host:$port/$database"
}
