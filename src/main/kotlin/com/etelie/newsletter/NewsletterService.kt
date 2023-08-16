package com.etelie.newsletter

import com.etelie.application.EtelieException
import com.etelie.application.logger
import org.jetbrains.exposed.exceptions.ExposedSQLException

private val log = logger { }

object NewsletterService {

    suspend fun createSubscription(emailAddress: String, ipAddress: String): NewsletterTarget {
        return NewsletterTarget(
            emailAddress = emailAddress,
            ipAddress = ipAddress,
        ).also {
            try {
                NewsletterTargetTable.insert(it)
                log.info { "Inserted newsletter target :: $it" }
            } catch (e: ExposedSQLException) {
                // Silence the exception if duplicate email address
                if (e.message?.contains("duplicate key") == true) {
                    return it
                }

                // todo: check key duplication before insertion - exposed logs error internally

                log.error(e) { "Failed to insert newsletter target :: $it" }
                throw EtelieException(e)
            }
        }
    }

}
