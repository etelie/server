package com.etelie.imports.treasury

import com.etelie.imports.ImporterSecurityAssociationTable
import com.etelie.imports.ImporterSecurityAssociationTable.detailForSerialName
import com.etelie.imports.ImporterSecurityAssociationTable.serialNames
import com.etelie.securities.SecurityType
import com.etelie.securities.detail.SecurityDetail
import com.etelie.securities.price.SecurityPrice
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.datetime.Instant
import java.math.BigDecimal

object AuctionedImport {

    const val importerId = 2

    suspend fun import(): String = coroutineScope {
        val securitiesAuctionedToday = async {
            TreasuryDirectClient.auctionedSecurities(0)
        }
        val associatedSecurities = async { ImporterSecurityAssociationTable.fetchSecuritiesForImporter(importerId) }

        val securityPrices: Map<SecurityDetail, List<SecurityPrice>> = securitiesAuctionedToday.await()
            .filter { security ->
                associatedSecurities.await().serialNames()
                    .contains(security.securityType)
            }
            .groupBy { security ->
                security.securityType
            }
            .mapKeys { (key, _) ->
                associatedSecurities.await().detailForSerialName(key)
                    ?: throw IllegalStateException("Security type not found after filter")
            }
            .mapValues { (key, value) ->
                value.map { security ->
                    asSecurityPrice(key, security)
                }
            }

        // todo: insert into database

        ""
    }

    // todo: implement
    private fun asSecurityPrice(
        securityDetail: SecurityDetail,
        security: TreasuryDirectClient.Security,
    ): SecurityPrice {
        assert(security.highPrice == security.pricePer100)

        val purchasedTimestamp = Instant.parse(security.auctionDate)
        val issuedTimestamp = Instant.parse(security.issueDate)

        return when (securityDetail.type) {
            SecurityType.TREASURY_MARKET_BILL ->
                SecurityPrice(
                    purchasedTimestamp = purchasedTimestamp,
                    issuedTimestamp = issuedTimestamp,
                    term = 0, // todo: weeks and months in db
                    parValue = BigDecimal(100),
                    discountPrice = BigDecimal(security.pricePer100),
                    interestRateFixed = BigDecimal.ZERO,
                    interestRateVariable = BigDecimal.ZERO,
                    yieldToMaturity = BigDecimal.ZERO,
                )

            SecurityType.TREASURY_MARKET_BOND -> TODO()
            SecurityType.TREASURY_MARKET_NOTE -> TODO()
            SecurityType.TREASURY_MARKET_FRN -> TODO()
            SecurityType.TREASURY_MARKET_TIPS -> TODO()
            else -> throw UnsupportedOperationException("Unsupported security type for ${this::class.simpleName}")
        }
    }

}
