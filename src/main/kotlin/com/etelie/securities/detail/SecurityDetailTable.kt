package com.etelie.securities.detail

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow

object SecurityDetailTable : IntIdTable(
    name = "security_detail",
    columnName = "detail_id",
) {
    val securityId = integer("security_id").uniqueIndex()
    val securityName = varchar("security_name", 100).uniqueIndex()
    val securityDescription = varchar("security_description", 500)
    val interestFrequency = integer("interest_frequency")
    val compoundingFrequency = integer("compounding_frequency").nullable().default(null)
    val isCurrent = bool("is_current")
    val isFixedRate = bool("is_fixed_rate")
    val isFixedPar = bool("is_fixed_par")
    val isCompoundable = bool("is_compoundable")
    val isAuctioned = bool("is_auctioned")
    val isMarketable = bool("is_marketable")
    val isCallable = bool("is_callable")
    val isTaxableFederal = bool("is_taxable_federal")
    val isTaxableState = bool("is_taxable_state")
    val isTaxableLocal = bool("is_taxable_local")

    fun toSecurityDetail(row: ResultRow): SecurityDetail {
        return SecurityDetail(
            name = row.get(securityName),
            description = row.get(securityDescription),
            interestFrequency = row.get(interestFrequency),
            compoundingFrequency = row.get(compoundingFrequency),
            isCurrent = row.get(isCurrent),
            isFixedRate = row.get(isFixedRate),
            isFixedPar = row.get(isFixedPar),
            isCompoundable = row.get(isCompoundable),
            isAuctioned = row.get(isAuctioned),
            isMarketable = row.get(isMarketable),
            isCallable = row.get(isCallable),
            isTaxableFederal = row.get(isTaxableFederal),
            isTaxableState = row.get(isTaxableState),
            isTaxableLocal = row.get(isTaxableLocal),
        )
    }
}
