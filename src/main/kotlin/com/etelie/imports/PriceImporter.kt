package com.etelie.imports

abstract class PriceImporter {

    private val importerName: String
        get() = this::class.simpleName!!

    fun getSuccessMessage(insertedPricesCount: Int): String {
        return "$importerName completed; $insertedPricesCount prices inserted into security_price table"
    }

    fun getFailureMessage(reason: String): String {
        return "$importerName failed; $reason"
    }

}
