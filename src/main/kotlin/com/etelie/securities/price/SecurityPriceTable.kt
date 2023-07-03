package com.etelie.securities.price

import com.etelie.securities.detail.SecurityDetailTable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object SecurityPriceTable : IntIdTable(
    name = "security_price",
    columnName = "price_id"
) {
    val securityId = reference("security_id", SecurityDetailTable.securityId)
    val purchasedTimestamp = timestamp("purchased_timestamp")
    val issuedTimestamp = timestamp("issued_timestamp").nullable()
    val termMonths = integer("term_months").nullable()
    val termWeeks = integer("term_weeks").nullable()
    val parValue = decimal("par_value", 1000, 4).nullable()
    val discountPrice = decimal("discount_price", 1000, 4).nullable()
    val interestRateFixed = decimal("interest_rate_fixed", 1000, 4)
    val interestRateVariable = decimal("interest_rate_variable", 1000, 4)
}
