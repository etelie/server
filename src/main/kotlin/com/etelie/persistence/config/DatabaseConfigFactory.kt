package com.etelie.persistence.config

import com.etelie.application.ExecutionEnvironment
import io.ktor.server.application.ApplicationEnvironment
import kotlin.reflect.full.primaryConstructor

object DatabaseConfigFactory {

    private fun getMap() = mapOf(
        ExecutionEnvironment.DEVELOPMENT to LocalDatabaseConfig::class,
        ExecutionEnvironment.STAGING to StagingRdsDatabaseConfig::class,
        ExecutionEnvironment.PRODUCTION to ProductionRdsDatabaseConfig::class,
    )

    fun fromExecutionEnvironment(
        executionEnvironment: ExecutionEnvironment,
        applicationEnvironment: ApplicationEnvironment,
    ): DatabaseConfig? {
        return getMap()
            .get(executionEnvironment)
            ?.primaryConstructor
            ?.call(applicationEnvironment)
    }

}
