package com.etelie.newsletter

class NewsletterTargetFactory private constructor() {

    companion object {
        private val baseSecurityDetail = NewsletterTarget(
            emailAddress = "test@example.com",
            ipAddress = "192.168.0.1",
        )

        fun build(block: NewsletterTargetFactory.() -> Unit): NewsletterTarget {
            return NewsletterTargetFactory().apply(block).run {
                baseSecurityDetail.copy()
            }
        }
    }

}
