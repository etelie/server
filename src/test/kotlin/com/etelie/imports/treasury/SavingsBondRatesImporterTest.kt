package com.etelie.imports.treasury

import com.etelie.network.WebContentNotFoundException
import com.etelie.securities.SecurityTerm
import com.etelie.securities.SecurityType
import com.etelie.securities.price.SecurityPrice
import com.etelie.securities.price.SecurityPriceTable
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockkObject
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import java.math.BigDecimal
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

internal class SavingsBondRatesImporterTest {

    private val mockEEFixedRate = BigDecimal("1.1")
    private val mockIFixedRate = BigDecimal("2.2")
    private val mockIVariableRate = BigDecimal("3.3")
    private val mockEEIssueDate = Instant.fromEpochMilliseconds(123456789L)
        .toLocalDateTime(TimeZone.UTC).date
    private val mockIIssueDate = Instant.fromEpochMilliseconds(333333333L)
        .toLocalDateTime(TimeZone.UTC).date
    private val savingsBondTerm = SecurityTerm.months(30 * 12)

    @BeforeTest
    fun before() {
        mockkObject(TreasuryDirectScraper)
        every { TreasuryDirectScraper.scrapeSavingsBondRates() } returns
            SavingsBondScrapeResultFactory.build {
                eeBondIssueDate = mockEEIssueDate
                eeBondFixedRate = mockEEFixedRate
                iBondIssueDate = mockIIssueDate
                iBondFixedRate = mockIFixedRate
                iBondVariableRate = mockIVariableRate
            }

        mockkObject(SecurityPriceTable)
        coEvery { SecurityPriceTable.insert(any(), any()) } returns 1

    }

    @Test
    fun `content not found exception handled`(): Unit = runBlocking {
        every { TreasuryDirectScraper.scrapeSavingsBondRates() } throws WebContentNotFoundException()

        val message: String = SavingsBondRatesImporter.import()

        assertEquals("SavingsBondRatesImport failed; scrape failure", message)
        coVerify(exactly = 0) { SecurityPriceTable.insert(any(), any()) }
    }

    @Test
    fun `import both savings bond types`(): Unit = runBlocking {
        val message: String = SavingsBondRatesImporter.import()
        val expectedEEPrice = SecurityPrice(
            purchasedTimestamp = null,
            issuedTimestamp = mockEEIssueDate.atStartOfDayIn(TimeZone.UTC),
            term = savingsBondTerm,
            interestRateFixed = mockEEFixedRate,
            interestRateVariable = BigDecimal.ZERO,
        )
        val expectedIPrice = SecurityPrice(
            purchasedTimestamp = null,
            issuedTimestamp = mockIIssueDate.atStartOfDayIn(TimeZone.UTC),
            term = savingsBondTerm,
            interestRateFixed = mockIFixedRate,
            interestRateVariable = mockIVariableRate,
        )

        assertContains(message, "2 prices inserted")
        coVerify(exactly = 1) { SecurityPriceTable.insert(SecurityType.TREASURY_SAVINGS_EE, expectedEEPrice) }
        coVerify(exactly = 1) { SecurityPriceTable.insert(SecurityType.TREASURY_SAVINGS_I, expectedIPrice) }
    }

}
