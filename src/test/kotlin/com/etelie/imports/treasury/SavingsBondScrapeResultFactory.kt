package com.etelie.imports.treasury

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.math.BigDecimal

class SavingsBondScrapeResultFactory {
    var eeBondIssueDate = base.eeBondIssueDate
    var eeBondFixedRate = base.eeBondFixedRate
    var iBondIssueDate = base.iBondIssueDate
    var iBondFixedRate = base.iBondFixedRate
    var iBondVariableRate = base.iBondVariableRate

    companion object {
        private val base = SavingsBondScrapeResult(
            eeBondIssueDate = Clock.System.now().toLocalDateTime(TimeZone.UTC).date,
            eeBondFixedRate = BigDecimal.ZERO,
            iBondIssueDate = Clock.System.now().toLocalDateTime(TimeZone.UTC).date,
            iBondFixedRate = BigDecimal.ZERO,
            iBondVariableRate = BigDecimal.ZERO,
        )

        fun build(block: SavingsBondScrapeResultFactory.() -> Unit = {}): SavingsBondScrapeResult {
            return SavingsBondScrapeResultFactory().apply(block).run {
                base.copy(
                    eeBondIssueDate = eeBondIssueDate,
                    eeBondFixedRate = eeBondFixedRate,
                    iBondIssueDate = iBondIssueDate,
                    iBondFixedRate = iBondFixedRate,
                    iBondVariableRate = iBondVariableRate,
                )
            }
        }
    }

}
