package com.etelie.imports.treasury

import com.etelie.application.logger
import com.etelie.imports.ImporterSecurityAssociationTable
import com.etelie.imports.ImporterSecurityAssociationTable.detailForSerialName
import com.etelie.imports.ImporterSecurityAssociationTable.serialNames
import com.etelie.securities.SecurityTerm
import com.etelie.securities.SecurityType
import com.etelie.securities.detail.SecurityDetail
import com.etelie.securities.price.SecurityPrice
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import java.math.BigDecimal
import kotlin.time.Duration

private val log = logger {}

object AuctionedImport {

    const val importerId = 2

    suspend fun import(): String = coroutineScope {
        val securitiesAuctionedToday = async {
            TreasuryDirectClient.auctionedSecurities(4)
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

        securityPrices.values.flatten().joinToString("\n")
    }

    private fun asSecurityPrice(
        securityDetail: SecurityDetail,
        security: TreasuryDirectClient.Security,
    ): SecurityPrice {
        security.apply {
            assert(highPrice == pricePer100)
        }

        // TODO: confirm UTC assumption
        val purchasedTimestamp = LocalDateTime.parse(security.auctionDate).toInstant(TimeZone.UTC)
        val issuedTimestamp = LocalDateTime.parse(security.issueDate).toInstant(TimeZone.UTC)
        val maturityTimestamp = LocalDateTime.parse(security.maturityDate).toInstant(TimeZone.UTC)
        val termDuration: Duration = (maturityTimestamp - issuedTimestamp).apply {
            assert(isPositive())
        }
        val parValue = BigDecimal("100.00000000")

        return when (securityDetail.type) {
            SecurityType.TREASURY_MARKET_BILL ->
                SecurityPrice(
                    purchasedTimestamp = purchasedTimestamp,
                    issuedTimestamp = issuedTimestamp,
                    term = SecurityTerm.weeks((termDuration.inWholeDays.floorDiv(7) + 1).toInt()),
                    parValue = parValue,
                    interestRateFixed = BigDecimal(security.highDiscountRate),
                    interestRateVariable = BigDecimal.ZERO,
                )

            SecurityType.TREASURY_MARKET_BOND -> TODO()
            SecurityType.TREASURY_MARKET_NOTE -> TODO()
            SecurityType.TREASURY_MARKET_FRN -> TODO()
            SecurityType.TREASURY_MARKET_TIPS -> TODO()
            else -> throw UnsupportedOperationException("Unsupported security type for ${this::class.simpleName}")
        }
    }

}
