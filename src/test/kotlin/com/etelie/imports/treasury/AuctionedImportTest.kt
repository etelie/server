package com.etelie.imports.treasury

import com.etelie.imports.ImporterSecurityAssociationTable
import com.etelie.securities.SecurityType.*
import com.etelie.securities.detail.SecurityDetailGenerator
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

internal class AuctionedImportTest {

    private val billResponse = TreasuryDirectSecurityResponseGenerator().build {
        type = "Bill"
        floatingRate = "No"
        tips = "No"
    }
    private val bondResponse = TreasuryDirectSecurityResponseGenerator().build {
        type = "Bond"
        floatingRate = "No"
        tips = "No"
    }
    private val noteResponse = TreasuryDirectSecurityResponseGenerator().build {
        type = "Note"
        floatingRate = "No"
        tips = "No"
    }
    private val frnResponse = TreasuryDirectSecurityResponseGenerator().build {
        type = "FRN"
        floatingRate = "Yes"
        tips = "No"
        spread = "0.5"
        frnIndexDeterminationRate = "1.0"
    }
    private val tipsResponse = TreasuryDirectSecurityResponseGenerator().build {
        type = "TIPS"
        floatingRate = "No"
        tips = "Yes"
    }
    private val billDetail = SecurityDetailGenerator().build {
        type = TREASURY_MARKET_BILL
    }
    private val bondDetail = SecurityDetailGenerator().build {
        type = TREASURY_MARKET_BOND
    }
    private val noteDetail = SecurityDetailGenerator().build {
        type = TREASURY_MARKET_NOTE
    }
    private val frnDetail = SecurityDetailGenerator().build {
        type = TREASURY_MARKET_FRN
    }
    private val tipsDetail = SecurityDetailGenerator().build {
        type = TREASURY_MARKET_TIPS
    }

    @BeforeTest
    fun before() {
        mockkObject(SecurityPriceTable)
        coEvery { SecurityPriceTable.insert(any(), any()) } returns 1

        mockkObject(TreasuryDirectClient)
        mockkObject(ImporterSecurityAssociationTable)
        coEvery { ImporterSecurityAssociationTable.fetchSecuritiesForImporter(AuctionedImport.importerId) }
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

                AuctionedImport.import()

                coVerify(exactly = 1) { SecurityPriceTable.insert(detail, any()) }
            }
        }
    }

    @Test
    fun `import securities bulk`() {
        runBlocking {
            every { TreasuryDirectClient.auctionedSecurities(0) }
                .returns(listOf(billResponse, bondResponse, noteResponse, frnResponse, tipsResponse))

            AuctionedImport.import()

            coVerify(exactly = 1) { SecurityPriceTable.insert(billDetail, any()) }
            coVerify(exactly = 1) { SecurityPriceTable.insert(bondDetail, any()) }
            coVerify(exactly = 1) { SecurityPriceTable.insert(noteDetail, any()) }
            coVerify(exactly = 1) { SecurityPriceTable.insert(frnDetail, any()) }
            coVerify(exactly = 1) { SecurityPriceTable.insert(tipsDetail, any()) }
        }
    }

}
