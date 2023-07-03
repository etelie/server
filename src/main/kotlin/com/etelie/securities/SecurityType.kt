package com.etelie.securities

enum class SecurityType(
    val serialName: String,
) {

    TREASURY_SAVINGS_EE("treasury-savings-ee"),
    TREASURY_SAVINGS_I("treasury-savings-i"),
    TREASURY_MARKET_BILL("treasury-market-bill"),
    TREASURY_MARKET_BOND("treasury-market-bond"),
    TREASURY_MARKET_NOTE("treasury-market-note"),
    TREASURY_MARKET_FRN("treasury-market-frn"),
    TREASURY_MARKET_TIPS("treasury-market-tips");

    companion object {
        fun findBySerialName(serialName: String): SecurityType? {
            return SecurityType.values().find {
                it.serialName == serialName
            }
        }
    }

}
