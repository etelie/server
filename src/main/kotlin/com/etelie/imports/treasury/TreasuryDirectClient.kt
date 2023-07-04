package com.etelie.imports.treasury

import com.etelie.network.addAllQueries
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.http4k.client.ApacheClient
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request
import java.math.BigDecimal
import java.time.Period
import kotlin.time.Duration

object TreasuryDirectClient {
    private const val scheme: String = "https"
    private const val host: String = "treasurydirect.gov"
    private const val basePath: String = "/TA_WS/securities"
    private val client: HttpHandler = ApacheClient()
    private val baseParams: Map<String, String> = mapOf(
        "format" to "json",
        "pagesize" to "1000",
    )
    private val json = Json {
        ignoreUnknownKeys = true
    }

    fun auctionedSecurities(days: Int): List<Security> {
        val path = "$basePath/auctioned"
        val request = Request(Method.GET, "$scheme://$host$path")
            .addAllQueries(baseParams)
            .addAllQueries(
                mapOf(
                    "days" to "$days",
                ),
            )

        val responseBody = client(request).bodyString()
        return json.decodeFromString<List<Security>>(responseBody)
    }

    @Serializable
    data class Security(
        val cusip: String, // "912797GA9",
        val issueDate: String, // "2023-07-05T00:00:00",
        val securityType: String, // WARN: USE type INSTEAD ("Note" is overloaded in securityType field)
        val securityTerm: String, // "4-Week",
        val maturityDate: String, // "2023-08-01T00:00:00",
        val interestRate: String, // "",
        val refCpiOnIssueDate: String, // "",
        val refCpiOnDatedDate: String, // "",
        val announcementDate: String, // "2023-06-27T00:00:00",
        val auctionDate: String, // "2023-06-29T00:00:00",
        val auctionDateYear: String, // "2023",
        val datedDate: String, // "",
        val accruedInterestPer1000: String, // "",
        val accruedInterestPer100: String, // "",
        val adjustedAccruedInterestPer1000: String, // "",
        val adjustedPrice: String, // "",
        val allocationPercentage: String, // "41.370000",
        val allocationPercentageDecimals: String, // "2",
        val announcedCusip: String, // "",
        val auctionFormat: String, // "Single-Price",
        val averageMedianDiscountRate: String, // "5.015000",
        val averageMedianInvestmentRate: String, // "",
        val averageMedianPrice: String, // "",
        val averageMedianDiscountMargin: String, // "",
        val averageMedianYield: String, // "",
        val backDated: String, // "",
        val backDatedDate: String, // "",
        val bidToCoverRatio: String, // "2.530000",
        val callDate: String, // "",
        val callable: String, // "",
        val calledDate: String, // "",
        val cashManagementBillCMB: String, // "No",
        val closingTimeCompetitive: String, // "11:30 AM",
        val closingTimeNoncompetitive: String, // "11:00 AM",
        val competitiveAccepted: String, // "66804501000",
        val competitiveBidDecimals: String, // "3",
        val competitiveTendered: String, // "174106691000",
        val competitiveTendersAccepted: String, // "Yes",
        val corpusCusip: String, // "",
        val cpiBaseReferencePeriod: String, // "",
        val currentlyOutstanding: String, // "72436000000.000000",
        val directBidderAccepted: String, // "1975275000",
        val directBidderTendered: String, // "6565000000",
        val estimatedAmountOfPubliclyHeldMaturingSecuritiesByType: String, // "115948000000",
        val fimaIncluded: String, // "Yes",
        val fimaNoncompetitiveAccepted: String, // "0",
        val fimaNoncompetitiveTendered: String, // "0",
        val firstInterestPeriod: String, // "",
        val firstInterestPaymentDate: String, // "",
        val floatingRate: String, // "No",
        val frnIndexDeterminationDate: String, // "",
        val frnIndexDeterminationRate: String, // "",
        val highDiscountRate: String, // "5.085000",
        val highInvestmentRate: String, // "5.190000",
        val highPrice: String, // "99.618625",
        val highDiscountMargin: String, // "",
        val highYield: String, // "",
        val indexRatioOnIssueDate: String, // "",
        val indirectBidderAccepted: String, // "35976691000",
        val indirectBidderTendered: String, // "39976691000",
        val interestPaymentFrequency: String, // "None",
        val lowDiscountRate: String, // "4.800000",
        val lowInvestmentRate: String, // "",
        val lowPrice: String, // "",
        val lowDiscountMargin: String, // "",
        val lowYield: String, // "",
        val maturingDate: String, // "2023-07-05T00:00:00",
        val maximumCompetitiveAward: String, // "24500000000",
        val maximumNoncompetitiveAward: String, // "10000000",
        val maximumSingleBid: String, // "24500000000",
        val minimumBidAmount: String, // "100",
        val minimumStripAmount: String, // "",
        val minimumToIssue: String, // "100",
        val multiplesToBid: String, // "100",
        val multiplesToIssue: String, // "100",
        val nlpExclusionAmount: String, // "24900000000",
        val nlpReportingThreshold: String, // "24500000000",
        val noncompetitiveAccepted: String, // "3195543300",
        val noncompetitiveTendersAccepted: String, // "Yes",
        val offeringAmount: String, // "70000000000",
        val originalCusip: String, // "",
        val originalDatedDate: String, // "",
        val originalIssueDate: String, // "2023-04-04T00:00:00",
        val originalSecurityTerm: String, // "17-Week",
        val pdfFilenameAnnouncement: String, // "A_20230627_3.pdf",
        val pdfFilenameCompetitiveResults: String, // "R_20230629_2.pdf",
        val pdfFilenameNoncompetitiveResults: String, // "NCR_20230629_2.pdf",
        val pdfFilenameSpecialAnnouncement: String, // "",
        val pricePer100: String, // "99.618625",
        val primaryDealerAccepted: String, // "28852535000",
        val primaryDealerTendered: String, // "127565000000",
        val reopening: String, // "Yes",
        val securityTermDayMonth: String, // "27-Day",
        val securityTermWeekYear: String, // "4-Week",
        val series: String, // "",
        val somaAccepted: String, // "786630400",
        val somaHoldings: String, // "2369000000",
        val somaIncluded: String, // "No",
        val somaTendered: String, // "786630400",
        val spread: String, // "",
        val standardInterestPaymentPer1000: String, // "",
        val strippable: String, // "",
        val term: String, // "4-Week",
        val tiinConversionFactorPer1000: String, // "",
        val tips: String, // "No",
        val totalAccepted: String, // "70786674700",
        val totalTendered: String, // "178088864700",
        val treasuryRetailAccepted: String, // "2624141300",
        val treasuryRetailTendersAccepted: String, // "Yes",
        val type: String, // "Bill",
        val unadjustedAccruedInterestPer1000: String, // "",
        val unadjustedPrice: String, // "",
        val updatedTimestamp: String, // "2023-06-29T11:34:22",
        val xmlFilenameAnnouncement: String, // "A_20230627_3.xml",
        val xmlFilenameCompetitiveResults: String, // "R_20230629_2.xml",
        val xmlFilenameSpecialAnnouncement: String, // "",
    ) {

        private fun getTermDuration(): Duration = getMaturityTimestamp() - getIssuedTimestamp()

        private fun getTermPeriod(): Period = Period.between(
            getIssuedTimestamp().toLocalDateTime(TimeZone.UTC).toJavaLocalDateTime().toLocalDate(),
            getMaturityTimestamp().toLocalDateTime(TimeZone.UTC).toJavaLocalDateTime().toLocalDate(),
        )

        private fun getMaturityTimestamp(): Instant = LocalDateTime.parse(maturityDate).toInstant(TimeZone.UTC)

        // TODO: confirm UTC assumption
        fun getPurchasedTimestamp(): Instant = LocalDateTime.parse(auctionDate).toInstant(TimeZone.UTC)

        fun getIssuedTimestamp(): Instant = LocalDateTime.parse(issueDate).toInstant(TimeZone.UTC)

        fun getTermInWeeks(): Int {
            val termDuration = getTermDuration()
            val weeks = termDuration.inWholeDays.floorDiv(7) +
                if (termDuration.inWholeDays % 7 != 0L) 1 else 0
            return weeks.toInt()
        }

        fun getTermInMonths(): Int {
            val termPeriod = getTermPeriod()
            val months = termPeriod.toTotalMonths() +
                if (termPeriod.days > 0) 1 else 0
            return months.toInt()
        }

        fun getParValue(): BigDecimal = BigDecimal(100)

    }

}
