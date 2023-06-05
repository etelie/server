package com.etelie.control

import com.etelie.common.Transactions.suspendedTransaction

enum class Controls(
    val identifier: String,
) {

    STATUS("status");

    suspend fun fetchEntity(): ControlEntity? = suspendedTransaction {
        ControlEntity.find { ControlTable.identifier eq identifier }.firstOrNull()
    }

}
