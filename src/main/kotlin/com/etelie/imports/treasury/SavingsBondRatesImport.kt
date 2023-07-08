package com.etelie.imports.treasury

import kotlinx.coroutines.coroutineScope

object SavingsBondRatesImport {

    const val importerId = 3

    suspend fun import(): String = coroutineScope {
        "stub"
    }

}
