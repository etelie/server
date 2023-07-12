package com.etelie.imports.treasury

import kotlinx.datetime.LocalDate
import java.math.BigDecimal

data class SavingsBondScrapeResult(
    val eeBondIssueDate: LocalDate,
    val eeBondFixedRate: BigDecimal,
    val iBondIssueDate: LocalDate,
    val iBondFixedRate: BigDecimal,
    val iBondVariableRate: BigDecimal,
)
