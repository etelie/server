package com.etelie.securities

enum class SecurityType(
    val persistentId: Int,
    val persistentName: String,
) {

    TREASURY_SAVINGS_EE(1, "treasury-savings-ee"),
    TREASURY_SAVINGS_I(2, "treasury-savings-i"),
    TREASURY_MARKET_BILL(3, "treasury-market-bill"),
    TREASURY_MARKET_BOND(4, "treasury-market-bond"),
    TREASURY_MARKET_NOTE(5, "treasury-market-note"),
    TREASURY_MARKET_FRN(6, "treasury-market-frn"),
    TREASURY_MARKET_TIPS(7, "treasury-market-tips");

    companion object {
        fun findByPersistentName(persistentName: String): SecurityType? {
            return SecurityType.values().find {
                it.persistentName == persistentName
            }
        }
    }

}
