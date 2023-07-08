package com.etelie.control

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

enum class Control(
    val identifier: String,
) {

    STATUS("status");

    suspend fun fetchEntity(): ControlEntity? = newSuspendedTransaction(coroutineContext) {
        ControlEntity.find { ControlTable.identifier eq identifier }.firstOrNull()
    }

    companion object {
        val coroutineContext = Dispatchers.IO + CoroutineName(Control::class.simpleName!!)
    }

}
