package com.etelie.securities.detail

import org.jetbrains.exposed.dao.id.IntIdTable

object SecurityDetailTable : IntIdTable(
    name = "security_detail",
    columnName = "detail_id"
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
}
