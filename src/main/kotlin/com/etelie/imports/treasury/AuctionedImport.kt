package com.etelie.imports.treasury

import com.etelie.application.logger
import com.etelie.imports.ImporterSecurityAssociationTable
import com.etelie.imports.ImporterSecurityAssociationTable.detailForSerialName
import com.etelie.imports.ImporterSecurityAssociationTable.serialNames
import com.etelie.securities.SecurityTerm
import com.etelie.securities.SecurityType
import com.etelie.securities.detail.SecurityDetail
import com.etelie.securities.price.SecurityPrice
import com.etelie.securities.price.SecurityPriceTable
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.math.BigDecimal

private val log = logger {}

object AuctionedImport {

    const val importerId = 2

    suspend fun import(): String = coroutineScope {
        val securitiesAuctionedToday = async {
            TreasuryDirectClient.auctionedSecurities(16)
        }
        val associatedSecurities = async { ImporterSecurityAssociationTable.fetchSecuritiesForImporter(importerId) }

        val securityPrices: Map<SecurityDetail, List<SecurityPrice>> = securitiesAuctionedToday.await()
            .filter { security ->
                associatedSecurities.await().serialNames()
                    .contains(security.type)
            }
            .groupBy { security ->
                security.type
            }
            .mapKeys { (securityType, _) ->
                associatedSecurities.await().detailForSerialName(securityType)
                    ?: throw IllegalStateException("Security type not found after filter")
            }
            .mapValues { (securityDetail, securities) ->
                securities.map { security ->
                    SecurityPriceConverter.findConverter(securityDetail.type)?.let { converter ->
                        converter.convert(security)
                    } ?: throw UnsupportedOperationException("Unsupported security type for ${this::class.simpleName}")
                }
            }

        securityPrices.entries.forEach { (detail, prices) ->
            prices.forEach { price ->
                SecurityPriceTable.insert(detail, price)
            }
        }

        securityPrices.values.flatten().joinToString("\n")
    }

    private enum class SecurityPriceConverter(
        val securityType: SecurityType,
        val convert: (security: TreasuryDirectClient.Security) -> SecurityPrice,
    ) {

        BILL(
            SecurityType.TREASURY_MARKET_BILL,
            { security ->
                assert(security.floatingRate == "No")
                assert(security.tips == "No")
                assert(security.highDiscountRate.isNotEmpty())
                SecurityPrice(
                    purchasedTimestamp = security.getPurchasedTimestamp(),
                    issuedTimestamp = security.getIssuedTimestamp(),
                    term = SecurityTerm.weeks(security.getTermInWeeks()),
                    interestRateFixed = BigDecimal(security.highDiscountRate),
                    interestRateVariable = BigDecimal.ZERO,
                )
            },
        ),
        BOND(
            SecurityType.TREASURY_MARKET_BOND,
            { security ->
                assert(security.floatingRate == "No")
                assert(security.tips == "No")
                assert(security.interestRate.isNotEmpty())
                SecurityPrice(
                    purchasedTimestamp = security.getPurchasedTimestamp(),
                    issuedTimestamp = security.getIssuedTimestamp(),
                    term = SecurityTerm.months(security.getTermInMonths()),
                    interestRateFixed = BigDecimal(security.interestRate),
                    interestRateVariable = BigDecimal.ZERO,
                )
            },
        ),
        NOTE(
            SecurityType.TREASURY_MARKET_NOTE,
            { security ->
                assert(security.floatingRate == "No")
                assert(security.tips == "No")
                assert(security.interestRate.isNotEmpty())
                SecurityPrice(
                    purchasedTimestamp = security.getPurchasedTimestamp(),
                    issuedTimestamp = security.getIssuedTimestamp(),
                    term = SecurityTerm.months(security.getTermInMonths()),
                    interestRateFixed = BigDecimal(security.interestRate),
                    interestRateVariable = BigDecimal.ZERO,
                )
            },
        ),
        FRN(
            SecurityType.TREASURY_MARKET_FRN,
            { security ->
                assert(security.floatingRate == "Yes")
                assert(security.tips == "No")
                assert(security.spread.isNotEmpty())
                assert(security.frnIndexDeterminationRate.isNotEmpty())
                SecurityPrice(
                    purchasedTimestamp = security.getPurchasedTimestamp(),
                    issuedTimestamp = security.getIssuedTimestamp(),
                    term = SecurityTerm.months(security.getTermInMonths()),
                    interestRateFixed = BigDecimal(security.spread),
                    interestRateVariable = BigDecimal(security.frnIndexDeterminationRate),
                )
            },
        ),
        TIPS(
            SecurityType.TREASURY_MARKET_TIPS,
            { security ->
                assert(security.floatingRate == "No")
                assert(security.tips == "Yes")
                assert(security.interestRate.isNotEmpty())
                SecurityPrice(
                    purchasedTimestamp = security.getPurchasedTimestamp(),
                    issuedTimestamp = security.getIssuedTimestamp(),
                    term = SecurityTerm.months(security.getTermInMonths()),
                    interestRateFixed = BigDecimal(security.interestRate),
                    interestRateVariable = BigDecimal.ZERO,
                )
            },
        );

        companion object {
            fun findConverter(type: SecurityType): SecurityPriceConverter? {
                return SecurityPriceConverter.values()
                    .find {
                        it.securityType == type
                    }
            }
        }

    }

}
