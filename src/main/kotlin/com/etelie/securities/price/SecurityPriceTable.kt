package com.etelie.securities.price

import com.etelie.securities.SecurityTerm
import com.etelie.securities.SecurityType
import com.etelie.securities.detail.SecurityDetailTable
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

object SecurityPriceTable : IntIdTable(
    name = "security_price",
    columnName = "price_id",
) {
    val securityId = reference("security_id", SecurityDetailTable.securityId)
    val purchasedTimestamp = timestamp("purchased_timestamp").nullable()
    val issuedTimestamp = timestamp("issued_timestamp").nullable()
    val termMonths = integer("term_months").nullable()
    val termWeeks = integer("term_weeks").nullable()
    val interestRateFixed = decimal("interest_rate_fixed", 1000, 4)
    val interestRateVariable = decimal("interest_rate_variable", 1000, 4)

    private val coroutineContext = Dispatchers.IO + CoroutineName(this::class.simpleName!!)

    /**
     * Insert a SecurityPrice into the security_price table
     * @return Number of inserted records
     * @see SecurityPrice
     */
    suspend fun insert(
        securityType: SecurityType,
        securityPrice: SecurityPrice,
    ): Int = newSuspendedTransaction(coroutineContext) {
        val termInMonths: Int? = if (securityPrice.term.unit == SecurityTerm.Unit.MONTHS)
            securityPrice.term.length else null
        val termInWeeks: Int? = if (securityPrice.term.unit == SecurityTerm.Unit.WEEKS)
            securityPrice.term.length else null

        val alreadyExistsQuery = selectAll().apply {
            andWhere { securityId eq securityType.persistentId }
            andWhere { issuedTimestamp eq securityPrice.issuedTimestamp }
            andWhere { termMonths eq termInMonths }
            andWhere { termWeeks eq termInWeeks }
        }

        if (!alreadyExistsQuery.empty()) {
            return@newSuspendedTransaction 0
        }

        return@newSuspendedTransaction insert {
            it[securityId] = securityType.persistentId
            it[purchasedTimestamp] = securityPrice.purchasedTimestamp
            it[issuedTimestamp] = securityPrice.issuedTimestamp
            it[termMonths] = termInMonths
            it[termWeeks] = termInWeeks
            it[interestRateFixed] = securityPrice.interestRateFixed
            it[interestRateVariable] = securityPrice.interestRateVariable
        }.insertedCount
    }

}
