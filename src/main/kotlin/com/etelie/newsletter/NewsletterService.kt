package com.etelie.newsletter

import com.etelie.application.logger

private val log = logger { }

object NewsletterService {

    suspend fun createSubscription(emailAddress: String, ipAddress: String): NewsletterTarget {
        return NewsletterTarget(
            email = emailAddress,
            ipAddress = ipAddress,
        ).also {
            NewsletterTargetTable.insert(it)
            log.info { "Inserted newsletter target :: $it" }
        }
    }

}
