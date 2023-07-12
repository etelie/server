package com.etelie.securities.detail

import com.etelie.securities.SecurityType

class SecurityDetailFactory private constructor() {

    var type: SecurityType = baseSecurityDetail.type

    companion object {
        private val baseSecurityDetail = SecurityDetail(
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

        fun build(block: SecurityDetailFactory.() -> Unit): SecurityDetail {
            return SecurityDetailFactory().apply(block).run {
                baseSecurityDetail.copy(
                    type = type,
                )
            }
        }
    }

}
