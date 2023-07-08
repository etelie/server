package com.etelie.imports

import com.etelie.securities.detail.SecurityDetail
import com.etelie.securities.detail.SecurityDetailTable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

private typealias ImporterSecurityAssociation = Pair<SecurityDetail, String>

object ImporterSecurityAssociationTable : IntIdTable(
    name = "association_importer_security",
    columnName = "association_id",
) {

    val importerId = integer("importer_id")
    val securityId = integer("security_id")
    val securitySerialName = varchar("security_serial_name", 200)

    init {
        uniqueIndex(importerId, securityId)
    }

    suspend fun fetchSecuritiesForImporter(id: Int): Collection<Pair<SecurityDetail, String>> =
        newSuspendedTransaction {
            join(
                SecurityDetailTable,
                JoinType.RIGHT,
                onColumn = securityId,
                otherColumn = SecurityDetailTable.securityId,
                additionalConstraint = {
                    importerId eq id
                },
            )
                .selectAll()
                .toSet()
                .map {
                    Pair(
                        SecurityDetailTable.toSecurityDetail(it),
                        it.get(securitySerialName),
                    )
                }
        }

    fun Collection<ImporterSecurityAssociation>.serialNames(): List<String> {
        return this.map { (_, serialName) ->
            serialName
        }
    }

    fun Collection<ImporterSecurityAssociation>.detailForSerialName(target: String): SecurityDetail? {
        return this.find { (_, serialName) ->
            serialName == target
        }
            ?.first
    }

}
