package com.etelie.newsletter

import com.etelie.persistence.EtelieTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

object NewsletterTargetTable : EtelieTable<NewsletterTarget, String>(
    name = "newsletter_target",
) {

    override val id = text("email").entityId()
    private val emailAddress = id

    override suspend fun insert(target: NewsletterTarget) {
        newSuspendedTransaction(coroutineContext) {
            insert {
                it.set(emailAddress, target.emailAddress)
                it.set(created, target.created)
                it.set(updated, target.updated)
            }
        }
    }

    override fun ResultRow.toObject(): NewsletterTarget {
        return NewsletterTarget(
            emailAddress = get(emailAddress).value,
            created = get(created),
            updated = get(updated),
        )
    }

}
