package com.etelie.control

import com.etelie.config.Persistence.suspendedTransaction
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.exposedLogger
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

fun x() = runBlocking {
    suspendedTransaction {
        val insertion = ControlTable.insert {
            it[identifier] = "test"
            it[state] = 0
        }
        val query = ControlTable.select {
            ControlTable.identifier eq "test"
        }
        exposedLogger.info(
            insertion.resultedValues
                ?.map {
                    it.toString()
                }
                ?.reduce { acc: String, resultRow ->
                    return@reduce acc + resultRow
                },
        )
    }
}

object ControlTable : Table() {
    val id = integer("id").autoIncrement()
    val identifier = varchar("identifier", 50)
    val state = integer("state")

    override val primaryKey = PrimaryKey(id)
}
