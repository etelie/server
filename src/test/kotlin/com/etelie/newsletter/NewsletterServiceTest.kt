package com.etelie.newsletter

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockkObject
import kotlinx.coroutines.runBlocking
import kotlin.test.BeforeTest
import kotlin.test.Test

class NewsletterServiceTest {

    private val newsletterTarget = NewsletterTargetFactory.build { }

    @BeforeTest
    fun setUp() {
        mockkObject(NewsletterTargetTable)
        coEvery { NewsletterTargetTable.get(newsletterTarget.emailAddress) } returns null
        coEvery { NewsletterTargetTable.insert(newsletterTarget) } returns Unit
    }

    @Test
    fun createSubscription() {
        runBlocking {
            NewsletterService.createSubscription(newsletterTarget.emailAddress, newsletterTarget.ipAddress)

            coVerify(exactly = 1) { NewsletterTargetTable.insert(newsletterTarget) }
        }
    }
}
