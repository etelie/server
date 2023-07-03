package com.etelie.securities.price

import com.etelie.securities.SecurityTerm
import kotlinx.datetime.Instant
import java.math.BigDecimal

data class SecurityPrice(
    val purchasedTimestamp: Instant,
    val issuedTimestamp: Instant?,
    val term: SecurityTerm,
    val parValue: BigDecimal?,
    val interestRateFixed: BigDecimal,
    val interestRateVariable: BigDecimal,
)
