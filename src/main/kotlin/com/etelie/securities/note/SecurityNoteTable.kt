package com.etelie.securities.note

import com.etelie.securities.detail.SecurityDetailTable
import org.jetbrains.exposed.dao.id.IntIdTable

object SecurityNoteTable : IntIdTable(
    name = "security_note",
    columnName = "note_id"
) {
    val securityId = reference("security_id", SecurityDetailTable.securityId)
    val category = varchar("category", 30)
    val label = varchar("label", 100)
    val content = varchar("content", 2000)
}
