package com.etelie.persistence.database

import com.etelie.application.ExecutionEnvironment
import kotlin.reflect.full.primaryConstructor

object DatabaseConfigFactory {

    private fun getMap() = mapOf(
        ExecutionEnvironment.DEVELOPMENT to LocalDatabaseConfig::class,
        ExecutionEnvironment.STAGING to StagingRdsDatabaseConfig::class,
        ExecutionEnvironment.PRODUCTION to ProductionRdsDatabaseConfig::class,
    )

    fun fromExecutionEnvironment(
        executionEnvironment: ExecutionEnvironment,
    ): DatabaseConfig? {
        return getMap()
            .get(executionEnvironment)
            ?.primaryConstructor
            ?.call(ExecutionEnvironment.applicationEnvironment)
    }

}
