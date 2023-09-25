package com.etelie.newsletter

import io.mockk.CapturingSlot
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockkObject
import kotlinx.coroutines.runBlocking
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class NewsletterServiceTest {

    private var newsletterTarget: NewsletterTarget = NewsletterTargetFactory.build { }
    private val capturedNewsletterTarget = CapturingSlot<NewsletterTarget>()

    @BeforeTest
    fun setUp() {
        mockkObject(NewsletterTargetTable)
        coEvery { NewsletterTargetTable.getById(newsletterTarget.emailAddress) } returns null
        coEvery { NewsletterTargetTable.insert(capture(capturedNewsletterTarget)) } returns Unit
    }

    @Test
    fun createSubscription() {
        runBlocking {
            NewsletterService.createSubscription(newsletterTarget.emailAddress)

            assertEquals(newsletterTarget.emailAddress, capturedNewsletterTarget.captured.emailAddress)
            coVerify(exactly = 1) {
                NewsletterTargetTable.insert(capturedNewsletterTarget.captured)
            }
        }
    }

}
