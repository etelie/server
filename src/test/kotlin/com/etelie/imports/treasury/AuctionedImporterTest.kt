package com.etelie.imports.treasury

import com.etelie.imports.ImporterSecurityAssociationTable
import com.etelie.securities.SecurityType.TREASURY_MARKET_BILL
import com.etelie.securities.SecurityType.TREASURY_MARKET_BOND
import com.etelie.securities.SecurityType.TREASURY_MARKET_FRN
import com.etelie.securities.SecurityType.TREASURY_MARKET_NOTE
import com.etelie.securities.SecurityType.TREASURY_MARKET_TIPS
import com.etelie.securities.detail.SecurityDetailFactory
import com.etelie.securities.price.SecurityPriceTable
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockkObject
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import kotlin.test.BeforeTest

internal class AuctionedImporterTest {

    private val billResponse = TreasuryDirectSecurityResponseFactory.build {
        type = "Bill"
        floatingRate = "No"
        tips = "No"
    }
    private val bondResponse = TreasuryDirectSecurityResponseFactory.build {
        type = "Bond"
        floatingRate = "No"
        tips = "No"
    }
    private val noteResponse = TreasuryDirectSecurityResponseFactory.build {
        type = "Note"
        floatingRate = "No"
        tips = "No"
    }
    private val frnResponse = TreasuryDirectSecurityResponseFactory.build {
        type = "FRN"
        floatingRate = "Yes"
        tips = "No"
        spread = "0.5"
        frnIndexDeterminationRate = "1.0"
    }
    private val tipsResponse = TreasuryDirectSecurityResponseFactory.build {
        type = "TIPS"
        floatingRate = "No"
        tips = "Yes"
    }
    private val billDetail = SecurityDetailFactory.build {
        type = TREASURY_MARKET_BILL
    }
    private val bondDetail = SecurityDetailFactory.build {
        type = TREASURY_MARKET_BOND
    }
    private val noteDetail = SecurityDetailFactory.build {
        type = TREASURY_MARKET_NOTE
    }
    private val frnDetail = SecurityDetailFactory.build {
        type = TREASURY_MARKET_FRN
    }
    private val tipsDetail = SecurityDetailFactory.build {
        type = TREASURY_MARKET_TIPS
    }

    @BeforeTest
    fun before() {
        mockkObject(SecurityPriceTable)
        coEvery { SecurityPriceTable.insert(any(), any()) } returns 1

        mockkObject(TreasuryDirectClient)
        mockkObject(ImporterSecurityAssociationTable)
        coEvery { ImporterSecurityAssociationTable.fetchSecuritiesForImporter(AuctionedImporter.importerId) }
            .returns(
                listOf(
                    billDetail to "Bill",
                    bondDetail to "Bond",
                    noteDetail to "Note",
                    frnDetail to "FRN",
                    tipsDetail to "TIPS",
                )
            )
    }

    @TestFactory
    fun `import securities individually`() = listOf(
        billResponse to billDetail,
        bondResponse to bondDetail,
        noteResponse to noteDetail,
        frnResponse to frnDetail,
        tipsResponse to tipsDetail,
    ).map { (response, detail) ->
        DynamicTest.dynamicTest("import ${response.type}") {
            runBlocking {
                every { TreasuryDirectClient.auctionedSecurities(0) } returns listOf(response)

                AuctionedImporter.import()

                coVerify(exactly = 1) { SecurityPriceTable.insert(detail.type, any()) }
            }
        }
    }

    @Test
    fun `import securities bulk`() {
        runBlocking {
            every { TreasuryDirectClient.auctionedSecurities(0) }
                .returns(listOf(billResponse, bondResponse, noteResponse, frnResponse, tipsResponse))

            AuctionedImporter.import()

            coVerify(exactly = 1) { SecurityPriceTable.insert(billDetail.type, any()) }
            coVerify(exactly = 1) { SecurityPriceTable.insert(bondDetail.type, any()) }
            coVerify(exactly = 1) { SecurityPriceTable.insert(noteDetail.type, any()) }
            coVerify(exactly = 1) { SecurityPriceTable.insert(frnDetail.type, any()) }
            coVerify(exactly = 1) { SecurityPriceTable.insert(tipsDetail.type, any()) }
        }
    }

}
