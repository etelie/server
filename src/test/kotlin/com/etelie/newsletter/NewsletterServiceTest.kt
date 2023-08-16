package com.etelie.newsletter

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockkObject
import kotlinx.coroutines.runBlocking
import kotlin.test.BeforeTest
import kotlin.test.Test

class NewsletterServiceTest {

    private val emailAddress = "test@example.com"
    private val ipAddress = "192.168.0.1"
    private val expectedNewsletterTarget = NewsletterTarget(emailAddress, ipAddress)

    @BeforeTest
    fun setUp() {
        mockkObject(NewsletterTargetTable)
        coEvery { NewsletterTargetTable.insert(expectedNewsletterTarget) } returns Unit
    }

    @Test
    fun createSubscription() {
        runBlocking {
            NewsletterService.createSubscription(emailAddress, ipAddress)

            coVerify(exactly = 1) { NewsletterTargetTable.insert(expectedNewsletterTarget) }
        }
    }
}
