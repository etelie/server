package com.etelie.common

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Slf4jSqlDebugLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object Transactions {

    fun <T> blockingTransaction(block: () -> T): T {
        return transaction {
            addLogger(Slf4jSqlDebugLogger)
            return@transaction block()
        }
    }

    suspend fun <T> suspendedTransaction(block: suspend () -> T): T {
        return newSuspendedTransaction(Dispatchers.IO) {
            addLogger(Slf4jSqlDebugLogger)
            block()
        }
    }

}
