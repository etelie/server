package com.etelie.control

import org.jetbrains.exposed.dao.id.IntIdTable

object ControlTable : IntIdTable("control") {
    val identifier = varchar("identifier", 50)
    val state = integer("state")
}
