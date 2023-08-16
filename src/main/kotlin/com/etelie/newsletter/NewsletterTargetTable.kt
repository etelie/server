package com.etelie.newsletter

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

object NewsletterTargetTable : IdTable<String>(
    name = "newsletter_target",
) {
    override val id = text("email").entityId()
    val email = id
    val ipAddress = varchar("ip_address", length = 40)

    private val coroutineContext = Dispatchers.IO + CoroutineName(this::class.simpleName!!)

    suspend fun insert(target: NewsletterTarget) {
        newSuspendedTransaction(coroutineContext) {
            NewsletterTargetTable.insert {
                it[email] = target.emailAddress
                it[ipAddress] = target.ipAddress
            }
        }
    }
}
