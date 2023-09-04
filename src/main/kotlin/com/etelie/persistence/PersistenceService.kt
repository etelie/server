package com.etelie.persistence

import com.etelie.application.ExecutionEnvironment
import com.etelie.persistence.database.DatabaseConfig
import com.etelie.persistence.database.DatabaseConfigFactory
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.server.application.ApplicationEnvironment
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.TransactionManager
import java.sql.Connection

private val log = KotlinLogging.logger {}

object PersistenceService {

    /**
     * The fully qualified dot-separated name of the class, or null if the class is local or a class of an anonymous object.
     * @see kotlin.reflect.KClass.qualifiedName
     */
    private val driverClassName: String = org.postgresql.Driver::class.qualifiedName!!

    fun connectToDatabase(environment: ApplicationEnvironment) = runBlocking {
        val config = DatabaseConfigFactory.fromExecutionEnvironment(
            executionEnvironment = ExecutionEnvironment.current,
        )
        check(config != null) { "Connecting to database within an unsupported environment [${ExecutionEnvironment.current.label}]" }

        log.info("Attempting to connect HikariCP to ${config.jdbcUrl}")
        val dataSource = createHikariDataSource(config)

        log.info("Attempting to connect Exposed to ${config.jdbcUrl}")
        val database = Database.connect(dataSource)
        TransactionManager.defaultDatabase = database

        log.info("Successfully connected to ${database.url}")
    }

    private fun createHikariDataSource(config: DatabaseConfig): HikariDataSource {
        return HikariDataSource(
            HikariConfig().also {
                it.driverClassName = driverClassName
                it.jdbcUrl = config.jdbcUrl
                it.maximumPoolSize = config.maxConnections
                it.isAutoCommit = false
                it.transactionIsolation = Connection::TRANSACTION_REPEATABLE_READ.name
                it.username = config.user
                it.password = config.password
                it.validate()
            },
        )
    }

}
