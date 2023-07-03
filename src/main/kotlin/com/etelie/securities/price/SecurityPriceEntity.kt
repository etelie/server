package com.etelie.securities.price

import com.etelie.securities.detail.SecurityDetailEntity
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID

class SecurityPriceEntity(priceId: EntityID<Int>) : Entity<Int>(priceId) {
    companion object : EntityClass<Int, SecurityPriceEntity>(SecurityPriceTable)

    val priceId by SecurityPriceTable.id
    val securityId by SecurityPriceTable.securityId
    val purchasedTimestamp by SecurityPriceTable.purchasedTimestamp
    val issuedTimestamp by SecurityPriceTable.issuedTimestamp
    val termMonths by SecurityPriceTable.termMonths
    val termWeeks by SecurityPriceTable.termWeeks
    val parValue by SecurityPriceTable.parValue
    val discountPrice by SecurityPriceTable.discountPrice
    val interestRateFixed by SecurityPriceTable.interestRateFixed
    val interestRateVariable by SecurityPriceTable.interestRateVariable
    val yieldToMaturity by SecurityPriceTable.yieldToMaturity

    val securityDetail by SecurityDetailEntity referencedOn SecurityPriceTable.securityId
}
