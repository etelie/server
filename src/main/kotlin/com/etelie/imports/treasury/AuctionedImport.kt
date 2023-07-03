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
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.math.BigDecimal
import java.time.Period
import kotlin.time.Duration

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
        assert(security.highPrice == security.pricePer100)

        // TODO: confirm UTC assumption
        val purchasedTime = LocalDateTime.parse(security.auctionDate).toInstant(TimeZone.UTC)
        val issuedTime = LocalDateTime.parse(security.issueDate).toInstant(TimeZone.UTC)
        val maturityTimestamp = LocalDateTime.parse(security.maturityDate).toInstant(TimeZone.UTC)
        val termDuration: Duration = maturityTimestamp - issuedTime
        val termPeriod: Period = Period.between(
            issuedTime.toLocalDateTime(TimeZone.UTC).toJavaLocalDateTime().toLocalDate(),
            maturityTimestamp.toLocalDateTime(TimeZone.UTC).toJavaLocalDateTime().toLocalDate(),
        )
        val termWeeks: SecurityTerm = SecurityTerm.weeks(
            (termDuration.inWholeDays.floorDiv(7) +
                if (termDuration.inWholeDays % 7 != 0L) 1 else 0).toInt(),
        )
        val termMonths: SecurityTerm = SecurityTerm.months(
            (termPeriod.toTotalMonths() +
                if (termPeriod.days > 0) 1 else 0).toInt(),
        )
        val parValue = BigDecimal("100")

        return when (securityDetail.type) {
            SecurityType.TREASURY_MARKET_BILL -> {
                assert(security.highDiscountRate.isNotEmpty())
                SecurityPrice(
                    purchasedTimestamp = purchasedTime,
                    issuedTimestamp = issuedTime,
                    term = termWeeks,
                    parValue = parValue,
                    interestRateFixed = BigDecimal(security.highDiscountRate),
                    interestRateVariable = BigDecimal.ZERO,
                )
            }

            SecurityType.TREASURY_MARKET_BOND -> {
                assert(security.interestRate.isNotEmpty())
                SecurityPrice(
                    purchasedTimestamp = purchasedTime,
                    issuedTimestamp = issuedTime,
                    term = termMonths,
                    parValue = parValue,
                    interestRateFixed = BigDecimal(security.interestRate),
                    interestRateVariable = BigDecimal.ZERO,
                )
            }

            SecurityType.TREASURY_MARKET_NOTE -> TODO()
            SecurityType.TREASURY_MARKET_FRN -> TODO()
            SecurityType.TREASURY_MARKET_TIPS -> TODO()
            else -> throw UnsupportedOperationException("Unsupported security type for ${this::class.simpleName}")
        }
    }

}
