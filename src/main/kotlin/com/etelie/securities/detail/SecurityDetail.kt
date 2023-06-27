package com.etelie.securities.detail

data class SecurityDetail(
    val name: String,
    val description: String,
    val interestFrequency: Int,
    val compoundingFrequency: Int?,
    val isCurrent: Boolean,
    val isFixedRate: Boolean,
    val isFixedPar: Boolean,
    val isCompoundable: Boolean,
    val isAuctioned: Boolean,
    val isMarketable: Boolean,
    val isCallable: Boolean,
    val isTaxableFederal: Boolean,
    val isTaxableState: Boolean,
    val isTaxableLocal: Boolean,
)
