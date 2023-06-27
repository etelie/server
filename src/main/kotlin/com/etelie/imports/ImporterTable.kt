package com.etelie.imports

import org.jetbrains.exposed.dao.id.IntIdTable

object ImporterTable: IntIdTable(
    name = "importer",
    columnName = "importer_id"
) {
    val name = varchar("name", 100)
    val cronExpression = varchar("cron_expression", 100)
    val cronComment = varchar("cron_comment", 300)
}
