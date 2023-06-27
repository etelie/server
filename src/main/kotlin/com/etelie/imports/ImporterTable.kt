package com.etelie.imports

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

object ImporterTable : IntIdTable(
    name = "importer",
    columnName = "importer_id",
) {
    val name = varchar("name", 100)
    val cronExpression = varchar("cron_expression", 100)
    val cronComment = varchar("cron_comment", 300)

    suspend fun fetchCronExpression(importerId: Int): String = newSuspendedTransaction {
        select {
            this@ImporterTable.id eq importerId
        }
            .first()
            .get(cronExpression)
    }
}
