package com.etelie.securities.note

import com.etelie.securities.detail.SecurityDetailEntity
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID

class SecurityNoteEntity(noteId: EntityID<Int>) : Entity<Int>(noteId) {
    companion object : EntityClass<Int, SecurityNoteEntity>(SecurityNoteTable)

    val noteId by SecurityNoteTable.id
    val securityId by SecurityNoteTable.securityId
    val category by SecurityNoteTable.category
    val label by SecurityNoteTable.label
    val content by SecurityNoteTable.content

    val securityDetail by SecurityDetailEntity referencedOn SecurityNoteTable.securityId
}
