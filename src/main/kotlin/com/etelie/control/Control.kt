package com.etelie.control

import com.etelie.persistence.Transactions.suspendedTransaction

enum class Control(
    val identifier: String,
) {

    STATUS("status");

    suspend fun fetchEntity(): ControlEntity? = suspendedTransaction {
        ControlEntity.find { ControlTable.identifier eq identifier }.firstOrNull()
    }

}
