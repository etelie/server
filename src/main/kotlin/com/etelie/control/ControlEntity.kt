package com.etelie.control

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID

class ControlEntity(id: EntityID<Int>) : Entity<Int>(id) {
    companion object : EntityClass<Int, ControlEntity>(ControlTable)

    var identifier by ControlTable.identifier
    var state by ControlTable.state
}
