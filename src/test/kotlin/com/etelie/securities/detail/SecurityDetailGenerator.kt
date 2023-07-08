package com.etelie.securities.detail

import com.etelie.securities.SecurityType

class SecurityDetailGenerator {

    var type: SecurityType = SecurityType.TREASURY_MARKET_BILL

    fun build(block: SecurityDetailGenerator.() -> Unit): SecurityDetail {
        this.apply(block)
        return baseSecurityDetail.copy(
            type = type,
        )
    }

    companion object {
        private var baseSecurityDetail = SecurityDetail(
            type = SecurityType.TREASURY_MARKET_BILL,
            description = "",
            interestFrequency = 1,
            compoundingFrequency = 1,
            isCurrent = true,
            isFixedRate = true,
            isFixedPar = true,
            isCompoundable = true,
            isAuctioned = true,
            isMarketable = true,
            isCallable = false,
            isTaxableFederal = true,
            isTaxableState = false,
            isTaxableLocal = false,
        )
    }

}
