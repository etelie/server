package com.etelie.newsletter

import com.etelie.application.EtelieException
import com.etelie.application.logger
import org.jetbrains.exposed.exceptions.ExposedSQLException

private val log = logger { }

object NewsletterService {

    suspend fun createSubscription(emailAddress: String, ipAddress: String): NewsletterTarget {
        return NewsletterTarget(
            email = emailAddress,
            ipAddress = ipAddress,
        ).also {
            try {
                NewsletterTargetTable.insert(it)
            } catch (e: ExposedSQLException) {
                log.error(e) { "Failed to insert newsletter target :: $it" }
                throw EtelieException(e)
            }
            log.info { "Inserted newsletter target :: $it" }
        }
    }

}
