package com.etelie.newsletter

class NewsletterTargetFactory private constructor() {

    var created = baseSecurityDetail.created
    var updated = baseSecurityDetail.updated

    companion object {
        private val baseSecurityDetail = NewsletterTarget(
            emailAddress = "test@example.com",
        )

        fun build(block: NewsletterTargetFactory.() -> Unit): NewsletterTarget {
            return NewsletterTargetFactory().apply(block).run {
                baseSecurityDetail.copy()
            }
        }
    }

}
