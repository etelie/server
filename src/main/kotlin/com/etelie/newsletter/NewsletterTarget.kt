package com.etelie.newsletter

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class NewsletterTarget(
    val emailAddress: String,
    val created: Instant = Clock.System.now(),
    val updated: Instant = Clock.System.now(),
)
