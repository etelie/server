package com.etelie.config

import io.ktor.server.application.Application
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Slf4jSqlDebugLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.exposedLogger
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object Persistence {

    /**
     * The fully qualified dot-separated name of the class, or null if the class is local or a class of an anonymous object.
     * @see kotlin.reflect.KClass.qualifiedName
     */
    private val driverClassName: String = org.postgresql.Driver::class.qualifiedName!!

    fun Application.connectToDatabase() {
        val host = environment.config.property("postgresql.deploy.host").getString()
        val port = environment.config.property("postgresql.deploy.port").getString()
        val user = environment.config.property("postgresql.credential.user").getString()
        val password = environment.config.property("postgresql.credential.password").getString()
        val db = environment.config.property("postgresql.database").getString()

        val jdbcUrl = "jdbc:postgresql://$host:$port/$db"
        val database = Database.connect(jdbcUrl, driverClassName, user, password)
        TransactionManager.defaultDatabase = database

        exposedLogger.info("Successfully connected to ${database.url}")
    }

    fun <T> blockingTransaction(block: () -> T): T {
        return transaction {
            addLogger(Slf4jSqlDebugLogger)
            return@transaction block()
        }
    }

    suspend fun <T> suspendedTransaction(block: suspend () -> T): T {
        return newSuspendedTransaction(Dispatchers.IO) {
            addLogger(Slf4jSqlDebugLogger)
            return@newSuspendedTransaction block()
        }
    }

}
