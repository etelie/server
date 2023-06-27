package com.etelie.securities.price

import kotlinx.datetime.Instant
import java.math.BigDecimal

data class SecurityPrice(
    val purchasedTimestamp: Instant,
    val issuedTimestamp: Instant?,
    val term: Int,
    val parValue: BigDecimal?,
    val discountPrice: BigDecimal?,
    val interestRateFixed: BigDecimal,
    val interestRateVariable: BigDecimal,
    val yieldToMaturity: BigDecimal,
)
