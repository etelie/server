package com.etelie.securities.detail

import com.etelie.securities.note.SecurityNoteEntity
import com.etelie.securities.note.SecurityNoteTable
import com.etelie.securities.price.SecurityPriceEntity
import com.etelie.securities.price.SecurityPriceTable
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID

class SecurityDetailEntity(id: EntityID<Int>) : Entity<Int>(id) {
    companion object : EntityClass<Int, SecurityDetailEntity>(SecurityDetailTable)

    val detailId by SecurityDetailTable.id
    val securityId by SecurityDetailTable.securityId
    val securityName by SecurityDetailTable.securityName
    val securityDescription by SecurityDetailTable.securityDescription
    val interestFrequency by SecurityDetailTable.interestFrequency
    val compoundingFrequency by SecurityDetailTable.compoundingFrequency
    val isCurrent by SecurityDetailTable.isCurrent
    val isFixedRate by SecurityDetailTable.isFixedRate
    val isFixedPar by SecurityDetailTable.isFixedPar
    val isCompoundable by SecurityDetailTable.isCompoundable
    val isAuctioned by SecurityDetailTable.isAuctioned
    val isMarketable by SecurityDetailTable.isMarketable
    val isCallable by SecurityDetailTable.isCallable
    val isTaxableFederal by SecurityDetailTable.isTaxableFederal
    val isTaxableState by SecurityDetailTable.isTaxableState
    val isTaxableLocal by SecurityDetailTable.isTaxableLocal

    val notes by SecurityNoteEntity referrersOn SecurityNoteTable.securityId
    val prices by SecurityPriceEntity referrersOn SecurityPriceTable.securityId
}
