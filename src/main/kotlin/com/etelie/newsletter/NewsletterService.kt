package com.etelie.newsletter

import com.etelie.application.EtelieException
import com.etelie.application.logger
import org.jetbrains.exposed.exceptions.ExposedSQLException

private val log = logger { }

object NewsletterService {

    suspend fun createSubscription(emailAddress: String): NewsletterTarget {
        return NewsletterTarget(
            emailAddress = emailAddress,
        ).also {
            try {
                val existing: NewsletterTarget? = NewsletterTargetTable.getById(it.emailAddress)
                if (existing != null) {
                    return existing
                }

                NewsletterTargetTable.insert(it)
                log.info { "Inserted newsletter target :: $it" }
            } catch (e: ExposedSQLException) {
                log.error(e) { "Failed to insert newsletter target :: $it" }
                throw EtelieException(e)
            }
        }
    }

}
