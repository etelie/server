package com.etelie.persistence

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

abstract class EtelieTable<ObjectType, IdType : Comparable<IdType>>(val name: String) : IdTable<IdType>(name) {

    abstract override val id: Column<EntityID<IdType>>

    protected val coroutineContext = Dispatchers.IO + CoroutineName(this::class.simpleName!!)

    val created = timestamp("created")
    val updated = timestamp("updated")

    protected abstract fun ResultRow.toObject(): ObjectType

    abstract suspend fun insert(target: ObjectType)

    suspend fun getById(id: IdType): ObjectType? {
        return newSuspendedTransaction(coroutineContext) {
            select {
                this@EtelieTable.id eq id
            }
                .firstOrNull()
                ?.toObject()
        }
    }

}
