package com.etelie.securities.price

import com.etelie.securities.SecurityTerm
import com.etelie.securities.detail.SecurityDetail
import com.etelie.securities.detail.SecurityDetailTable
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

object SecurityPriceTable : IntIdTable(
    name = "security_price",
    columnName = "price_id",
) {
    val securityId = reference("security_id", SecurityDetailTable.securityId)
    val purchasedTimestamp = timestamp("purchased_timestamp")
    val issuedTimestamp = timestamp("issued_timestamp").nullable()
    val termMonths = integer("term_months").nullable()
    val termWeeks = integer("term_weeks").nullable()
    val interestRateFixed = decimal("interest_rate_fixed", 1000, 4)
    val interestRateVariable = decimal("interest_rate_variable", 1000, 4)

    private val coroutineContext = Dispatchers.IO + CoroutineName(this::class.simpleName!!)

    suspend fun insert(
        securityDetail: SecurityDetail,
        securityPrice: SecurityPrice,
    ) = newSuspendedTransaction(coroutineContext) {
        val termInMonths: Int? = if (securityPrice.term.unit == SecurityTerm.Unit.MONTHS)
            securityPrice.term.length else null
        val termInWeeks: Int? = if (securityPrice.term.unit == SecurityTerm.Unit.WEEKS)
            securityPrice.term.length else null

        insert {
            it[securityId] = securityDetail.type.persistentId
            it[purchasedTimestamp] = securityPrice.purchasedTimestamp
            it[issuedTimestamp] = securityPrice.issuedTimestamp
            it[termMonths] = termInMonths
            it[termWeeks] = termInWeeks
            it[interestRateFixed] = securityPrice.interestRateFixed
            it[interestRateVariable] = securityPrice.interestRateVariable
        }
    }

}
