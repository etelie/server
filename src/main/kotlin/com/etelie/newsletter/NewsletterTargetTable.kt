package com.etelie.newsletter

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

object NewsletterTargetTable : IdTable<String>(
    name = "newsletter_target",
) {

    override val id = text("email").entityId()
    private val emailAddress = id
    private val ipAddress = varchar("ip_address", length = 40)

    private val coroutineContext = Dispatchers.IO + CoroutineName(this::class.simpleName!!)

    suspend fun insert(target: NewsletterTarget) {
        newSuspendedTransaction(coroutineContext) {
            insert {
                it[emailAddress] = target.emailAddress
                it[ipAddress] = target.ipAddress
            }
        }
    }

    suspend fun get(emailAddress: String): NewsletterTarget? {
        return newSuspendedTransaction(coroutineContext) {
            select {
                NewsletterTargetTable.emailAddress eq emailAddress
            }
                .firstOrNull()
                ?.toNewsletterTarget()
        }
    }

    private fun ResultRow.toNewsletterTarget(): NewsletterTarget {
        return NewsletterTarget(
            emailAddress = get(emailAddress).value,
            ipAddress = get(ipAddress),
        )
    }

}
